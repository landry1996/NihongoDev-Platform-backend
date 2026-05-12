package com.nihongodev.platform.application.service.realcontent.annotator;

import com.nihongodev.platform.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AnnotationEngine")
class AnnotationEngineTest {

    private AnnotationEngine engine;

    @BeforeEach
    void setUp() {
        List<ContentAnnotator> annotators = List.of(
            new VocabularyAnnotator(),
            new GrammarPatternAnnotator(),
            new KanjiReadingAnnotator(),
            new TechnicalTermAnnotator()
        );
        engine = new AnnotationEngine(annotators);
    }

    @Test
    @DisplayName("should annotate content with multiple annotators")
    void shouldAnnotateWithMultipleAnnotators() {
        RealContent content = RealContent.ingest(
            "テスト記事", "マイクロサービスにおいて設計の実装を行う",
            "https://test.com", ContentSource.TECH_BLOG, ContentDomain.WEB_DEVELOPMENT
        );

        engine.annotate(content);

        assertThat(content.getAnnotations()).isNotEmpty();
        assertThat(content.getStatus()).isEqualTo(ContentStatus.ANNOTATED);
        assertThat(content.getDifficulty()).isNotNull();
        assertThat(content.getReadingDifficulty()).isNotNull();
    }

    @Test
    @DisplayName("should assess difficulty based on annotation levels")
    void shouldAssessDifficulty() {
        RealContent content = RealContent.ingest(
            "高度な記事", "非同期処理における依存関係の脆弱性を踏まえて冗長化を設計する",
            "https://test.com", ContentSource.TECH_BLOG, ContentDomain.WEB_DEVELOPMENT
        );

        engine.annotate(content);

        assertThat(content.getDifficulty()).isIn(JapaneseLevel.N1, JapaneseLevel.N2);
    }

    @Test
    @DisplayName("should mark content as annotated")
    void shouldMarkAnnotated() {
        RealContent content = RealContent.ingest(
            "テスト", "簡単なテキストです",
            "https://test.com", ContentSource.TECH_BLOG, ContentDomain.GENERAL_SOFTWARE
        );

        engine.annotate(content);

        assertThat(content.getStatus()).isEqualTo(ContentStatus.ANNOTATED);
        assertThat(content.getAnnotatedAt()).isNotNull();
    }
}
