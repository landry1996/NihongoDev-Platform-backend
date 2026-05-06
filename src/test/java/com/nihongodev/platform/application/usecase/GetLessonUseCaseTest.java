package com.nihongodev.platform.application.usecase;

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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetLessonUseCase")
class GetLessonUseCaseTest {

    @Mock private LessonRepositoryPort lessonRepository;

    private GetLessonUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetLessonUseCase(lessonRepository);
    }

    @Test
    @DisplayName("should get lesson by ID")
    void shouldGetById() {
        UUID id = UUID.randomUUID();
        Lesson lesson = Lesson.create("Test", "Desc", LessonType.VOCABULARY, LessonLevel.A2, "content", 1);
        lesson.setId(id);

        when(lessonRepository.findById(id)).thenReturn(Optional.of(lesson));

        LessonDto result = useCase.getById(id);

        assertThat(result.id()).isEqualTo(id);
        assertThat(result.title()).isEqualTo("Test");
    }

    @Test
    @DisplayName("should throw when lesson not found by ID")
    void shouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(lessonRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.getById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Lesson");
    }

    @Test
    @DisplayName("should get lessons by type")
    void shouldGetByType() {
        Lesson l1 = Lesson.create("Hiragana 1", "Desc", LessonType.HIRAGANA, LessonLevel.BEGINNER, "c", 1);
        Lesson l2 = Lesson.create("Hiragana 2", "Desc", LessonType.HIRAGANA, LessonLevel.A1, "c", 2);

        when(lessonRepository.findByType(LessonType.HIRAGANA)).thenReturn(List.of(l1, l2));

        List<LessonDto> results = useCase.getByType(LessonType.HIRAGANA);

        assertThat(results).hasSize(2);
        assertThat(results).allMatch(dto -> dto.type() == LessonType.HIRAGANA);
    }

    @Test
    @DisplayName("should get lessons by level")
    void shouldGetByLevel() {
        Lesson l1 = Lesson.create("Lesson 1", "Desc", LessonType.GRAMMAR, LessonLevel.B1, "c", 1);

        when(lessonRepository.findByLevel(LessonLevel.B1)).thenReturn(List.of(l1));

        List<LessonDto> results = useCase.getByLevel(LessonLevel.B1);

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().level()).isEqualTo(LessonLevel.B1);
    }

    @Test
    @DisplayName("should get published lessons")
    void shouldGetPublished() {
        Lesson l1 = Lesson.create("Published", "Desc", LessonType.KANJI, LessonLevel.A2, "c", 1);

        when(lessonRepository.findPublished()).thenReturn(List.of(l1));

        List<LessonDto> results = useCase.getPublished();

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().published()).isTrue();
    }
}
