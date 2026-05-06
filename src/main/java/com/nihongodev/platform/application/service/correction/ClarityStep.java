package com.nihongodev.platform.application.service.correction;

import com.nihongodev.platform.domain.model.*;

import java.util.ArrayList;
import java.util.List;

public class ClarityStep implements CorrectionStep {

    private static final int IDEAL_SENTENCE_LENGTH = 40;
    private static final int MAX_SENTENCE_LENGTH = 80;

    @Override
    public CorrectionStepResult process(String text, CorrectionContext context) {
        List<Annotation> annotations = new ArrayList<>();
        double score = 80.0;

        String[] sentences = text.split("[。！？]");

        for (String sentence : sentences) {
            if (sentence.trim().isEmpty()) continue;

            if (sentence.length() > MAX_SENTENCE_LENGTH) {
                int start = text.indexOf(sentence);
                annotations.add(Annotation.create(
                        start, start + sentence.length(), Severity.WARNING,
                        AnnotationCategory.CLARITY, sentence,
                        "Break into shorter sentences for clarity",
                        "Sentence exceeds " + MAX_SENTENCE_LENGTH + " characters — consider splitting",
                        "clarity-long-sentence"
                ));
                score -= 8;
            }
        }

        if (sentences.length == 1 && text.length() > 100) {
            score -= 10;
            annotations.add(Annotation.create(
                    0, Math.min(text.length(), 20), Severity.INFO,
                    AnnotationCategory.CLARITY, "",
                    "Add paragraph structure with multiple sentences",
                    "Single long sentence reduces readability",
                    "clarity-single-sentence"
            ));
        }

        if (text.contains("。") && text.length() > 30) {
            score += 5;
        }
        if (text.contains("、") && text.contains("。")) {
            score += 5;
        }

        score = Math.max(Math.min(score, 100), 0);
        CorrectionScore correctionScore = CorrectionScore.of(0, 0, 0, score, 0, 0);
        return new CorrectionStepResult(correctionScore, annotations);
    }
}
