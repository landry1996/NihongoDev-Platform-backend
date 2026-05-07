package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.UserProgressDto;
import com.nihongodev.platform.application.port.out.ProgressRepositoryPort;
import com.nihongodev.platform.domain.model.ProgressLevel;
import com.nihongodev.platform.domain.model.UserProgress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetUserProgressUseCase")
class GetUserProgressUseCaseTest {

    @Mock private ProgressRepositoryPort progressRepository;

    private GetUserProgressUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetUserProgressUseCase(progressRepository);
    }

    @Test
    @DisplayName("should return user progress when exists")
    void shouldReturnProgress() {
        UUID userId = UUID.randomUUID();
        UserProgress progress = UserProgress.initialize(userId);
        progress.setTotalXp(2500);
        progress.setLevel(ProgressLevel.INTERMEDIATE);
        progress.setTotalQuizzesCompleted(10);

        when(progressRepository.findByUserId(userId)).thenReturn(Optional.of(progress));

        UserProgressDto dto = useCase.execute(userId);

        assertThat(dto.userId()).isEqualTo(userId);
        assertThat(dto.totalXp()).isEqualTo(2500);
        assertThat(dto.level()).isEqualTo(ProgressLevel.INTERMEDIATE);
        assertThat(dto.totalQuizzesCompleted()).isEqualTo(10);
    }

    @Test
    @DisplayName("should return initialized progress for new user")
    void shouldReturnInitializedForNewUser() {
        UUID userId = UUID.randomUUID();
        when(progressRepository.findByUserId(userId)).thenReturn(Optional.empty());

        UserProgressDto dto = useCase.execute(userId);

        assertThat(dto.userId()).isEqualTo(userId);
        assertThat(dto.totalXp()).isZero();
        assertThat(dto.level()).isEqualTo(ProgressLevel.BEGINNER);
    }
}
