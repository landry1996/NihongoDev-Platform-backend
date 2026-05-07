package com.nihongodev.platform.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ModuleProgress")
class ModuleProgressTest {

    private ModuleProgress moduleProgress;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        moduleProgress = ModuleProgress.initialize(userId, ModuleType.QUIZ);
    }

    @Test
    @DisplayName("should initialize with NOT_STARTED status")
    void shouldInitialize() {
        assertThat(moduleProgress.getUserId()).isEqualTo(userId);
        assertThat(moduleProgress.getModuleType()).isEqualTo(ModuleType.QUIZ);
        assertThat(moduleProgress.getStatus()).isEqualTo(ModuleStatus.NOT_STARTED);
        assertThat(moduleProgress.getCompletedItems()).isZero();
        assertThat(moduleProgress.getAverageScore()).isZero();
        assertThat(moduleProgress.getBestScore()).isZero();
    }

    @Test
    @DisplayName("should transition to IN_PROGRESS on first completion")
    void shouldTransitionToInProgress() {
        moduleProgress.recordCompletion(80.0);

        assertThat(moduleProgress.getStatus()).isEqualTo(ModuleStatus.IN_PROGRESS);
        assertThat(moduleProgress.getCompletedItems()).isEqualTo(1);
    }

    @Test
    @DisplayName("should calculate average score incrementally")
    void shouldCalculateAverageScore() {
        moduleProgress.recordCompletion(80.0);
        moduleProgress.recordCompletion(60.0);

        assertThat(moduleProgress.getAverageScore()).isEqualTo(70.0);
        assertThat(moduleProgress.getCompletedItems()).isEqualTo(2);
    }

    @Test
    @DisplayName("should update best score when new score is higher")
    void shouldUpdateBestScore() {
        moduleProgress.recordCompletion(60.0);
        moduleProgress.recordCompletion(90.0);
        moduleProgress.recordCompletion(70.0);

        assertThat(moduleProgress.getBestScore()).isEqualTo(90.0);
    }

    @Test
    @DisplayName("should calculate completion percentage when totalItems set")
    void shouldCalculateCompletionPercentage() {
        moduleProgress.setTotalItems(10);
        moduleProgress.recordCompletion(80.0);
        moduleProgress.recordCompletion(70.0);

        assertThat(moduleProgress.getCompletionPercentage()).isEqualTo(20.0);
    }

    @Test
    @DisplayName("should return 0 completion percentage when totalItems is null")
    void shouldReturnZeroWhenNoTotalItems() {
        moduleProgress.recordCompletion(80.0);

        assertThat(moduleProgress.getCompletionPercentage()).isZero();
    }
}
