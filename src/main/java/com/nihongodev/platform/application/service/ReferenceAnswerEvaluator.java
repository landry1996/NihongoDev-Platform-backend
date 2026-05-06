package com.nihongodev.platform.application.service;

import com.nihongodev.platform.domain.model.InterviewFeedback;
import com.nihongodev.platform.domain.model.InterviewQuestion;

import java.util.*;

public class ReferenceAnswerEvaluator implements AnswerEvaluator {

    @Override
    public InterviewFeedback evaluate(InterviewQuestion question, String answerText, int timeSpentSeconds) {
        String modelAnswer = question.getModelAnswer();
        if (modelAnswer == null || modelAnswer.isBlank()) {
            return new KeywordBasedEvaluator().evaluate(question, answerText, timeSpentSeconds);
        }

        double similarity = calculateSimilarity(answerText, modelAnswer);
        double technicalScore = similarity * 100;
        double languageScore = evaluateLanguageQuality(answerText);
        double communicationScore = evaluateStructure(answerText);
        double culturalScore = evaluatePoliteness(answerText);

        List<String> strengths = new ArrayList<>();
        List<String> improvements = new ArrayList<>();

        if (similarity >= 0.6) strengths.add("Answer closely matches expected response");
        if (similarity >= 0.8) strengths.add("Excellent coverage of key points");
        if (similarity < 0.4) improvements.add("Review the model answer for missing concepts");
        if (similarity < 0.6) improvements.add("Try to cover more aspects of the expected response");

        List<String> grammarNotes = new ArrayList<>();
        List<String> vocabularySuggestions = extractVocabSuggestions(modelAnswer, answerText);

        return InterviewFeedback.create(
                null, languageScore, technicalScore, communicationScore, culturalScore,
                strengths, improvements, modelAnswer, grammarNotes, vocabularySuggestions
        );
    }

    private double calculateSimilarity(String answer, String reference) {
        Set<String> answerWords = new HashSet<>(Arrays.asList(answer.toLowerCase().split("\\s+")));
        Set<String> refWords = new HashSet<>(Arrays.asList(reference.toLowerCase().split("\\s+")));

        if (refWords.isEmpty()) return 0;

        Set<String> intersection = new HashSet<>(answerWords);
        intersection.retainAll(refWords);

        Set<String> union = new HashSet<>(answerWords);
        union.addAll(refWords);

        return union.isEmpty() ? 0 : (double) intersection.size() / union.size();
    }

    private double evaluateLanguageQuality(String answer) {
        double score = 50.0;
        if (answer.length() > 50) score += 20;
        if (answer.contains("です") || answer.contains("ます")) score += 15;
        if (!answer.contains("  ")) score += 15;
        return Math.min(score, 100);
    }

    private double evaluateStructure(String answer) {
        double score = 50.0;
        long sentences = answer.chars().filter(c -> c == '。' || c == '.').count();
        if (sentences >= 2) score += 20;
        if (sentences >= 4) score += 10;
        if (answer.length() >= 100) score += 20;
        return Math.min(score, 100);
    }

    private double evaluatePoliteness(String answer) {
        double score = 50.0;
        if (answer.contains("です") || answer.contains("ます")) score += 20;
        if (answer.contains("ございます")) score += 15;
        if (answer.contains("させていただ")) score += 15;
        return Math.min(score, 100);
    }

    private List<String> extractVocabSuggestions(String modelAnswer, String userAnswer) {
        Set<String> modelWords = new HashSet<>(Arrays.asList(modelAnswer.split("\\s+")));
        Set<String> userWords = new HashSet<>(Arrays.asList(userAnswer.split("\\s+")));
        modelWords.removeAll(userWords);

        List<String> suggestions = new ArrayList<>();
        int count = 0;
        for (String word : modelWords) {
            if (word.length() > 2 && count < 5) {
                suggestions.add(word);
                count++;
            }
        }
        return suggestions;
    }
}
