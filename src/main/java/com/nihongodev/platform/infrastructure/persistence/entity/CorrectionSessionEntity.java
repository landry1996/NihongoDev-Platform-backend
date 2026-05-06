package com.nihongodev.platform.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "correction_sessions")
public class CorrectionSessionEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "original_text", nullable = false, columnDefinition = "TEXT")
    private String originalText;

    @Column(name = "text_type", nullable = false)
    private String textType;

    @Column(name = "target_level", nullable = false)
    private String targetLevel;

    @Column(name = "grammar_score")
    private double grammarScore;

    @Column(name = "vocabulary_score")
    private double vocabularyScore;

    @Column(name = "politeness_score")
    private double politenessScore;

    @Column(name = "clarity_score")
    private double clarityScore;

    @Column(name = "naturalness_score")
    private double naturalnessScore;

    @Column(name = "professional_score")
    private double professionalScore;

    @Column(name = "overall_score")
    private double overallScore;

    @Column(name = "total_annotations")
    private int totalAnnotations;

    @Column(name = "error_count")
    private int errorCount;

    @Column(name = "warning_count")
    private int warningCount;

    @Column(name = "info_count")
    private int infoCount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CorrectionAnnotationEntity> annotations = new ArrayList<>();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getOriginalText() { return originalText; }
    public void setOriginalText(String originalText) { this.originalText = originalText; }
    public String getTextType() { return textType; }
    public void setTextType(String textType) { this.textType = textType; }
    public String getTargetLevel() { return targetLevel; }
    public void setTargetLevel(String targetLevel) { this.targetLevel = targetLevel; }
    public double getGrammarScore() { return grammarScore; }
    public void setGrammarScore(double grammarScore) { this.grammarScore = grammarScore; }
    public double getVocabularyScore() { return vocabularyScore; }
    public void setVocabularyScore(double vocabularyScore) { this.vocabularyScore = vocabularyScore; }
    public double getPolitenessScore() { return politenessScore; }
    public void setPolitenessScore(double politenessScore) { this.politenessScore = politenessScore; }
    public double getClarityScore() { return clarityScore; }
    public void setClarityScore(double clarityScore) { this.clarityScore = clarityScore; }
    public double getNaturalnessScore() { return naturalnessScore; }
    public void setNaturalnessScore(double naturalnessScore) { this.naturalnessScore = naturalnessScore; }
    public double getProfessionalScore() { return professionalScore; }
    public void setProfessionalScore(double professionalScore) { this.professionalScore = professionalScore; }
    public double getOverallScore() { return overallScore; }
    public void setOverallScore(double overallScore) { this.overallScore = overallScore; }
    public int getTotalAnnotations() { return totalAnnotations; }
    public void setTotalAnnotations(int totalAnnotations) { this.totalAnnotations = totalAnnotations; }
    public int getErrorCount() { return errorCount; }
    public void setErrorCount(int errorCount) { this.errorCount = errorCount; }
    public int getWarningCount() { return warningCount; }
    public void setWarningCount(int warningCount) { this.warningCount = warningCount; }
    public int getInfoCount() { return infoCount; }
    public void setInfoCount(int infoCount) { this.infoCount = infoCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<CorrectionAnnotationEntity> getAnnotations() { return annotations; }
    public void setAnnotations(List<CorrectionAnnotationEntity> annotations) { this.annotations = annotations; }
}
