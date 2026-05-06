package com.nihongodev.platform.application.service;

import com.nihongodev.platform.domain.model.InterviewFeedback;
import com.nihongodev.platform.domain.model.InterviewQuestion;

import java.util.ArrayList;
import java.util.List;

public class StructureEvaluator implements AnswerEvaluator {

    @Override
    public InterviewFeedback evaluate(InterviewQuestion question, String answerText, int timeSpentSeconds) {
        double structureScore = evaluateSTAR(answerText);
        double languageScore = evaluateLanguage(answerText);
        double communicationScore = evaluateClarity(answerText, timeSpentSeconds);
        double culturalScore = evaluateFormality(answerText);

        List<String> strengths = new ArrayList<>();
        List<String> improvements = new ArrayList<>();
        List<String> grammarNotes = new ArrayList<>();

        if (structureScore >= 70) strengths.add("Good answer structure (STAR method detected)");
        if (hasIntroduction(answerText)) strengths.add("Clear introduction provided");
        if (hasConclusion(answerText)) strengths.add("Answer ends with a clear conclusion");

        if (structureScore < 50) improvements.add("Use STAR method: Situation → Task → Action → Result");
        if (!hasIntroduction(answerText)) improvements.add("Start with context/situation before details");
        if (!hasConclusion(answerText)) improvements.add("End with the result or what you learned");
        if (answerText.length() < 100) improvements.add("Elaborate more — aim for 3-5 sentences minimum");

        return InterviewFeedback.create(
                null, languageScore, structureScore, communicationScore, culturalScore,
                strengths, improvements, question.getModelAnswer(), grammarNotes, List.of()
        );
    }

    private double evaluateSTAR(String answer) {
        double score = 30.0;
        String lower = answer.toLowerCase();

        if (lower.contains("状況") || lower.contains("situation") || lower.contains("当時")) score += 15;
        if (lower.contains("課題") || lower.contains("task") || lower.contains("目標")) score += 15;
        if (lower.contains("行動") || lower.contains("action") || lower.contains("実行")) score += 15;
        if (lower.contains("結果") || lower.contains("result") || lower.contains("成果")) score += 15;

        long sentences = answer.chars().filter(c -> c == '。' || c == '.').count();
        if (sentences >= 3) score += 10;

        return Math.min(score, 100);
    }

    private double evaluateLanguage(String answer) {
        double score = 50.0;
        if (answer.contains("です") || answer.contains("ます")) score += 20;
        if (answer.length() > 100) score += 15;
        if (answer.contains("。")) score += 15;
        return Math.min(score, 100);
    }

    private double evaluateClarity(String answer, int timeSpentSeconds) {
        double score = 50.0;
        if (answer.length() >= 80 && answer.length() <= 600) score += 25;
        if (timeSpentSeconds >= 30 && timeSpentSeconds <= 180) score += 15;
        long sentences = answer.chars().filter(c -> c == '。' || c == '.').count();
        if (sentences >= 2 && sentences <= 8) score += 10;
        return Math.min(score, 100);
    }

    private double evaluateFormality(String answer) {
        double score = 50.0;
        if (answer.contains("です") || answer.contains("ます")) score += 20;
        if (answer.contains("ございます") || answer.contains("いただ")) score += 15;
        if (answer.contains("と思います") || answer.contains("考えております")) score += 15;
        return Math.min(score, 100);
    }

    private boolean hasIntroduction(String answer) {
        String lower = answer.toLowerCase();
        return lower.startsWith("私は") || lower.startsWith("はい") || lower.startsWith("以前")
                || lower.contains("について") || answer.length() > 50;
    }

    private boolean hasConclusion(String answer) {
        String lower = answer.toLowerCase();
        return lower.contains("結果") || lower.contains("学び") || lower.contains("成長")
                || lower.contains("result") || lower.endsWith("ます。") || lower.endsWith("した。");
    }
}
