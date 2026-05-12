package com.nihongodev.platform.application.service.realcontent.pipeline;

import com.nihongodev.platform.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ContentIngestionPipeline")
class ContentIngestionPipelineTest {

    private ContentIngestionPipeline pipeline;

    @BeforeEach
    void setUp() {
        pipeline = new ContentIngestionPipeline(List.of(
            new TextNormalizationStep(),
            new MetadataExtractionStep()
        ));
    }

    @Test
    @DisplayName("should normalize full-width characters")
    void shouldNormalizeFullWidth() {
        RealContent content = RealContent.ingest(
            "テスト", "ＡＰＩの設計　パターン",
            "https://test.com", ContentSource.TECH_BLOG, ContentDomain.WEB_DEVELOPMENT
        );

        pipeline.process(content);

        assertThat(content.getBody()).doesNotContain("Ａ");
        assertThat(content.getBody()).contains("API");
        assertThat(content.getBody()).doesNotContain("　");
    }

    @Test
    @DisplayName("should extract tags from content")
    void shouldExtractTags() {
        RealContent content = RealContent.ingest(
            "Docker Guide", "Docker containerization with Kubernetes orchestration",
            "https://test.com", ContentSource.TECH_BLOG, ContentDomain.CLOUD_INFRASTRUCTURE
        );

        pipeline.process(content);

        assertThat(content.getTags()).isNotEmpty();
        assertThat(content.getTags()).contains("container");
    }

    @Test
    @DisplayName("should extract key vocabulary kanji compounds")
    void shouldExtractKeyVocabulary() {
        RealContent content = RealContent.ingest(
            "技術記事", "設計と実装と認証の処理について",
            "https://test.com", ContentSource.TECH_BLOG, ContentDomain.WEB_DEVELOPMENT
        );

        pipeline.process(content);

        assertThat(content.getKeyVocabulary()).isNotEmpty();
    }

    @Test
    @DisplayName("should normalize whitespace")
    void shouldNormalizeWhitespace() {
        RealContent content = RealContent.ingest(
            "テスト", "行1\r\n\r\n\r\n\r\n行2",
            "https://test.com", ContentSource.TECH_BLOG, ContentDomain.GENERAL_SOFTWARE
        );

        pipeline.process(content);

        assertThat(content.getBody()).doesNotContain("\r\n");
        assertThat(content.getBody()).doesNotContain("\n\n\n");
    }
}
