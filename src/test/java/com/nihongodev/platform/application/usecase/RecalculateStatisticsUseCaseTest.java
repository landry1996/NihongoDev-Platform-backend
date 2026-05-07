package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.port.out.LearningActivityRepositoryPort;
import com.nihongodev.platform.application.port.out.StatisticsRepositoryPort;
import com.nihongodev.platform.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RecalculateStatisticsUseCase")
class RecalculateStatisticsUseCaseTest {

    @Mock private StatisticsRepositoryPort statisticsRepository;
    @Mock private LearningActivityRepositoryPort activityRepository;

    private RecalculateStatisticsUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new RecalculateStatisticsUseCase(statisticsRepository, activityRepository);
    }

    @Test
    @DisplayName("should recalculate statistics for active users")
    void shouldRecalculateForActiveUsers() {
        UUID userId = UUID.randomUUID();
        when(statisticsRepository.findUserIdsWithActivitySince(any())).thenReturn(List.of(userId));
        when(statisticsRepository.findByUserId(userId)).thenReturn(Optional.empty());

        LearningActivity activity1 = LearningActivity.create(userId, ActivityType.QUIZ_COMPLETED, UUID.randomUUID(), 80.0, 110);
        activity1.setOccurredAt(LocalDateTime.now().minusDays(2));
        LearningActivity activity2 = LearningActivity.create(userId, ActivityType.QUIZ_COMPLETED, UUID.randomUUID(), 60.0, 90);
        activity2.setOccurredAt(LocalDateTime.now().minusDays(1));

        when(activityRepository.findByUserIdAndOccurredAfter(eq(userId), any()))
                .thenReturn(List.of(activity1, activity2));
        when(activityRepository.countDistinctDaysActiveAfter(eq(userId), any()))
                .thenReturn(2L);
        when(statisticsRepository.save(any(UserStatistics.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.recalculateAll();

        ArgumentCaptor<UserStatistics> captor = ArgumentCaptor.forClass(UserStatistics.class);
        verify(statisticsRepository).save(captor.capture());
        UserStatistics saved = captor.getValue();
        assertThat(saved.getAverageScore7Days()).isEqualTo(70.0);
        assertThat(saved.getUserId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("should detect IMPROVING trend")
    void shouldDetectImprovingTrend() {
        UUID userId = UUID.randomUUID();
        when(statisticsRepository.findUserIdsWithActivitySince(any())).thenReturn(List.of(userId));
        when(statisticsRepository.findByUserId(userId)).thenReturn(Optional.empty());

        LearningActivity recent = LearningActivity.create(userId, ActivityType.QUIZ_COMPLETED, UUID.randomUUID(), 90.0, 120);
        recent.setOccurredAt(LocalDateTime.now().minusDays(1));
        LearningActivity older = LearningActivity.create(userId, ActivityType.QUIZ_COMPLETED, UUID.randomUUID(), 50.0, 65);
        older.setOccurredAt(LocalDateTime.now().minusDays(20));

        when(activityRepository.findByUserIdAndOccurredAfter(eq(userId), any()))
                .thenReturn(List.of(recent, older));
        when(activityRepository.countDistinctDaysActiveAfter(eq(userId), any()))
                .thenReturn(2L);
        when(statisticsRepository.save(any(UserStatistics.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.recalculateAll();

        ArgumentCaptor<UserStatistics> captor = ArgumentCaptor.forClass(UserStatistics.class);
        verify(statisticsRepository).save(captor.capture());
        assertThat(captor.getValue().getProgressTrend()).isEqualTo(Trend.IMPROVING);
    }

    @Test
    @DisplayName("should identify weak areas below 75%")
    void shouldIdentifyWeakAreas() {
        UUID userId = UUID.randomUUID();
        when(statisticsRepository.findUserIdsWithActivitySince(any())).thenReturn(List.of(userId));
        when(statisticsRepository.findByUserId(userId)).thenReturn(Optional.empty());

        List<LearningActivity> activities = List.of(
                createActivity(userId, ActivityType.QUIZ_COMPLETED, 50.0, 1),
                createActivity(userId, ActivityType.QUIZ_COMPLETED, 55.0, 2),
                createActivity(userId, ActivityType.QUIZ_COMPLETED, 45.0, 3),
                createActivity(userId, ActivityType.QUIZ_COMPLETED, 40.0, 4)
        );

        when(activityRepository.findByUserIdAndOccurredAfter(eq(userId), any())).thenReturn(activities);
        when(activityRepository.countDistinctDaysActiveAfter(eq(userId), any())).thenReturn(4L);
        when(statisticsRepository.save(any(UserStatistics.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.recalculateAll();

        ArgumentCaptor<UserStatistics> captor = ArgumentCaptor.forClass(UserStatistics.class);
        verify(statisticsRepository).save(captor.capture());
        assertThat(captor.getValue().getWeakAreas()).isNotEmpty();
        assertThat(captor.getValue().getWeakAreas().get(0).getPriority()).isEqualTo(Priority.HIGH);
    }

    private LearningActivity createActivity(UUID userId, ActivityType type, double score, int daysAgo) {
        LearningActivity a = LearningActivity.create(userId, type, UUID.randomUUID(), score, 50);
        a.setOccurredAt(LocalDateTime.now().minusDays(daysAgo));
        return a;
    }
}
