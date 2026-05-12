package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.IngestContentCommand;
import com.nihongodev.platform.application.dto.RealContentDto;
import com.nihongodev.platform.application.port.out.RealContentRepositoryPort;
import com.nihongodev.platform.application.service.realcontent.pipeline.ContentIngestionPipeline;
import com.nihongodev.platform.application.service.realcontent.pipeline.MetadataExtractionStep;
import com.nihongodev.platform.application.service.realcontent.pipeline.TextNormalizationStep;
import com.nihongodev.platform.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("IngestContentUseCase")
class IngestContentUseCaseTest {

    @Mock private RealContentRepositoryPort contentRepository;

    private IngestContentUseCase useCase;

    @BeforeEach
    void setUp() {
        ContentIngestionPipeline pipeline = new ContentIngestionPipeline(
            List.of(new TextNormalizationStep(), new MetadataExtractionStep())
        );
        useCase = new IngestContentUseCase(contentRepository, pipeline);
    }

    @Test
    @DisplayName("should ingest new content successfully")
    void shouldIngestContent() {
        var command = new IngestContentCommand(
            "APIの設計", "マイクロサービスの設計パターン",
            "https://blog.example.com/api", ContentSource.TECH_BLOG,
            ContentDomain.WEB_DEVELOPMENT, "著者"
        );
        when(contentRepository.existsBySourceUrl(command.sourceUrl())).thenReturn(false);
        when(contentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RealContentDto result = useCase.execute(command);

        assertThat(result.title()).isEqualTo("APIの設計");
        assertThat(result.status()).isEqualTo(ContentStatus.INGESTED);
        verify(contentRepository).save(any());
    }

    @Test
    @DisplayName("should reject duplicate source URL")
    void shouldRejectDuplicate() {
        var command = new IngestContentCommand(
            "Title", "Body", "https://dup.com",
            ContentSource.TECH_BLOG, ContentDomain.WEB_DEVELOPMENT, null
        );
        when(contentRepository.existsBySourceUrl("https://dup.com")).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(command))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("already exists");
    }
}
