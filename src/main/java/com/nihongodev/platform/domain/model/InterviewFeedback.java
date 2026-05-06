package com.nihongodev.platform.domain.model;

import java.util.List;
import java.util.UUID;

public class InterviewFeedback {

    private UUID id;
    private UUID answerId;
    private double overallScore;
    private double languageScore;
    private double technicalScore;
    private double communicationScore;
    private double culturalScore;
    private List<String> strengths;
    private List<String> improvements;
    private String modelAnswer;
    private List<String> grammarNotes;
    private List<String> vocabularySuggestions;

    public InterviewFeedback() {}

    public static InterviewFeedback create(UUID answerId, double languageScore, double technicalScore,
                                           double communicationScore, double culturalScore,
                                           List<String> strengths, List<String> improvements,
                                           String modelAnswer, List<String> grammarNotes,
                                           List<String> vocabularySuggestions) {
        InterviewFeedback fb = new InterviewFeedback();
        fb.id = UUID.randomUUID();
        fb.answerId = answerId;
        fb.languageScore = languageScore;
        fb.technicalScore = technicalScore;
        fb.communicationScore = communicationScore;
        fb.culturalScore = culturalScore;
        fb.overallScore = (languageScore + technicalScore + communicationScore + culturalScore) / 4.0;
        fb.strengths = strengths;
        fb.improvements = improvements;
        fb.modelAnswer = modelAnswer;
        fb.grammarNotes = grammarNotes;
        fb.vocabularySuggestions = vocabularySuggestions;
        return fb;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getAnswerId() { return answerId; }
    public void setAnswerId(UUID answerId) { this.answerId = answerId; }
    public double getOverallScore() { return overallScore; }
    public void setOverallScore(double overallScore) { this.overallScore = overallScore; }
    public double getLanguageScore() { return languageScore; }
    public void setLanguageScore(double languageScore) { this.languageScore = languageScore; }
    public double getTechnicalScore() { return technicalScore; }
    public void setTechnicalScore(double technicalScore) { this.technicalScore = technicalScore; }
    public double getCommunicationScore() { return communicationScore; }
    public void setCommunicationScore(double communicationScore) { this.communicationScore = communicationScore; }
    public double getCulturalScore() { return culturalScore; }
    public void setCulturalScore(double culturalScore) { this.culturalScore = culturalScore; }
    public List<String> getStrengths() { return strengths; }
    public void setStrengths(List<String> strengths) { this.strengths = strengths; }
    public List<String> getImprovements() { return improvements; }
    public void setImprovements(List<String> improvements) { this.improvements = improvements; }
    public String getModelAnswer() { return modelAnswer; }
    public void setModelAnswer(String modelAnswer) { this.modelAnswer = modelAnswer; }
    public List<String> getGrammarNotes() { return grammarNotes; }
    public void setGrammarNotes(List<String> grammarNotes) { this.grammarNotes = grammarNotes; }
    public List<String> getVocabularySuggestions() { return vocabularySuggestions; }
    public void setVocabularySuggestions(List<String> vocabularySuggestions) { this.vocabularySuggestions = vocabularySuggestions; }
}
