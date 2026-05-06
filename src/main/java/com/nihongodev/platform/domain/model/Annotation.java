package com.nihongodev.platform.domain.model;

import java.util.UUID;

public class Annotation {

    private UUID id;
    private int startOffset;
    private int endOffset;
    private Severity severity;
    private AnnotationCategory category;
    private String original;
    private String suggestion;
    private String explanation;
    private String ruleId;

    public Annotation() {}

    public static Annotation create(int startOffset, int endOffset, Severity severity,
                                    AnnotationCategory category, String original,
                                    String suggestion, String explanation, String ruleId) {
        Annotation a = new Annotation();
        a.id = UUID.randomUUID();
        a.startOffset = startOffset;
        a.endOffset = endOffset;
        a.severity = severity;
        a.category = category;
        a.original = original;
        a.suggestion = suggestion;
        a.explanation = explanation;
        a.ruleId = ruleId;
        return a;
    }

    public boolean overlaps(Annotation other) {
        return this.startOffset < other.endOffset && other.startOffset < this.endOffset;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public int getStartOffset() { return startOffset; }
    public void setStartOffset(int startOffset) { this.startOffset = startOffset; }
    public int getEndOffset() { return endOffset; }
    public void setEndOffset(int endOffset) { this.endOffset = endOffset; }
    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }
    public AnnotationCategory getCategory() { return category; }
    public void setCategory(AnnotationCategory category) { this.category = category; }
    public String getOriginal() { return original; }
    public void setOriginal(String original) { this.original = original; }
    public String getSuggestion() { return suggestion; }
    public void setSuggestion(String suggestion) { this.suggestion = suggestion; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    public String getRuleId() { return ruleId; }
    public void setRuleId(String ruleId) { this.ruleId = ruleId; }
}
