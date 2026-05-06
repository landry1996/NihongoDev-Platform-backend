package com.nihongodev.platform.application.service.correction;

import com.nihongodev.platform.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CorrectionMerger")
class CorrectionMergerTest {

    private CorrectionMerger merger;

    @BeforeEach
    void setUp() {
        merger = new CorrectionMerger();
    }

    @Test
    @DisplayName("should merge scores by averaging non-zero values")
    void shouldMergeScores() {
        CorrectionScore s1 = CorrectionScore.of(80, 0, 0, 0, 0, 0);
        CorrectionScore s2 = CorrectionScore.of(0, 70, 0, 0, 0, 0);
        CorrectionScore s3 = CorrectionScore.of(0, 0, 90, 0, 0, 0);

        CorrectionScore merged = merger.mergeScores(List.of(s1, s2, s3));

        assertThat(merged.getGrammarScore()).isEqualTo(80);
        assertThat(merged.getVocabularyScore()).isEqualTo(70);
        assertThat(merged.getPolitenessScore()).isEqualTo(90);
    }

    @Test
    @DisplayName("should deduplicate overlapping annotations with same rule")
    void shouldDeduplicateOverlapping() {
        Annotation a1 = Annotation.create(0, 5, Severity.WARNING, AnnotationCategory.GRAMMAR,
                "text", "suggestion", "explanation", "rule-1");
        Annotation a2 = Annotation.create(2, 7, Severity.WARNING, AnnotationCategory.GRAMMAR,
                "text2", "suggestion2", "explanation2", "rule-1");

        List<Annotation> merged = merger.mergeAnnotations(List.of(a1, a2));

        assertThat(merged).hasSize(1);
    }

    @Test
    @DisplayName("should keep non-overlapping annotations")
    void shouldKeepNonOverlapping() {
        Annotation a1 = Annotation.create(0, 5, Severity.WARNING, AnnotationCategory.GRAMMAR,
                "text1", "sug1", "exp1", "rule-1");
        Annotation a2 = Annotation.create(10, 15, Severity.ERROR, AnnotationCategory.KEIGO,
                "text2", "sug2", "exp2", "rule-2");

        List<Annotation> merged = merger.mergeAnnotations(List.of(a1, a2));

        assertThat(merged).hasSize(2);
    }

    @Test
    @DisplayName("should sort by severity priority (ERROR first)")
    void shouldSortBySeverity() {
        Annotation info = Annotation.create(0, 5, Severity.INFO, AnnotationCategory.CLARITY,
                "t", "s", "e", "rule-info");
        Annotation error = Annotation.create(10, 15, Severity.ERROR, AnnotationCategory.GRAMMAR,
                "t", "s", "e", "rule-error");
        Annotation warning = Annotation.create(20, 25, Severity.WARNING, AnnotationCategory.KEIGO,
                "t", "s", "e", "rule-warning");

        List<Annotation> merged = merger.mergeAnnotations(List.of(info, error, warning));

        assertThat(merged.get(0).getSeverity()).isEqualTo(Severity.ERROR);
        assertThat(merged.get(1).getSeverity()).isEqualTo(Severity.WARNING);
        assertThat(merged.get(2).getSeverity()).isEqualTo(Severity.INFO);
    }
}
