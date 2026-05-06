package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.QuizResultDto;
import com.nihongodev.platform.application.port.out.*;
import com.nihongodev.platform.domain.exception.BusinessException;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.*;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CompleteQuizUseCase")
class CompleteQuizUseCaseTest {

    @Mock private QuizAttemptRepositoryPort attemptRepository;
    @Mock private QuizRepositoryPort quizRepository;
    @Mock private QuestionRepositoryPort questionRepository;
    @Mock private QuizResultRepositoryPort resultRepository;
    @Mock private EventPublisherPort eventPublisher;

    private CompleteQuizUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CompleteQuizUseCase(attemptRepository, quizRepository,
                questionRepository, resultRepository, eventPublisher);
    }

    @Test
    @DisplayName("should complete a quiz and return result")
    void shouldCompleteQuiz() {
        UUID userId = UUID.randomUUID();
        UUID quizId = UUID.randomUUID();
        UUID attemptId = UUID.randomUUID();

        QuizAttempt attempt = QuizAttempt.start(quizId, userId, QuizMode.CLASSIC);
        attempt.setId(attemptId);
        attempt.recordCorrectAnswer();
        attempt.recordCorrectAnswer();
        attempt.recordCorrectAnswer();

        Quiz quiz = Quiz.create(UUID.randomUUID(), "Java Terms", "desc", "N5",
                QuizMode.CLASSIC, 300, 3, 60);
        quiz.setId(quizId);

        List<Question> questions = List.of(
                Question.create(quizId, "Q1", "A1", "E1", QuestionType.MULTIPLE_CHOICE, DifficultyLevel.EASY, List.of("A1", "B1"), 2, 30, 1),
                Question.create(quizId, "Q2", "A2", "E2", QuestionType.MULTIPLE_CHOICE, DifficultyLevel.MEDIUM, List.of("A2", "B2"), 3, 30, 2),
                Question.create(quizId, "Q3", "A3", "E3", QuestionType.MULTIPLE_CHOICE, DifficultyLevel.HARD, List.of("A3", "B3"), 4, 30, 3)
        );

        when(attemptRepository.findById(attemptId)).thenReturn(Optional.of(attempt));
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
        when(questionRepository.findByQuizId(quizId)).thenReturn(questions);
        when(attemptRepository.save(any(QuizAttempt.class))).thenAnswer(inv -> inv.getArgument(0));
        when(resultRepository.save(any(QuizResult.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(eventPublisher).publish(anyString(), any());

        QuizResultDto result = useCase.complete(userId, attemptId);

        assertThat(result).isNotNull();
        assertThat(result.totalQuestions()).isEqualTo(3);
        assertThat(result.maxStreak()).isEqualTo(3);
        assertThat(result.completedAt()).isNotNull();
        verify(eventPublisher).publish(anyString(), any());
        verify(resultRepository).save(any(QuizResult.class));
    }

    @Test
    @DisplayName("should throw when attempt not found")
    void shouldThrowWhenAttemptNotFound() {
        UUID attemptId = UUID.randomUUID();
        when(attemptRepository.findById(attemptId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.complete(UUID.randomUUID(), attemptId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("QuizAttempt");
    }

    @Test
    @DisplayName("should throw when attempt belongs to another user")
    void shouldThrowWhenUnauthorized() {
        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        UUID attemptId = UUID.randomUUID();

        QuizAttempt attempt = QuizAttempt.start(UUID.randomUUID(), otherUserId, QuizMode.CLASSIC);
        attempt.setId(attemptId);

        when(attemptRepository.findById(attemptId)).thenReturn(Optional.of(attempt));

        assertThatThrownBy(() -> useCase.complete(userId, attemptId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Unauthorized");
    }

    @Test
    @DisplayName("should throw when attempt already completed")
    void shouldThrowWhenAlreadyCompleted() {
        UUID userId = UUID.randomUUID();
        UUID attemptId = UUID.randomUUID();

        QuizAttempt attempt = QuizAttempt.start(UUID.randomUUID(), userId, QuizMode.CLASSIC);
        attempt.setId(attemptId);
        attempt.complete(100);

        when(attemptRepository.findById(attemptId)).thenReturn(Optional.of(attempt));

        assertThatThrownBy(() -> useCase.complete(userId, attemptId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already completed");
    }

    @Test
    @DisplayName("should allow completing a game-over attempt")
    void shouldAllowCompletingGameOver() {
        UUID userId = UUID.randomUUID();
        UUID quizId = UUID.randomUUID();
        UUID attemptId = UUID.randomUUID();

        QuizAttempt attempt = QuizAttempt.start(quizId, userId, QuizMode.SURVIVAL);
        attempt.setId(attemptId);
        attempt.recordIncorrectAnswer();
        attempt.recordIncorrectAnswer();
        attempt.recordIncorrectAnswer();

        Quiz quiz = Quiz.create(UUID.randomUUID(), "Survival Quiz", "desc", "N4",
                QuizMode.SURVIVAL, 0, 0, 60);
        quiz.setId(quizId);

        when(attemptRepository.findById(attemptId)).thenReturn(Optional.of(attempt));
        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
        when(questionRepository.findByQuizId(quizId)).thenReturn(List.of());
        when(attemptRepository.save(any(QuizAttempt.class))).thenAnswer(inv -> inv.getArgument(0));
        when(resultRepository.save(any(QuizResult.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(eventPublisher).publish(anyString(), any());

        QuizResultDto result = useCase.complete(userId, attemptId);

        assertThat(result).isNotNull();
    }
}
