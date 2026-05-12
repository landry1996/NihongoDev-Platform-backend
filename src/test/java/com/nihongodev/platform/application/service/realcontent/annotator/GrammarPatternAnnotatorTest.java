package com.nihongodev.platform.application.service.realcontent.annotator;

import com.nihongodev.platform.domain.model.AnnotationType;
import com.nihongodev.platform.domain.model.ContentAnnotation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GrammarPatternAnnotator")
class GrammarPatternAnnotatorTest {

    private GrammarPatternAnnotator annotator;

    @BeforeEach
    void setUp() {
        annotator = new GrammarPatternAnnotator();
    }

    @Test
    @DisplayName("should detect grammar patterns in text")
    void shouldDetectGrammarPatterns() {
        String text = "このAPIにおいてバージョン管理を行うことができる";

        List<ContentAnnotation> annotations = annotator.annotate(text);

        assertThat(annotations).anyMatch(a -> a.surfaceForm().equals("において"));
        assertThat(annotations).anyMatch(a -> a.surfaceForm().equals("ことができる"));
    }

    @Test
    @DisplayName("should include grammar notes")
    void shouldIncludeGrammarNotes() {
        String text = "開発においてテストは重要です";

        List<ContentAnnotation> annotations = annotator.annotate(text);

        assertThat(annotations).anyMatch(a ->
            a.surfaceForm().equals("において") && a.grammarNote() != null && !a.grammarNote().isEmpty()
        );
    }

    @Test
    @DisplayName("should set correct annotation type")
    void shouldSetCorrectType() {
        assertThat(annotator.getType()).isEqualTo(AnnotationType.GRAMMAR_PATTERN);
    }
}
