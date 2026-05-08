package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.CulturalProgressDto;
import com.nihongodev.platform.application.port.out.CulturalProgressRepositoryPort;
import com.nihongodev.platform.domain.model.CulturalProgress;
import com.nihongodev.platform.domain.model.KeigoLevel;
import com.nihongodev.platform.domain.model.ScenarioCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetCulturalProgressUseCase")
class GetCulturalProgressUseCaseTest {

    @Mock
    private CulturalProgressRepositoryPort progressRepository;

    @InjectMocks
    private GetCulturalProgressUseCase useCase;

    @Test
    @DisplayName("should return empty list for user with no progress")
    void shouldReturnEmptyForNoProgress() {
        UUID userId = UUID.randomUUID();
        when(progressRepository.findByUserId(userId)).thenReturn(List.of());

        List<CulturalProgressDto> result = useCase.getProgress(userId);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should return progress DTOs for user with data")
    void shouldReturnProgressDtos() {
        UUID userId = UUID.randomUUID();
        CulturalProgress p1 = CulturalProgress.initialize(userId, ScenarioCategory.COMMUNICATION);
        p1.recordAttempt(80);
        CulturalProgress p2 = CulturalProgress.initialize(userId, ScenarioCategory.SOCIAL);
        p2.recordAttempt(65);

        when(progressRepository.findByUserId(userId)).thenReturn(List.of(p1, p2));

        List<CulturalProgressDto> result = useCase.getProgress(userId);

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("should map domain fields to DTO correctly")
    void shouldMapCorrectly() {
        UUID userId = UUID.randomUUID();
        CulturalProgress progress = CulturalProgress.initialize(userId, ScenarioCategory.REPORTING);
        progress.recordAttempt(85);
        progress.recordAttempt(90);

        when(progressRepository.findByUserId(userId)).thenReturn(List.of(progress));

        List<CulturalProgressDto> result = useCase.getProgress(userId);

        assertThat(result).hasSize(1);
        CulturalProgressDto dto = result.get(0);
        assertThat(dto.category()).isEqualTo(ScenarioCategory.REPORTING);
        assertThat(dto.scenariosCompleted()).isEqualTo(2);
        assertThat(dto.bestScore()).isEqualTo(90);
        assertThat(dto.currentStreak()).isEqualTo(2);
    }
}
