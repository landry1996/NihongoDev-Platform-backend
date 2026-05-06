package com.nihongodev.platform.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "correction_rules")
public class CorrectionRuleEntity {

    @Id
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "severity", nullable = false)
    private String severity;

    @Column(name = "pattern", nullable = false)
    private String pattern;

    @Column(name = "suggestion_template", columnDefinition = "TEXT")
    private String suggestionTemplate;

    @Column(name = "explanation_template", columnDefinition = "TEXT")
    private String explanationTemplate;

    @Column(name = "applicable_contexts")
    private String applicableContexts;

    @Column(name = "min_level")
    private String minLevel;

    @Column(name = "active", nullable = false)
    private boolean active;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public String getPattern() { return pattern; }
    public void setPattern(String pattern) { this.pattern = pattern; }
    public String getSuggestionTemplate() { return suggestionTemplate; }
    public void setSuggestionTemplate(String suggestionTemplate) { this.suggestionTemplate = suggestionTemplate; }
    public String getExplanationTemplate() { return explanationTemplate; }
    public void setExplanationTemplate(String explanationTemplate) { this.explanationTemplate = explanationTemplate; }
    public String getApplicableContexts() { return applicableContexts; }
    public void setApplicableContexts(String applicableContexts) { this.applicableContexts = applicableContexts; }
    public String getMinLevel() { return minLevel; }
    public void setMinLevel(String minLevel) { this.minLevel = minLevel; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
