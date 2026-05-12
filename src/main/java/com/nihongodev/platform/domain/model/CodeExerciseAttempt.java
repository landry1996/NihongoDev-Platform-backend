package com.nihongodev.platform.domain.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class CodeExerciseAttempt {

    private UUID id;
    private UUID userId;
    private UUID exerciseId;
    private ExerciseType exerciseType;
    private String userResponse;
    private CodeExerciseScore score;
    private List<TechnicalJapaneseViolation> violations;
    private CommitMessageAnalysis commitAnalysis;
    private String feedback;
    private int timeSpentSeconds;
    private LocalDateTime attemptedAt;

    public CodeExerciseAttempt() {}

    public static CodeExerciseAttempt create(UUID userId, UUID exerciseId, ExerciseType exerciseType,
                                             String userResponse, int timeSpentSeconds) {
        CodeExerciseAttempt attempt = new CodeExerciseAttempt();
        attempt.id = UUID.randomUUID();
        attempt.userId = userId;
        attempt.exerciseId = exerciseId;
        attempt.exerciseType = exerciseType;
        attempt.userResponse = userResponse;
        attempt.timeSpentSeconds = timeSpentSeconds;
        attempt.attemptedAt = LocalDateTime.now();
        return attempt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public UUID getExerciseId() { return exerciseId; }
    public void setExerciseId(UUID exerciseId) { this.exerciseId = exerciseId; }
    public ExerciseType getExerciseType() { return exerciseType; }
    public void setExerciseType(ExerciseType exerciseType) { this.exerciseType = exerciseType; }
    public String getUserResponse() { return userResponse; }
    public void setUserResponse(String userResponse) { this.userResponse = userResponse; }
    public CodeExerciseScore getScore() { return score; }
    public void setScore(CodeExerciseScore score) { this.score = score; }
    public List<TechnicalJapaneseViolation> getViolations() { return violations; }
    public void setViolations(List<TechnicalJapaneseViolation> violations) { this.violations = violations; }
    public CommitMessageAnalysis getCommitAnalysis() { return commitAnalysis; }
    public void setCommitAnalysis(CommitMessageAnalysis commitAnalysis) { this.commitAnalysis = commitAnalysis; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
    public int getTimeSpentSeconds() { return timeSpentSeconds; }
    public void setTimeSpentSeconds(int timeSpentSeconds) { this.timeSpentSeconds = timeSpentSeconds; }
    public LocalDateTime getAttemptedAt() { return attemptedAt; }
    public void setAttemptedAt(LocalDateTime attemptedAt) { this.attemptedAt = attemptedAt; }
}
