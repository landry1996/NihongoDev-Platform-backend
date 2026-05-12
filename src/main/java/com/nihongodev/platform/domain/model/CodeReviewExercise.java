package com.nihongodev.platform.domain.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class CodeReviewExercise {

    private UUID id;
    private String title;
    private String titleJp;
    private ExerciseType type;
    private CodeContext codeContext;
    private JapaneseLevel difficulty;
    private String codeSnippet;
    private String codeLanguage;
    private String scenario;
    private String scenarioJp;
    private ReviewLevel expectedReviewLevel;
    private List<String> technicalIssues;
    private String modelAnswer;
    private String modelAnswerExplanation;
    private List<String> keyPhrases;
    private List<String> avoidPhrases;
    private List<String> technicalTermsJp;
    private PRTemplate prTemplate;
    private CommitMessageRule commitRule;
    private String culturalNote;
    private int xpReward;
    private boolean published;
    private LocalDateTime createdAt;

    public CodeReviewExercise() {}

    public static CodeReviewExercise create(String title, String titleJp, ExerciseType type,
                                            CodeContext codeContext, JapaneseLevel difficulty,
                                            String scenario, String scenarioJp, int xpReward) {
        CodeReviewExercise exercise = new CodeReviewExercise();
        exercise.id = UUID.randomUUID();
        exercise.title = title;
        exercise.titleJp = titleJp;
        exercise.type = type;
        exercise.codeContext = codeContext;
        exercise.difficulty = difficulty;
        exercise.scenario = scenario;
        exercise.scenarioJp = scenarioJp;
        exercise.xpReward = xpReward;
        exercise.published = false;
        exercise.createdAt = LocalDateTime.now();
        return exercise;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getTitleJp() { return titleJp; }
    public void setTitleJp(String titleJp) { this.titleJp = titleJp; }
    public ExerciseType getType() { return type; }
    public void setType(ExerciseType type) { this.type = type; }
    public CodeContext getCodeContext() { return codeContext; }
    public void setCodeContext(CodeContext codeContext) { this.codeContext = codeContext; }
    public JapaneseLevel getDifficulty() { return difficulty; }
    public void setDifficulty(JapaneseLevel difficulty) { this.difficulty = difficulty; }
    public String getCodeSnippet() { return codeSnippet; }
    public void setCodeSnippet(String codeSnippet) { this.codeSnippet = codeSnippet; }
    public String getCodeLanguage() { return codeLanguage; }
    public void setCodeLanguage(String codeLanguage) { this.codeLanguage = codeLanguage; }
    public String getScenario() { return scenario; }
    public void setScenario(String scenario) { this.scenario = scenario; }
    public String getScenarioJp() { return scenarioJp; }
    public void setScenarioJp(String scenarioJp) { this.scenarioJp = scenarioJp; }
    public ReviewLevel getExpectedReviewLevel() { return expectedReviewLevel; }
    public void setExpectedReviewLevel(ReviewLevel expectedReviewLevel) { this.expectedReviewLevel = expectedReviewLevel; }
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
    public PRTemplate getPrTemplate() { return prTemplate; }
    public void setPrTemplate(PRTemplate prTemplate) { this.prTemplate = prTemplate; }
    public CommitMessageRule getCommitRule() { return commitRule; }
    public void setCommitRule(CommitMessageRule commitRule) { this.commitRule = commitRule; }
    public String getCulturalNote() { return culturalNote; }
    public void setCulturalNote(String culturalNote) { this.culturalNote = culturalNote; }
    public int getXpReward() { return xpReward; }
    public void setXpReward(int xpReward) { this.xpReward = xpReward; }
    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
