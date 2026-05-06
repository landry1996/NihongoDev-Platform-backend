package com.nihongodev.platform.domain.model;

import java.util.UUID;

public class WeaknessPattern {

    private UUID id;
    private UUID userId;
    private AnnotationCategory category;
    private String patternDescription;
    private int occurrenceCount;
    private String lastExample;

    public WeaknessPattern() {}

    public static WeaknessPattern create(UUID userId, AnnotationCategory category,
                                         String patternDescription, String example) {
        WeaknessPattern wp = new WeaknessPattern();
        wp.id = UUID.randomUUID();
        wp.userId = userId;
        wp.category = category;
        wp.patternDescription = patternDescription;
        wp.occurrenceCount = 1;
        wp.lastExample = example;
        return wp;
    }

    public void incrementOccurrence(String newExample) {
        this.occurrenceCount++;
        this.lastExample = newExample;
    }

    public boolean isRecurring() {
        return occurrenceCount >= 3;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public AnnotationCategory getCategory() { return category; }
    public void setCategory(AnnotationCategory category) { this.category = category; }
    public String getPatternDescription() { return patternDescription; }
    public void setPatternDescription(String patternDescription) { this.patternDescription = patternDescription; }
    public int getOccurrenceCount() { return occurrenceCount; }
    public void setOccurrenceCount(int occurrenceCount) { this.occurrenceCount = occurrenceCount; }
    public String getLastExample() { return lastExample; }
    public void setLastExample(String lastExample) { this.lastExample = lastExample; }
}
