package com.nihongodev.platform.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class QuizResult {

    private UUID id;
    private UUID attemptId;
    private UUID quizId;
    private UUID userId;
    private int totalQuestions;
    private int correctAnswers;
    private double totalScore;
    private double maxPossibleScore;
    private double percentage;
    private boolean passed;
    private int maxStreak;
    private double averageTimePerQuestion;
    private DifficultyLevel difficultyReached;
    private LocalDateTime completedAt;

    public QuizResult() {}

    public static QuizResult calculate(UUID attemptId, UUID quizId, UUID userId,
                                       int totalQuestions, int correctAnswers,
                                       double totalScore, double maxPossibleScore,
                                       int passingScore, int maxStreak,
                                       double avgTime, DifficultyLevel difficultyReached) {
        QuizResult result = new QuizResult();
        result.id = UUID.randomUUID();
        result.attemptId = attemptId;
        result.quizId = quizId;
        result.userId = userId;
        result.totalQuestions = totalQuestions;
        result.correctAnswers = correctAnswers;
        result.totalScore = totalScore;
        result.maxPossibleScore = maxPossibleScore;
        result.percentage = maxPossibleScore > 0 ? (totalScore / maxPossibleScore) * 100 : 0;
        result.passed = result.percentage >= passingScore;
        result.maxStreak = maxStreak;
        result.averageTimePerQuestion = avgTime;
        result.difficultyReached = difficultyReached;
        result.completedAt = LocalDateTime.now();
        return result;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getAttemptId() { return attemptId; }
    public void setAttemptId(UUID attemptId) { this.attemptId = attemptId; }
    public UUID getQuizId() { return quizId; }
    public void setQuizId(UUID quizId) { this.quizId = quizId; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }
    public int getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(int correctAnswers) { this.correctAnswers = correctAnswers; }
    public double getTotalScore() { return totalScore; }
    public void setTotalScore(double totalScore) { this.totalScore = totalScore; }
    public double getMaxPossibleScore() { return maxPossibleScore; }
    public void setMaxPossibleScore(double maxPossibleScore) { this.maxPossibleScore = maxPossibleScore; }
    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }
    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }
    public int getMaxStreak() { return maxStreak; }
    public void setMaxStreak(int maxStreak) { this.maxStreak = maxStreak; }
    public double getAverageTimePerQuestion() { return averageTimePerQuestion; }
    public void setAverageTimePerQuestion(double averageTimePerQuestion) { this.averageTimePerQuestion = averageTimePerQuestion; }
    public DifficultyLevel getDifficultyReached() { return difficultyReached; }
    public void setDifficultyReached(DifficultyLevel difficultyReached) { this.difficultyReached = difficultyReached; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
