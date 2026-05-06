package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.port.out.EventPublisherPort;
import com.nihongodev.platform.application.port.out.LessonRepositoryPort;
import com.nihongodev.platform.domain.event.LessonCompletedEvent;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.Lesson;
import com.nihongodev.platform.domain.model.LessonLevel;
import com.nihongodev.platform.domain.model.LessonType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CompleteLessonUseCase")
class CompleteLessonUseCaseTest {

    @Mock private LessonRepositoryPort lessonRepository;
    @Mock private EventPublisherPort eventPublisher;

    private CompleteLessonUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CompleteLessonUseCase(lessonRepository, eventPublisher);
    }

    @Test
    @DisplayName("should complete lesson and publish event")
    void shouldCompleteLessonAndPublishEvent() {
        UUID userId = UUID.randomUUID();
        UUID lessonId = UUID.randomUUID();
        Lesson lesson = Lesson.create("Test Lesson", "Desc", LessonType.GRAMMAR, LessonLevel.A2, "content", 1);
        lesson.setId(lessonId);

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));

        useCase.complete(userId, lessonId);

        verify(eventPublisher).publish(eq("lesson-events"), any(LessonCompletedEvent.class));
    }

    @Test
    @DisplayName("should throw when lesson not found")
    void shouldThrowWhenLessonNotFound() {
        UUID userId = UUID.randomUUID();
        UUID lessonId = UUID.randomUUID();

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.complete(userId, lessonId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Lesson");

        verify(eventPublisher, never()).publish(any(), any());
    }
}
