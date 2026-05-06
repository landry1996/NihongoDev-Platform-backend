package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.UpdateLessonCommand;
import com.nihongodev.platform.application.dto.LessonDto;
import com.nihongodev.platform.application.port.out.LessonRepositoryPort;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateLessonUseCase")
class UpdateLessonUseCaseTest {

    @Mock private LessonRepositoryPort lessonRepository;

    private UpdateLessonUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new UpdateLessonUseCase(lessonRepository);
    }

    @Test
    @DisplayName("should update lesson successfully")
    void shouldUpdateLesson() {
        UUID lessonId = UUID.randomUUID();
        Lesson existing = Lesson.create("Old Title", "Old desc", LessonType.HIRAGANA, LessonLevel.BEGINNER, "old content", 1);
        existing.setId(lessonId);

        UpdateLessonCommand command = new UpdateLessonCommand(
                lessonId, "New Title", "New desc", LessonType.KATAKANA, LessonLevel.A1, "new content", 2
        );

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(existing));
        when(lessonRepository.save(any(Lesson.class))).thenAnswer(inv -> inv.getArgument(0));

        LessonDto result = useCase.update(command);

        assertThat(result.title()).isEqualTo("New Title");
        assertThat(result.description()).isEqualTo("New desc");
        assertThat(result.type()).isEqualTo(LessonType.KATAKANA);
        assertThat(result.level()).isEqualTo(LessonLevel.A1);
        verify(lessonRepository).save(any(Lesson.class));
    }

    @Test
    @DisplayName("should throw when lesson not found")
    void shouldThrowWhenNotFound() {
        UUID lessonId = UUID.randomUUID();
        UpdateLessonCommand command = new UpdateLessonCommand(lessonId, "Title", null, null, null, null, null);

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.update(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Lesson");

        verify(lessonRepository, never()).save(any());
    }

    @Test
    @DisplayName("should partially update lesson (only title)")
    void shouldPartiallyUpdate() {
        UUID lessonId = UUID.randomUUID();
        Lesson existing = Lesson.create("Original", "Desc", LessonType.GRAMMAR, LessonLevel.B2, "content", 3);
        existing.setId(lessonId);

        UpdateLessonCommand command = new UpdateLessonCommand(lessonId, "Updated Title", null, null, null, null, null);

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(existing));
        when(lessonRepository.save(any(Lesson.class))).thenAnswer(inv -> inv.getArgument(0));

        LessonDto result = useCase.update(command);

        assertThat(result.title()).isEqualTo("Updated Title");
        assertThat(result.description()).isEqualTo("Desc");
        assertThat(result.type()).isEqualTo(LessonType.GRAMMAR);
        assertThat(result.level()).isEqualTo(LessonLevel.B2);
    }
}
