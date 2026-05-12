package com.nihongodev.platform.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "user_content_preferences")
public class UserContentPreferenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "preferred_domains", columnDefinition = "jsonb")
    private List<String> preferredDomains;

    @Column(name = "current_level", length = 10)
    private String currentLevel;

    @Column(name = "max_difficulty", length = 20)
    private String maxDifficulty;

    @Column(name = "preferred_reading_minutes")
    private int preferredReadingMinutes;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "preferred_sources", columnDefinition = "jsonb")
    private List<String> preferredSources;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public List<String> getPreferredDomains() { return preferredDomains; }
    public void setPreferredDomains(List<String> preferredDomains) { this.preferredDomains = preferredDomains; }
    public String getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(String currentLevel) { this.currentLevel = currentLevel; }
    public String getMaxDifficulty() { return maxDifficulty; }
    public void setMaxDifficulty(String maxDifficulty) { this.maxDifficulty = maxDifficulty; }
    public int getPreferredReadingMinutes() { return preferredReadingMinutes; }
    public void setPreferredReadingMinutes(int preferredReadingMinutes) { this.preferredReadingMinutes = preferredReadingMinutes; }
    public List<String> getPreferredSources() { return preferredSources; }
    public void setPreferredSources(List<String> preferredSources) { this.preferredSources = preferredSources; }
}
