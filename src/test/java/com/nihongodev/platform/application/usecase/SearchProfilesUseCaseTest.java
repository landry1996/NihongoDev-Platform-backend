package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.RecruiterSearchResultDto;
import com.nihongodev.platform.application.port.out.BadgeRepositoryPort;
import com.nihongodev.platform.application.port.out.PublicProfileRepositoryPort;
import com.nihongodev.platform.domain.model.JapaneseLevel;
import com.nihongodev.platform.domain.model.PublicProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SearchProfilesUseCase")
class SearchProfilesUseCaseTest {

    @Mock private PublicProfileRepositoryPort profileRepository;
    @Mock private BadgeRepositoryPort badgeRepository;

    private SearchProfilesUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new SearchProfilesUseCase(profileRepository, badgeRepository);
    }

    @Test
    @DisplayName("should return search results with pagination")
    void shouldReturnSearchResults() {
        PublicProfile profile1 = PublicProfile.create(UUID.randomUUID(), "Taro", "taro");
        PublicProfile profile2 = PublicProfile.create(UUID.randomUUID(), "Hanako", "hanako");

        when(profileRepository.searchProfiles(JapaneseLevel.N3, null, true, 0, 20))
            .thenReturn(List.of(profile1, profile2));
        when(profileRepository.countSearchResults(JapaneseLevel.N3, null, true))
            .thenReturn(2);
        when(badgeRepository.findByIds(any())).thenReturn(List.of());

        RecruiterSearchResultDto result = useCase.search(JapaneseLevel.N3, null, true, 0, 20);

        assertThat(result.profiles()).hasSize(2);
        assertThat(result.totalResults()).isEqualTo(2);
        assertThat(result.page()).isZero();
        assertThat(result.pageSize()).isEqualTo(20);
    }

    @Test
    @DisplayName("should return empty results when no profiles match")
    void shouldReturnEmptyResults() {
        when(profileRepository.searchProfiles(any(), any(), any(), anyInt(), anyInt()))
            .thenReturn(List.of());
        when(profileRepository.countSearchResults(any(), any(), any()))
            .thenReturn(0);

        RecruiterSearchResultDto result = useCase.search(JapaneseLevel.N1, "Rust", true, 0, 10);

        assertThat(result.profiles()).isEmpty();
        assertThat(result.totalResults()).isZero();
    }
}
