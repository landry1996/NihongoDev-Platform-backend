package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.ContentFeedDto;
import com.nihongodev.platform.application.port.out.RealContentRepositoryPort;
import com.nihongodev.platform.application.port.out.UserContentPreferenceRepositoryPort;
import com.nihongodev.platform.application.service.realcontent.selector.*;
import com.nihongodev.platform.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetPersonalizedFeedUseCase")
class GetPersonalizedFeedUseCaseTest {

    @Mock private RealContentRepositoryPort contentRepository;
    @Mock private UserContentPreferenceRepositoryPort preferenceRepository;

    private GetPersonalizedFeedUseCase useCase;

    @BeforeEach
    void setUp() {
        PersonalizedContentSelector selector = new PersonalizedContentSelector(List.of(
            new DomainRelevanceScorer(),
            new DifficultyMatchScorer(),
            new ReadingTimeScorer(),
            new SourcePreferenceScorer()
        ));
        useCase = new GetPersonalizedFeedUseCase(contentRepository, preferenceRepository, selector);
    }

    @Test
    @DisplayName("should return personalized feed with defaults when no preferences")
    void shouldReturnFeedWithDefaults() {
        UUID userId = UUID.randomUUID();
        RealContent content = RealContent.ingest("テスト", "本文",
            "https://test.com", ContentSource.TECH_BLOG, ContentDomain.WEB_DEVELOPMENT);
        content.setReadingDifficulty(ReadingDifficulty.INTERMEDIATE);
        content.setEstimatedReadingMinutes(10);

        when(preferenceRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(contentRepository.findPublished()).thenReturn(List.of(content));
        when(contentRepository.findRecentPublished(5)).thenReturn(List.of(content));

        ContentFeedDto result = useCase.execute(userId);

        assertThat(result.recommended()).hasSize(1);
        assertThat(result.newArrivals()).hasSize(1);
        assertThat(result.totalAvailable()).isEqualTo(1);
    }

    @Test
    @DisplayName("should use user preferences when available")
    void shouldUseUserPreferences() {
        UUID userId = UUID.randomUUID();
        UserContentPreference pref = new UserContentPreference(
            userId, List.of(ContentDomain.SECURITY), JapaneseLevel.N2,
            ReadingDifficulty.ADVANCED, 15, List.of(ContentSource.API_DOCUMENTATION)
        );

        when(preferenceRepository.findByUserId(userId)).thenReturn(Optional.of(pref));
        when(contentRepository.findPublished()).thenReturn(List.of());
        when(contentRepository.findRecentPublished(5)).thenReturn(List.of());

        ContentFeedDto result = useCase.execute(userId);

        assertThat(result.recommended()).isEmpty();
        assertThat(result.totalAvailable()).isEqualTo(0);
    }
}
