package com.nihongodev.platform.domain.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ScenarioAttempt {

    private UUID id;
    private UUID userId;
    private UUID scenarioId;
    private String userResponse;
    private UUID selectedChoiceId;
    private CulturalScore score;
    private List<KeigoViolation> violations;
    private String feedback;
    private int timeSpentSeconds;
    private LocalDateTime attemptedAt;

    public ScenarioAttempt() {}

    public static ScenarioAttempt create(UUID userId, UUID scenarioId, String userResponse, UUID selectedChoiceId, int timeSpentSeconds) {
        ScenarioAttempt attempt = new ScenarioAttempt();
        attempt.id = UUID.randomUUID();
        attempt.userId = userId;
        attempt.scenarioId = scenarioId;
        attempt.userResponse = userResponse;
        attempt.selectedChoiceId = selectedChoiceId;
        attempt.timeSpentSeconds = timeSpentSeconds;
        attempt.attemptedAt = LocalDateTime.now();
        return attempt;
    }

    public void applyScore(CulturalScore score, List<KeigoViolation> violations, String feedback) {
        this.score = score;
        this.violations = violations;
        this.feedback = feedback;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public UUID getScenarioId() { return scenarioId; }
    public void setScenarioId(UUID scenarioId) { this.scenarioId = scenarioId; }
    public String getUserResponse() { return userResponse; }
    public void setUserResponse(String userResponse) { this.userResponse = userResponse; }
    public UUID getSelectedChoiceId() { return selectedChoiceId; }
    public void setSelectedChoiceId(UUID selectedChoiceId) { this.selectedChoiceId = selectedChoiceId; }
    public CulturalScore getScore() { return score; }
    public void setScore(CulturalScore score) { this.score = score; }
    public List<KeigoViolation> getViolations() { return violations; }
    public void setViolations(List<KeigoViolation> violations) { this.violations = violations; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
    public int getTimeSpentSeconds() { return timeSpentSeconds; }
    public void setTimeSpentSeconds(int timeSpentSeconds) { this.timeSpentSeconds = timeSpentSeconds; }
    public LocalDateTime getAttemptedAt() { return attemptedAt; }
    public void setAttemptedAt(LocalDateTime attemptedAt) { this.attemptedAt = attemptedAt; }
}
