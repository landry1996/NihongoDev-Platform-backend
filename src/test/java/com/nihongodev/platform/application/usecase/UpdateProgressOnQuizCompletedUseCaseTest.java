package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.port.out.EventPublisherPort;
import com.nihongodev.platform.application.port.out.LearningActivityRepositoryPort;
import com.nihongodev.platform.application.port.out.ModuleProgressRepositoryPort;
import com.nihongodev.platform.application.port.out.ProgressRepositoryPort;
import com.nihongodev.platform.domain.event.ProgressUpdatedEvent;
import com.nihongodev.platform.domain.event.QuizCompletedEvent;
import com.nihongodev.platform.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateProgressOnQuizCompletedUseCase")
class UpdateProgressOnQuizCompletedUseCaseTest {

    @Mock private ProgressRepositoryPort progressRepository;
    @Mock private ModuleProgressRepositoryPort moduleProgressRepository;
    @Mock private LearningActivityRepositoryPort activityRepository;
    @Mock private EventPublisherPort eventPublisher;

    private UpdateProgressOnQuizCompletedUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new UpdateProgressOnQuizCompletedUseCase(
                progressRepository, moduleProgressRepository, activityRepository, eventPublisher);
    }

    @Test
    @DisplayName("should create UserProgress if first activity for user")
    void shouldCreateProgressIfFirst() {
        UUID userId = UUID.randomUUID();
        QuizCompletedEvent event = new QuizCompletedEvent(
                userId, UUID.randomUUID(), UUID.randomUUID(), "Quiz 1",
                85.0, true, 5, "CLASSIC", LocalDateTime.now());

        when(progressRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(moduleProgressRepository.findByUserIdAndModuleType(userId, ModuleType.QUIZ))
                .thenReturn(Optional.empty());
        when(activityRepository.existsByUserIdAndReferenceIdAndActivityType(
                userId, event.attemptId(), ActivityType.QUIZ_COMPLETED)).thenReturn(false);
        when(progressRepository.save(any(UserProgress.class))).thenAnswer(inv -> inv.getArgument(0));
        when(moduleProgressRepository.save(any(ModuleProgress.class))).thenAnswer(inv -> inv.getArgument(0));
        when(activityRepository.save(any(LearningActivity.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(event);

        ArgumentCaptor<UserProgress> captor = ArgumentCaptor.forClass(UserProgress.class);
        verify(progressRepository).save(captor.capture());
        UserProgress saved = captor.getValue();
        assertThat(saved.getTotalQuizzesCompleted()).isEqualTo(1);
        assertThat(saved.getTotalXp()).isEqualTo(115); // 30 + (85 * 1.0)
    }

    @Test
    @DisplayName("should increment quiz counter on existing progress")
    void shouldIncrementQuizCounter() {
        UUID userId = UUID.randomUUID();
        UserProgress existing = UserProgress.initialize(userId);
        existing.recordActivity(ActivityType.QUIZ_COMPLETED, 70.0);

        QuizCompletedEvent event = new QuizCompletedEvent(
                userId, UUID.randomUUID(), UUID.randomUUID(), "Quiz 2",
                90.0, true, 7, "CLASSIC", LocalDateTime.now());

        when(progressRepository.findByUserId(userId)).thenReturn(Optional.of(existing));
        when(moduleProgressRepository.findByUserIdAndModuleType(userId, ModuleType.QUIZ))
                .thenReturn(Optional.of(ModuleProgress.initialize(userId, ModuleType.QUIZ)));
        when(activityRepository.existsByUserIdAndReferenceIdAndActivityType(
                userId, event.attemptId(), ActivityType.QUIZ_COMPLETED)).thenReturn(false);
        when(progressRepository.save(any(UserProgress.class))).thenAnswer(inv -> inv.getArgument(0));
        when(moduleProgressRepository.save(any(ModuleProgress.class))).thenAnswer(inv -> inv.getArgument(0));
        when(activityRepository.save(any(LearningActivity.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(event);

        ArgumentCaptor<UserProgress> captor = ArgumentCaptor.forClass(UserProgress.class);
        verify(progressRepository).save(captor.capture());
        assertThat(captor.getValue().getTotalQuizzesCompleted()).isEqualTo(2);
    }

    @Test
    @DisplayName("should be idempotent and skip duplicate events")
    void shouldSkipDuplicateEvent() {
        UUID userId = UUID.randomUUID();
        UUID attemptId = UUID.randomUUID();
        QuizCompletedEvent event = new QuizCompletedEvent(
                userId, UUID.randomUUID(), attemptId, "Quiz 1",
                85.0, true, 5, "CLASSIC", LocalDateTime.now());

        when(activityRepository.existsByUserIdAndReferenceIdAndActivityType(
                userId, attemptId, ActivityType.QUIZ_COMPLETED)).thenReturn(true);

        useCase.execute(event);

        verify(progressRepository, never()).save(any());
        verify(eventPublisher, never()).publish(anyString(), any());
    }

    @Test
    @DisplayName("should publish ProgressUpdatedEvent")
    void shouldPublishEvent() {
        UUID userId = UUID.randomUUID();
        QuizCompletedEvent event = new QuizCompletedEvent(
                userId, UUID.randomUUID(), UUID.randomUUID(), "Quiz 1",
                80.0, true, 4, "CLASSIC", LocalDateTime.now());

        when(progressRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(moduleProgressRepository.findByUserIdAndModuleType(userId, ModuleType.QUIZ))
                .thenReturn(Optional.empty());
        when(activityRepository.existsByUserIdAndReferenceIdAndActivityType(
                any(), any(), any())).thenReturn(false);
        when(progressRepository.save(any(UserProgress.class))).thenAnswer(inv -> inv.getArgument(0));
        when(moduleProgressRepository.save(any(ModuleProgress.class))).thenAnswer(inv -> inv.getArgument(0));
        when(activityRepository.save(any(LearningActivity.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(event);

        verify(eventPublisher).publish(eq("progress-events"), any(ProgressUpdatedEvent.class));
    }

    @Test
    @DisplayName("should update module progress average score")
    void shouldUpdateModuleProgress() {
        UUID userId = UUID.randomUUID();
        QuizCompletedEvent event = new QuizCompletedEvent(
                userId, UUID.randomUUID(), UUID.randomUUID(), "Quiz 1",
                75.0, true, 3, "CLASSIC", LocalDateTime.now());

        when(progressRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(moduleProgressRepository.findByUserIdAndModuleType(userId, ModuleType.QUIZ))
                .thenReturn(Optional.empty());
        when(activityRepository.existsByUserIdAndReferenceIdAndActivityType(
                any(), any(), any())).thenReturn(false);
        when(progressRepository.save(any(UserProgress.class))).thenAnswer(inv -> inv.getArgument(0));
        when(moduleProgressRepository.save(any(ModuleProgress.class))).thenAnswer(inv -> inv.getArgument(0));
        when(activityRepository.save(any(LearningActivity.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(event);

        ArgumentCaptor<ModuleProgress> captor = ArgumentCaptor.forClass(ModuleProgress.class);
        verify(moduleProgressRepository).save(captor.capture());
        assertThat(captor.getValue().getCompletedItems()).isEqualTo(1);
        assertThat(captor.getValue().getAverageScore()).isEqualTo(75.0);
    }
}
