package com.nihongodev.platform.application.service;

import com.nihongodev.platform.domain.model.InterviewFeedback;
import com.nihongodev.platform.domain.model.InterviewQuestion;

import java.util.ArrayList;
import java.util.List;

public class KeywordBasedEvaluator implements AnswerEvaluator {

    @Override
    public InterviewFeedback evaluate(InterviewQuestion question, String answerText, int timeSpentSeconds) {
        List<String> expectedKeywords = question.getExpectedKeywords();
        if (expectedKeywords == null || expectedKeywords.isEmpty()) {
            return buildDefaultFeedback(question, answerText);
        }

        String normalizedAnswer = answerText.toLowerCase().trim();
        int matchedKeywords = 0;
        List<String> foundKeywords = new ArrayList<>();
        List<String> missedKeywords = new ArrayList<>();

        for (String keyword : expectedKeywords) {
            if (normalizedAnswer.contains(keyword.toLowerCase())) {
                matchedKeywords++;
                foundKeywords.add(keyword);
            } else {
                missedKeywords.add(keyword);
            }
        }

        double keywordRatio = (double) matchedKeywords / expectedKeywords.size();
        double technicalScore = Math.min(keywordRatio * 100, 100);
        double languageScore = evaluateLanguage(answerText);
        double communicationScore = evaluateCommunication(answerText, timeSpentSeconds);
        double culturalScore = evaluateCultural(answerText);

        List<String> strengths = new ArrayList<>();
        List<String> improvements = new ArrayList<>();

        if (!foundKeywords.isEmpty()) {
            strengths.add("Keywords mentioned: " + String.join(", ", foundKeywords));
        }
        if (answerText.length() > 50) {
            strengths.add("Detailed response provided");
        }
        if (!missedKeywords.isEmpty()) {
            improvements.add("Missing key concepts: " + String.join(", ", missedKeywords));
        }
        if (answerText.length() < 30) {
            improvements.add("Response too short — elaborate more");
        }

        return InterviewFeedback.create(
                null, languageScore, technicalScore, communicationScore, culturalScore,
                strengths, improvements, question.getModelAnswer(),
                List.of(), List.of()
        );
    }

    private double evaluateLanguage(String answer) {
        double score = 50.0;
        if (answer.length() > 30) score += 15;
        if (answer.contains("です") || answer.contains("ます")) score += 20;
        if (answer.contains("と思います") || answer.contains("考えています")) score += 15;
        return Math.min(score, 100);
    }

    private double evaluateCommunication(String answer, int timeSpentSeconds) {
        double score = 60.0;
        if (answer.length() >= 50 && answer.length() <= 500) score += 20;
        if (timeSpentSeconds <= 120) score += 10;
        if (answer.contains("。")) score += 10;
        return Math.min(score, 100);
    }

    private double evaluateCultural(String answer) {
        double score = 50.0;
        if (answer.contains("よろしくお願いします") || answer.contains("失礼します")) score += 25;
        if (answer.contains("させていただ")) score += 15;
        if (answer.contains("ございます")) score += 10;
        return Math.min(score, 100);
    }

    private InterviewFeedback buildDefaultFeedback(InterviewQuestion question, String answerText) {
        double baseScore = answerText.length() > 30 ? 60.0 : 40.0;
        return InterviewFeedback.create(
                null, baseScore, baseScore, baseScore, baseScore,
                List.of("Answer provided"), List.of("Add more details for higher score"),
                question.getModelAnswer(), List.of(), List.of()
        );
    }
}
