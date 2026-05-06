package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.AddQuestionCommand;
import com.nihongodev.platform.application.command.CreateQuizCommand;
import com.nihongodev.platform.application.dto.QuestionDto;
import com.nihongodev.platform.application.dto.QuizDto;
import com.nihongodev.platform.application.port.out.QuestionRepositoryPort;
import com.nihongodev.platform.application.port.out.QuizRepositoryPort;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateQuizUseCase")
class CreateQuizUseCaseTest {

    @Mock private QuizRepositoryPort quizRepository;
    @Mock private QuestionRepositoryPort questionRepository;

    private CreateQuizUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateQuizUseCase(quizRepository, questionRepository);
    }

    @Test
    @DisplayName("should create a quiz successfully")
    void shouldCreateQuiz() {
        CreateQuizCommand command = new CreateQuizCommand(
                UUID.randomUUID(), "IT Japanese Basics", "Basic IT vocabulary quiz",
                "N5", QuizMode.CLASSIC, 300, 3, 60
        );

        when(quizRepository.save(any(Quiz.class))).thenAnswer(inv -> inv.getArgument(0));

        QuizDto result = useCase.create(command);

        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("IT Japanese Basics");
        assertThat(result.mode()).isEqualTo(QuizMode.CLASSIC);
        assertThat(result.timeLimitSeconds()).isEqualTo(300);
        assertThat(result.maxAttempts()).isEqualTo(3);
        assertThat(result.passingScore()).isEqualTo(60);
        assertThat(result.published()).isTrue();
        verify(quizRepository).save(any(Quiz.class));
    }

    @Test
    @DisplayName("should add a question to a quiz")
    void shouldAddQuestion() {
        UUID quizId = UUID.randomUUID();

        AddQuestionCommand command = new AddQuestionCommand(
                quizId, "What is コンパイラ?", "compiler", "コンパイラ means compiler",
                QuestionType.MULTIPLE_CHOICE, DifficultyLevel.EASY,
                List.of("compiler", "interpreter", "debugger", "linker"), 2, 30, 1
        );

        when(quizRepository.existsById(quizId)).thenReturn(true);
        when(questionRepository.save(any(Question.class))).thenAnswer(inv -> inv.getArgument(0));

        QuestionDto result = useCase.addQuestion(command);

        assertThat(result).isNotNull();
        assertThat(result.content()).isEqualTo("What is コンパイラ?");
        assertThat(result.questionType()).isEqualTo(QuestionType.MULTIPLE_CHOICE);
        assertThat(result.points()).isEqualTo(2);
        verify(questionRepository).save(any(Question.class));
    }

    @Test
    @DisplayName("should throw when quiz not found for adding question")
    void shouldThrowWhenQuizNotFoundForQuestion() {
        UUID quizId = UUID.randomUUID();
        when(quizRepository.existsById(quizId)).thenReturn(false);

        AddQuestionCommand command = new AddQuestionCommand(
                quizId, "content", "answer", "explanation",
                QuestionType.MULTIPLE_CHOICE, DifficultyLevel.EASY,
                List.of("a", "b"), 1, 30, 1
        );

        assertThatThrownBy(() -> useCase.addQuestion(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Quiz");
    }

    @Test
    @DisplayName("should create quiz with survival mode")
    void shouldCreateSurvivalQuiz() {
        CreateQuizCommand command = new CreateQuizCommand(
                UUID.randomUUID(), "Survival Challenge", "Test your limits",
                "N4", QuizMode.SURVIVAL, 0, 0, 70
        );

        when(quizRepository.save(any(Quiz.class))).thenAnswer(inv -> inv.getArgument(0));

        QuizDto result = useCase.create(command);

        assertThat(result.mode()).isEqualTo(QuizMode.SURVIVAL);
        assertThat(result.passingScore()).isEqualTo(70);
    }
}
