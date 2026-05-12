package com.nihongodev.platform.application.service.realcontent.annotator;

import com.nihongodev.platform.domain.model.AnnotationType;
import com.nihongodev.platform.domain.model.ContentAnnotation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("VocabularyAnnotator")
class VocabularyAnnotatorTest {

    private VocabularyAnnotator annotator;

    @BeforeEach
    void setUp() {
        annotator = new VocabularyAnnotator();
    }

    @Test
    @DisplayName("should detect known tech vocabulary")
    void shouldDetectKnownVocabulary() {
        String text = "この実装では非同期処理を使用しています";

        List<ContentAnnotation> annotations = annotator.annotate(text);

        assertThat(annotations).isNotEmpty();
        assertThat(annotations).anyMatch(a -> a.surfaceForm().equals("実装"));
        assertThat(annotations).anyMatch(a -> a.surfaceForm().equals("非同期"));
    }

    @Test
    @DisplayName("should return empty list for text without vocabulary")
    void shouldReturnEmptyForNoVocabulary() {
        String text = "Hello world, this is plain English text";

        List<ContentAnnotation> annotations = annotator.annotate(text);

        assertThat(annotations).isEmpty();
    }

    @Test
    @DisplayName("should set correct annotation type")
    void shouldSetCorrectType() {
        assertThat(annotator.getType()).isEqualTo(AnnotationType.VOCABULARY);
    }

    @Test
    @DisplayName("should detect katakana tech terms")
    void shouldDetectKatakanaTerms() {
        String text = "リファクタリングとデプロイの流れ";

        List<ContentAnnotation> annotations = annotator.annotate(text);

        assertThat(annotations).anyMatch(a -> a.surfaceForm().equals("リファクタリング"));
        assertThat(annotations).anyMatch(a -> a.surfaceForm().equals("デプロイ"));
    }
}
