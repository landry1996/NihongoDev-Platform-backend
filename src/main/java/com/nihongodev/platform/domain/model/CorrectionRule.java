package com.nihongodev.platform.domain.model;

import java.util.List;
import java.util.UUID;

public class CorrectionRule {

    private UUID id;
    private String name;
    private AnnotationCategory category;
    private Severity severity;
    private String pattern;
    private String suggestionTemplate;
    private String explanationTemplate;
    private List<TextType> applicableContexts;
    private JapaneseLevel minLevel;
    private boolean active;

    public CorrectionRule() {}

    public static CorrectionRule create(String name, AnnotationCategory category, Severity severity,
                                        String pattern, String suggestionTemplate,
                                        String explanationTemplate, List<TextType> contexts,
                                        JapaneseLevel minLevel) {
        CorrectionRule rule = new CorrectionRule();
        rule.id = UUID.randomUUID();
        rule.name = name;
        rule.category = category;
        rule.severity = severity;
        rule.pattern = pattern;
        rule.suggestionTemplate = suggestionTemplate;
        rule.explanationTemplate = explanationTemplate;
        rule.applicableContexts = contexts;
        rule.minLevel = minLevel;
        rule.active = true;
        return rule;
    }

    public boolean appliesTo(CorrectionContext context) {
        if (!active) return false;
        if (applicableContexts != null && !applicableContexts.isEmpty()
                && !applicableContexts.contains(context.getTextType())) {
            return false;
        }
        return true;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public AnnotationCategory getCategory() { return category; }
    public void setCategory(AnnotationCategory category) { this.category = category; }
    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }
    public String getPattern() { return pattern; }
    public void setPattern(String pattern) { this.pattern = pattern; }
    public String getSuggestionTemplate() { return suggestionTemplate; }
    public void setSuggestionTemplate(String suggestionTemplate) { this.suggestionTemplate = suggestionTemplate; }
    public String getExplanationTemplate() { return explanationTemplate; }
    public void setExplanationTemplate(String explanationTemplate) { this.explanationTemplate = explanationTemplate; }
    public List<TextType> getApplicableContexts() { return applicableContexts; }
    public void setApplicableContexts(List<TextType> applicableContexts) { this.applicableContexts = applicableContexts; }
    public JapaneseLevel getMinLevel() { return minLevel; }
    public void setMinLevel(JapaneseLevel minLevel) { this.minLevel = minLevel; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
