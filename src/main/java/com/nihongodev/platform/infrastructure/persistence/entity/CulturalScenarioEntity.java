package com.nihongodev.platform.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cultural_scenarios")
public class CulturalScenarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 300)
    private String title;

    @Column(name = "title_jp", length = 300)
    private String titleJp;

    @Column(columnDefinition = "TEXT")
    private String situation;

    @Column(name = "situation_jp", columnDefinition = "TEXT")
    private String situationJp;

    @Column(length = 50)
    private String context;

    @Column(length = 50)
    private String relationship;

    @Column(length = 50)
    private String mode;

    @Column(length = 50)
    private String category;

    @Column(name = "expected_keigo_level", length = 50)
    private String expectedKeigoLevel;

    @Column(length = 50)
    private String difficulty;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String choices;

    @Column(name = "model_answer", columnDefinition = "TEXT")
    private String modelAnswer;

    @Column(name = "model_answer_explanation", columnDefinition = "TEXT")
    private String modelAnswerExplanation;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "key_phrases", columnDefinition = "jsonb")
    private String keyPhrases;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "avoid_phrases", columnDefinition = "jsonb")
    private String avoidPhrases;

    @Column(name = "cultural_note", columnDefinition = "TEXT")
    private String culturalNote;

    @Column(name = "xp_reward")
    private int xpReward;

    @Column(name = "is_published")
    private boolean published;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
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
    public String getContext() { return context; }
    public void setContext(String context) { this.context = context; }
    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }
    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getExpectedKeigoLevel() { return expectedKeigoLevel; }
    public void setExpectedKeigoLevel(String expectedKeigoLevel) { this.expectedKeigoLevel = expectedKeigoLevel; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public String getChoices() { return choices; }
    public void setChoices(String choices) { this.choices = choices; }
    public String getModelAnswer() { return modelAnswer; }
    public void setModelAnswer(String modelAnswer) { this.modelAnswer = modelAnswer; }
    public String getModelAnswerExplanation() { return modelAnswerExplanation; }
    public void setModelAnswerExplanation(String modelAnswerExplanation) { this.modelAnswerExplanation = modelAnswerExplanation; }
    public String getKeyPhrases() { return keyPhrases; }
    public void setKeyPhrases(String keyPhrases) { this.keyPhrases = keyPhrases; }
    public String getAvoidPhrases() { return avoidPhrases; }
    public void setAvoidPhrases(String avoidPhrases) { this.avoidPhrases = avoidPhrases; }
    public String getCulturalNote() { return culturalNote; }
    public void setCulturalNote(String culturalNote) { this.culturalNote = culturalNote; }
    public int getXpReward() { return xpReward; }
    public void setXpReward(int xpReward) { this.xpReward = xpReward; }
    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
