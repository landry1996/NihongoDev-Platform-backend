package com.nihongodev.platform.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("RealContent")
class RealContentTest {

    @Test
    @DisplayName("should ingest content with correct initial state")
    void shouldIngestCorrectly() {
        RealContent content = RealContent.ingest(
            "マイクロサービス入門", "マイクロサービスの設計パターンについて",
            "https://test.com", ContentSource.TECH_BLOG, ContentDomain.WEB_DEVELOPMENT
        );

        assertThat(content.getId()).isNotNull();
        assertThat(content.getStatus()).isEqualTo(ContentStatus.INGESTED);
        assertThat(content.getIngestedAt()).isNotNull();
        assertThat(content.getWordCount()).isGreaterThan(0);
    }

    @Test
    @DisplayName("should count kanji characters")
    void shouldCountKanji() {
        RealContent content = RealContent.ingest(
            "test", "設計と実装の関数", "https://test.com",
            ContentSource.TECH_BLOG, ContentDomain.WEB_DEVELOPMENT
        );

        assertThat(content.getKanjiCount()).isEqualTo(6);
    }

    @Test
    @DisplayName("should transition from annotated to published")
    void shouldTransitionToPublished() {
        RealContent content = RealContent.ingest(
            "test", "body", "https://test.com",
            ContentSource.TECH_BLOG, ContentDomain.WEB_DEVELOPMENT
        );
        content.markAnnotated();

        content.publish();

        assertThat(content.getStatus()).isEqualTo(ContentStatus.PUBLISHED);
    }

    @Test
    @DisplayName("should not publish unannotated content")
    void shouldRejectPublishingUnannotated() {
        RealContent content = RealContent.ingest(
            "test", "body", "https://test.com",
            ContentSource.TECH_BLOG, ContentDomain.WEB_DEVELOPMENT
        );

        assertThatThrownBy(content::publish)
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("annotated before publishing");
    }

    @Test
    @DisplayName("should add annotations")
    void shouldAddAnnotations() {
        RealContent content = RealContent.ingest(
            "test", "設計パターン", "https://test.com",
            ContentSource.TECH_BLOG, ContentDomain.WEB_DEVELOPMENT
        );

        ContentAnnotation annotation = ContentAnnotation.create(
            0, 2, "設計", "せっけい", "design",
            AnnotationType.VOCABULARY, JapaneseLevel.N3
        );
        content.addAnnotation(annotation);

        assertThat(content.getAnnotations()).hasSize(1);
        assertThat(content.getAnnotations().get(0).surfaceForm()).isEqualTo("設計");
    }

    @Test
    @DisplayName("should archive content")
    void shouldArchiveContent() {
        RealContent content = RealContent.ingest(
            "test", "body", "https://test.com",
            ContentSource.TECH_BLOG, ContentDomain.WEB_DEVELOPMENT
        );

        content.archive();

        assertThat(content.getStatus()).isEqualTo(ContentStatus.ARCHIVED);
    }
}
