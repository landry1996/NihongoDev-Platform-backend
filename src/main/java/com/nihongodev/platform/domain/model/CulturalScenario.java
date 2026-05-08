package com.nihongodev.platform.domain.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class CulturalScenario {

    private UUID id;
    private String title;
    private String titleJp;
    private String situation;
    private String situationJp;
    private WorkplaceContext context;
    private RelationshipType relationship;
    private ScenarioMode mode;
    private ScenarioCategory category;
    private KeigoLevel expectedKeigoLevel;
    private JapaneseLevel difficulty;
    private List<ScenarioChoice> choices;
    private String modelAnswer;
    private String modelAnswerExplanation;
    private List<String> keyPhrases;
    private List<String> avoidPhrases;
    private String culturalNote;
    private int xpReward;
    private boolean published;
    private LocalDateTime createdAt;

    public CulturalScenario() {}

    public static CulturalScenario create(String title, String titleJp, String situation, String situationJp,
                                          WorkplaceContext context, RelationshipType relationship,
                                          ScenarioMode mode, ScenarioCategory category,
                                          KeigoLevel expectedKeigoLevel, JapaneseLevel difficulty,
                                          int xpReward) {
        CulturalScenario scenario = new CulturalScenario();
        scenario.id = UUID.randomUUID();
        scenario.title = title;
        scenario.titleJp = titleJp;
        scenario.situation = situation;
        scenario.situationJp = situationJp;
        scenario.context = context;
        scenario.relationship = relationship;
        scenario.mode = mode;
        scenario.category = category;
        scenario.expectedKeigoLevel = expectedKeigoLevel;
        scenario.difficulty = difficulty;
        scenario.xpReward = xpReward;
        scenario.published = false;
        scenario.createdAt = LocalDateTime.now();
        return scenario;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getTitleJp() { return titleJp; }
    public void setTitleJp(String titleJp) { this.titleJp = titleJp; }
    public String getSituation() { return situation; }
    public void setSituation(String situation) { this.situation = situation; }
    public String getSituationJp() { return situationJp; }
    public void setSituationJp(String situationJp) { this.situationJp = situationJp; }
    public WorkplaceContext getContext() { return context; }
    public void setContext(WorkplaceContext context) { this.context = context; }
    public RelationshipType getRelationship() { return relationship; }
    public void setRelationship(RelationshipType relationship) { this.relationship = relationship; }
    public ScenarioMode getMode() { return mode; }
    public void setMode(ScenarioMode mode) { this.mode = mode; }
    public ScenarioCategory getCategory() { return category; }
    public void setCategory(ScenarioCategory category) { this.category = category; }
    public KeigoLevel getExpectedKeigoLevel() { return expectedKeigoLevel; }
    public void setExpectedKeigoLevel(KeigoLevel expectedKeigoLevel) { this.expectedKeigoLevel = expectedKeigoLevel; }
    public JapaneseLevel getDifficulty() { return difficulty; }
    public void setDifficulty(JapaneseLevel difficulty) { this.difficulty = difficulty; }
    public List<ScenarioChoice> getChoices() { return choices; }
    public void setChoices(List<ScenarioChoice> choices) { this.choices = choices; }
    public String getModelAnswer() { return modelAnswer; }
    public void setModelAnswer(String modelAnswer) { this.modelAnswer = modelAnswer; }
    public String getModelAnswerExplanation() { return modelAnswerExplanation; }
    public void setModelAnswerExplanation(String modelAnswerExplanation) { this.modelAnswerExplanation = modelAnswerExplanation; }
    public List<String> getKeyPhrases() { return keyPhrases; }
    public void setKeyPhrases(List<String> keyPhrases) { this.keyPhrases = keyPhrases; }
    public List<String> getAvoidPhrases() { return avoidPhrases; }
    public void setAvoidPhrases(List<String> avoidPhrases) { this.avoidPhrases = avoidPhrases; }
    public String getCulturalNote() { return culturalNote; }
    public void setCulturalNote(String culturalNote) { this.culturalNote = culturalNote; }
    public int getXpReward() { return xpReward; }
    public void setXpReward(int xpReward) { this.xpReward = xpReward; }
    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
