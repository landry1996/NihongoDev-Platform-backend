package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.port.out.LessonRepositoryPort;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteLessonUseCase")
class DeleteLessonUseCaseTest {

    @Mock private LessonRepositoryPort lessonRepository;

    private DeleteLessonUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new DeleteLessonUseCase(lessonRepository);
    }

    @Test
    @DisplayName("should delete lesson successfully")
    void shouldDeleteLesson() {
        UUID id = UUID.randomUUID();
        when(lessonRepository.existsById(id)).thenReturn(true);

        useCase.delete(id);

        verify(lessonRepository).deleteById(id);
    }

    @Test
    @DisplayName("should throw when lesson not found")
    void shouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(lessonRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> useCase.delete(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Lesson");

        verify(lessonRepository, never()).deleteById(any());
    }
}
