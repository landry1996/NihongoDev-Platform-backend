package com.nihongodev.platform.domain.model;

public class CorrectionScore {

    private double grammarScore;
    private double vocabularyScore;
    private double politenessScore;
    private double clarityScore;
    private double naturalnessScore;
    private double professionalScore;

    public CorrectionScore() {}

    public static CorrectionScore zero() {
        return new CorrectionScore();
    }

    public static CorrectionScore of(double grammar, double vocabulary, double politeness,
                                     double clarity, double naturalness, double professional) {
        CorrectionScore s = new CorrectionScore();
        s.grammarScore = grammar;
        s.vocabularyScore = vocabulary;
        s.politenessScore = politeness;
        s.clarityScore = clarity;
        s.naturalnessScore = naturalness;
        s.professionalScore = professional;
        return s;
    }

    public double getOverallScore() {
        return (grammarScore + vocabularyScore + politenessScore
                + clarityScore + naturalnessScore + professionalScore) / 6.0;
    }

    public CorrectionScore merge(CorrectionScore other) {
        return CorrectionScore.of(
                avg(this.grammarScore, other.grammarScore),
                avg(this.vocabularyScore, other.vocabularyScore),
                avg(this.politenessScore, other.politenessScore),
                avg(this.clarityScore, other.clarityScore),
                avg(this.naturalnessScore, other.naturalnessScore),
                avg(this.professionalScore, other.professionalScore)
        );
    }

    private double avg(double a, double b) {
        if (a == 0) return b;
        if (b == 0) return a;
        return (a + b) / 2.0;
    }

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
}
