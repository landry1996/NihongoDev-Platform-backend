package com.nihongodev.platform.application.service.correction;

import com.nihongodev.platform.domain.model.Annotation;
import com.nihongodev.platform.domain.model.CorrectionScore;
import com.nihongodev.platform.domain.model.Severity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CorrectionMerger {

    public CorrectionScore mergeScores(List<CorrectionScore> scores) {
        if (scores.isEmpty()) return CorrectionScore.zero();

        CorrectionScore result = scores.get(0);
        for (int i = 1; i < scores.size(); i++) {
            result = result.merge(scores.get(i));
        }
        return result;
    }

    public List<Annotation> mergeAnnotations(List<Annotation> annotations) {
        List<Annotation> sorted = new ArrayList<>(annotations);
        sorted.sort(Comparator.comparingInt(Annotation::getStartOffset));

        List<Annotation> merged = new ArrayList<>();
        for (Annotation current : sorted) {
            boolean isDuplicate = merged.stream().anyMatch(existing ->
                    existing.overlaps(current)
                            && existing.getCategory() == current.getCategory()
                            && existing.getRuleId().equals(current.getRuleId()));
            if (!isDuplicate) {
                merged.add(current);
            }
        }

        merged.sort(Comparator
                .comparing(Annotation::getSeverity, Comparator.comparingInt(this::severityPriority))
                .thenComparingInt(Annotation::getStartOffset));

        return merged;
    }

    private int severityPriority(Severity severity) {
        return switch (severity) {
            case ERROR -> 0;
            case WARNING -> 1;
            case INFO -> 2;
        };
    }
}
