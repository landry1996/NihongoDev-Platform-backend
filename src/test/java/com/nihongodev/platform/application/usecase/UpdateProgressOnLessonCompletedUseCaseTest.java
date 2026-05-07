package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.port.out.EventPublisherPort;
import com.nihongodev.platform.application.port.out.LearningActivityRepositoryPort;
import com.nihongodev.platform.application.port.out.ModuleProgressRepositoryPort;
import com.nihongodev.platform.application.port.out.ProgressRepositoryPort;
import com.nihongodev.platform.domain.event.LessonCompletedEvent;
import com.nihongodev.platform.domain.event.ProgressUpdatedEvent;
import com.nihongodev.platform.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateProgressOnLessonCompletedUseCase")
class UpdateProgressOnLessonCompletedUseCaseTest {

    @Mock private ProgressRepositoryPort progressRepository;
    @Mock private ModuleProgressRepositoryPort moduleProgressRepository;
    @Mock private LearningActivityRepositoryPort activityRepository;
    @Mock private EventPublisherPort eventPublisher;

    private UpdateProgressOnLessonCompletedUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new UpdateProgressOnLessonCompletedUseCase(
                progressRepository, moduleProgressRepository, activityRepository, eventPublisher);
    }

    @Test
    @DisplayName("should increment lesson counter and award 50 XP")
    void shouldIncrementLessonCounter() {
        UUID userId = UUID.randomUUID();
        LessonCompletedEvent event = LessonCompletedEvent.of(userId, UUID.randomUUID(), "Lesson 1", "GRAMMAR", "N5");

        when(progressRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(moduleProgressRepository.findByUserIdAndModuleType(userId, ModuleType.LESSON))
                .thenReturn(Optional.empty());
        when(activityRepository.existsByUserIdAndReferenceIdAndActivityType(
                any(), any(), any())).thenReturn(false);
        when(progressRepository.save(any(UserProgress.class))).thenAnswer(inv -> inv.getArgument(0));
        when(moduleProgressRepository.save(any(ModuleProgress.class))).thenAnswer(inv -> inv.getArgument(0));
        when(activityRepository.save(any(LearningActivity.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(event);

        ArgumentCaptor<UserProgress> captor = ArgumentCaptor.forClass(UserProgress.class);
        verify(progressRepository).save(captor.capture());
        assertThat(captor.getValue().getTotalLessonsCompleted()).isEqualTo(1);
        assertThat(captor.getValue().getTotalXp()).isEqualTo(50);
    }

    @Test
    @DisplayName("should be idempotent and skip duplicate events")
    void shouldSkipDuplicate() {
        UUID userId = UUID.randomUUID();
        UUID lessonId = UUID.randomUUID();
        LessonCompletedEvent event = LessonCompletedEvent.of(userId, lessonId, "Lesson 1", "GRAMMAR", "N5");

        when(activityRepository.existsByUserIdAndReferenceIdAndActivityType(
                userId, lessonId, ActivityType.LESSON_COMPLETED)).thenReturn(true);

        useCase.execute(event);

        verify(progressRepository, never()).save(any());
    }

    @Test
    @DisplayName("should publish ProgressUpdatedEvent after lesson completion")
    void shouldPublishEvent() {
        UUID userId = UUID.randomUUID();
        LessonCompletedEvent event = LessonCompletedEvent.of(userId, UUID.randomUUID(), "Lesson 1", "GRAMMAR", "N5");

        when(progressRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(moduleProgressRepository.findByUserIdAndModuleType(userId, ModuleType.LESSON))
                .thenReturn(Optional.empty());
        when(activityRepository.existsByUserIdAndReferenceIdAndActivityType(
                any(), any(), any())).thenReturn(false);
        when(progressRepository.save(any(UserProgress.class))).thenAnswer(inv -> inv.getArgument(0));
        when(moduleProgressRepository.save(any(ModuleProgress.class))).thenAnswer(inv -> inv.getArgument(0));
        when(activityRepository.save(any(LearningActivity.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(event);

        verify(eventPublisher).publish(eq("progress-events"), any(ProgressUpdatedEvent.class));
    }
}
