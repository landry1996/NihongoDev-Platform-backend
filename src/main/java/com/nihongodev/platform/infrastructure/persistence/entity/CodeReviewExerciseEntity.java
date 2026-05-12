package com.nihongodev.platform.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "code_review_exercises")
public class CodeReviewExerciseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "title_jp", length = 200)
    private String titleJp;

    @Column(name = "exercise_type", nullable = false, length = 30)
    private String exerciseType;

    @Column(name = "code_context", nullable = false, length = 30)
    private String codeContext;

    @Column(nullable = false, length = 20)
    private String difficulty;

    @Column(name = "code_snippet", columnDefinition = "TEXT")
    private String codeSnippet;

    @Column(name = "code_language", length = 30)
    private String codeLanguage;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String scenario;

    @Column(name = "scenario_jp", columnDefinition = "TEXT")
    private String scenarioJp;

    @Column(name = "expected_review_level", length = 20)
    private String expectedReviewLevel;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "technical_issues", columnDefinition = "jsonb")
    private List<String> technicalIssues;

    @Column(name = "model_answer", columnDefinition = "TEXT")
    private String modelAnswer;

    @Column(name = "model_answer_explanation", columnDefinition = "TEXT")
    private String modelAnswerExplanation;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "key_phrases", columnDefinition = "jsonb")
    private List<String> keyPhrases;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "avoid_phrases", columnDefinition = "jsonb")
    private List<String> avoidPhrases;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "technical_terms_jp", columnDefinition = "jsonb")
    private List<String> technicalTermsJp;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "pr_template", columnDefinition = "jsonb")
    private Map<String, Object> prTemplate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "commit_rule", columnDefinition = "jsonb")
    private Map<String, Object> commitRule;

    @Column(name = "cultural_note", columnDefinition = "TEXT")
    private String culturalNote;

    @Column(name = "xp_reward", nullable = false)
    private int xpReward;

    @Column(nullable = false)
    private boolean published;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getTitleJp() { return titleJp; }
    public void setTitleJp(String titleJp) { this.titleJp = titleJp; }
    public String getExerciseType() { return exerciseType; }
    public void setExerciseType(String exerciseType) { this.exerciseType = exerciseType; }
    public String getCodeContext() { return codeContext; }
    public void setCodeContext(String codeContext) { this.codeContext = codeContext; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public String getCodeSnippet() { return codeSnippet; }
    public void setCodeSnippet(String codeSnippet) { this.codeSnippet = codeSnippet; }
    public String getCodeLanguage() { return codeLanguage; }
    public void setCodeLanguage(String codeLanguage) { this.codeLanguage = codeLanguage; }
    public String getScenario() { return scenario; }
    public void setScenario(String scenario) { this.scenario = scenario; }
    public String getScenarioJp() { return scenarioJp; }
    public void setScenarioJp(String scenarioJp) { this.scenarioJp = scenarioJp; }
    public String getExpectedReviewLevel() { return expectedReviewLevel; }
    public void setExpectedReviewLevel(String expectedReviewLevel) { this.expectedReviewLevel = expectedReviewLevel; }
    public List<String> getTechnicalIssues() { return technicalIssues; }
    public void setTechnicalIssues(List<String> technicalIssues) { this.technicalIssues = technicalIssues; }
    public String getModelAnswer() { return modelAnswer; }
    public void setModelAnswer(String modelAnswer) { this.modelAnswer = modelAnswer; }
    public String getModelAnswerExplanation() { return modelAnswerExplanation; }
    public void setModelAnswerExplanation(String modelAnswerExplanation) { this.modelAnswerExplanation = modelAnswerExplanation; }
    public List<String> getKeyPhrases() { return keyPhrases; }
    public void setKeyPhrases(List<String> keyPhrases) { this.keyPhrases = keyPhrases; }
    public List<String> getAvoidPhrases() { return avoidPhrases; }
    public void setAvoidPhrases(List<String> avoidPhrases) { this.avoidPhrases = avoidPhrases; }
    public List<String> getTechnicalTermsJp() { return technicalTermsJp; }
    public void setTechnicalTermsJp(List<String> technicalTermsJp) { this.technicalTermsJp = technicalTermsJp; }
    public Map<String, Object> getPrTemplate() { return prTemplate; }
    public void setPrTemplate(Map<String, Object> prTemplate) { this.prTemplate = prTemplate; }
    public Map<String, Object> getCommitRule() { return commitRule; }
    public void setCommitRule(Map<String, Object> commitRule) { this.commitRule = commitRule; }
    public String getCulturalNote() { return culturalNote; }
    public void setCulturalNote(String culturalNote) { this.culturalNote = culturalNote; }
    public int getXpReward() { return xpReward; }
    public void setXpReward(int xpReward) { this.xpReward = xpReward; }
    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
