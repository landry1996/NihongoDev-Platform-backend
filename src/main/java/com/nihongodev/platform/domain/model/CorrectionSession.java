package com.nihongodev.platform.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CorrectionSession {

    private UUID id;
    private UUID userId;
    private String originalText;
    private TextType textType;
    private JapaneseLevel targetLevel;
    private CorrectionScore score;
    private List<Annotation> annotations;
    private int totalAnnotations;
    private int errorCount;
    private int warningCount;
    private int infoCount;
    private LocalDateTime createdAt;

    public CorrectionSession() {}

    public static CorrectionSession create(UUID userId, String originalText, TextType textType,
                                           JapaneseLevel targetLevel) {
        CorrectionSession session = new CorrectionSession();
        session.id = UUID.randomUUID();
        session.userId = userId;
        session.originalText = originalText;
        session.textType = textType;
        session.targetLevel = targetLevel;
        session.score = CorrectionScore.zero();
        session.annotations = new ArrayList<>();
        session.totalAnnotations = 0;
        session.errorCount = 0;
        session.warningCount = 0;
        session.infoCount = 0;
        session.createdAt = LocalDateTime.now();
        return session;
    }

    public void applyResults(CorrectionScore score, List<Annotation> annotations) {
        this.score = score;
        this.annotations = annotations;
        this.totalAnnotations = annotations.size();
        this.errorCount = (int) annotations.stream()
                .filter(a -> a.getSeverity() == Severity.ERROR).count();
        this.warningCount = (int) annotations.stream()
                .filter(a -> a.getSeverity() == Severity.WARNING).count();
        this.infoCount = (int) annotations.stream()
                .filter(a -> a.getSeverity() == Severity.INFO).count();
    }

    public boolean hasErrors() {
        return errorCount > 0;
    }

    public boolean hasWarnings() {
        return warningCount > 0;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getOriginalText() { return originalText; }
    public void setOriginalText(String originalText) { this.originalText = originalText; }
    public TextType getTextType() { return textType; }
    public void setTextType(TextType textType) { this.textType = textType; }
    public JapaneseLevel getTargetLevel() { return targetLevel; }
    public void setTargetLevel(JapaneseLevel targetLevel) { this.targetLevel = targetLevel; }
    public CorrectionScore getScore() { return score; }
    public void setScore(CorrectionScore score) { this.score = score; }
    public List<Annotation> getAnnotations() { return annotations; }
    public void setAnnotations(List<Annotation> annotations) { this.annotations = annotations; }
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
}
