package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.CreateLessonCommand;
import com.nihongodev.platform.application.dto.LessonDto;
import com.nihongodev.platform.application.port.out.LessonRepositoryPort;
import com.nihongodev.platform.domain.model.Lesson;
import com.nihongodev.platform.domain.model.LessonLevel;
import com.nihongodev.platform.domain.model.LessonType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateLessonUseCase")
class CreateLessonUseCaseTest {

    @Mock private LessonRepositoryPort lessonRepository;

    private CreateLessonUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateLessonUseCase(lessonRepository);
    }

    @Test
    @DisplayName("should create a lesson successfully")
    void shouldCreateLesson() {
        CreateLessonCommand command = new CreateLessonCommand(
                "Hiragana Basics", "Learn basic hiragana characters",
                LessonType.HIRAGANA, LessonLevel.BEGINNER, "<p>Content</p>", 1
        );

        when(lessonRepository.save(any(Lesson.class))).thenAnswer(inv -> inv.getArgument(0));

        LessonDto result = useCase.create(command);

        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("Hiragana Basics");
        assertThat(result.description()).isEqualTo("Learn basic hiragana characters");
        assertThat(result.type()).isEqualTo(LessonType.HIRAGANA);
        assertThat(result.level()).isEqualTo(LessonLevel.BEGINNER);
        assertThat(result.published()).isTrue();
        assertThat(result.orderIndex()).isEqualTo(1);
        verify(lessonRepository).save(any(Lesson.class));
    }

    @Test
    @DisplayName("should create lesson with IT_JAPANESE type")
    void shouldCreateItJapaneseLesson() {
        CreateLessonCommand command = new CreateLessonCommand(
                "IT Meeting Vocabulary", "Technical meeting terms",
                LessonType.IT_JAPANESE, LessonLevel.B1, "Meeting vocabulary content", 5
        );

        when(lessonRepository.save(any(Lesson.class))).thenAnswer(inv -> inv.getArgument(0));

        LessonDto result = useCase.create(command);

        assertThat(result.type()).isEqualTo(LessonType.IT_JAPANESE);
        assertThat(result.level()).isEqualTo(LessonLevel.B1);
    }

    @Test
    @DisplayName("should set default published to true")
    void shouldDefaultPublishedTrue() {
        CreateLessonCommand command = new CreateLessonCommand(
                "Kanji N5", "N5 level kanji",
                LessonType.KANJI, LessonLevel.A1, "Kanji content", 0
        );

        when(lessonRepository.save(any(Lesson.class))).thenAnswer(inv -> inv.getArgument(0));

        LessonDto result = useCase.create(command);

        assertThat(result.published()).isTrue();
        assertThat(result.createdAt()).isNotNull();
    }
}
