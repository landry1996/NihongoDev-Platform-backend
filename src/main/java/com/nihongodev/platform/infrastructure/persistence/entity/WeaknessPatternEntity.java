package com.nihongodev.platform.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "weakness_patterns")
public class WeaknessPatternEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "pattern_description", nullable = false)
    private String patternDescription;

    @Column(name = "occurrence_count", nullable = false)
    private int occurrenceCount;

    @Column(name = "last_example", columnDefinition = "TEXT")
    private String lastExample;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getPatternDescription() { return patternDescription; }
    public void setPatternDescription(String patternDescription) { this.patternDescription = patternDescription; }
    public int getOccurrenceCount() { return occurrenceCount; }
    public void setOccurrenceCount(int occurrenceCount) { this.occurrenceCount = occurrenceCount; }
    public String getLastExample() { return lastExample; }
    public void setLastExample(String lastExample) { this.lastExample = lastExample; }
}
