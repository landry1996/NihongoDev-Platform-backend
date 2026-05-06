package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.QuizAttemptDto;
import com.nihongodev.platform.application.port.out.QuizAttemptRepositoryPort;
import com.nihongodev.platform.application.port.out.QuizRepositoryPort;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("StartQuizUseCase")
class StartQuizUseCaseTest {

    @Mock private QuizRepositoryPort quizRepository;
    @Mock private QuizAttemptRepositoryPort attemptRepository;

    private StartQuizUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new StartQuizUseCase(quizRepository, attemptRepository);
    }

    @Test
    @DisplayName("should start a quiz attempt successfully")
    void shouldStartQuizAttempt() {
        UUID userId = UUID.randomUUID();
        UUID quizId = UUID.randomUUID();

        Quiz quiz = Quiz.create(UUID.randomUUID(), "Java IT Terms", "desc", "N5",
                QuizMode.CLASSIC, 300, 3, 60);
        quiz.setId(quizId);

        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
        when(attemptRepository.findByUserIdAndQuizId(userId, quizId)).thenReturn(List.of());
        when(attemptRepository.save(any(QuizAttempt.class))).thenAnswer(inv -> inv.getArgument(0));

        QuizAttemptDto result = useCase.start(userId, quizId, QuizMode.CLASSIC);

        assertThat(result).isNotNull();
        assertThat(result.quizId()).isEqualTo(quizId);
        assertThat(result.mode()).isEqualTo(QuizMode.CLASSIC);
        assertThat(result.status()).isEqualTo(AttemptStatus.IN_PROGRESS);
        assertThat(result.currentStreak()).isEqualTo(0);
        verify(attemptRepository).save(any(QuizAttempt.class));
    }

    @Test
    @DisplayName("should use quiz default mode when no mode specified")
    void shouldUseDefaultMode() {
        UUID userId = UUID.randomUUID();
        UUID quizId = UUID.randomUUID();

        Quiz quiz = Quiz.create(UUID.randomUUID(), "Survival Quiz", "desc", "N4",
                QuizMode.SURVIVAL, 0, 0, 70);
        quiz.setId(quizId);

        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
        when(attemptRepository.save(any(QuizAttempt.class))).thenAnswer(inv -> inv.getArgument(0));

        QuizAttemptDto result = useCase.start(userId, quizId, null);

        assertThat(result.mode()).isEqualTo(QuizMode.SURVIVAL);
    }

    @Test
    @DisplayName("should throw when quiz not found")
    void shouldThrowWhenQuizNotFound() {
        UUID quizId = UUID.randomUUID();
        when(quizRepository.findById(quizId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.start(UUID.randomUUID(), quizId, null))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Quiz");
    }

    @Test
    @DisplayName("should throw when max attempts reached")
    void shouldThrowWhenMaxAttemptsReached() {
        UUID userId = UUID.randomUUID();
        UUID quizId = UUID.randomUUID();

        Quiz quiz = Quiz.create(UUID.randomUUID(), "Limited Quiz", "desc", "N5",
                QuizMode.CLASSIC, 300, 2, 60);
        quiz.setId(quizId);

        QuizAttempt attempt1 = QuizAttempt.start(quizId, userId, QuizMode.CLASSIC);
        QuizAttempt attempt2 = QuizAttempt.start(quizId, userId, QuizMode.CLASSIC);

        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
        when(attemptRepository.findByUserIdAndQuizId(userId, quizId)).thenReturn(List.of(attempt1, attempt2));

        assertThatThrownBy(() -> useCase.start(userId, quizId, QuizMode.CLASSIC))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Maximum attempts reached");
    }

    @Test
    @DisplayName("should allow unlimited attempts when maxAttempts is 0")
    void shouldAllowUnlimitedAttempts() {
        UUID userId = UUID.randomUUID();
        UUID quizId = UUID.randomUUID();

        Quiz quiz = Quiz.create(UUID.randomUUID(), "Unlimited Quiz", "desc", "N5",
                QuizMode.CLASSIC, 300, 0, 60);
        quiz.setId(quizId);

        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
        when(attemptRepository.save(any(QuizAttempt.class))).thenAnswer(inv -> inv.getArgument(0));

        QuizAttemptDto result = useCase.start(userId, quizId, QuizMode.CLASSIC);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("should initialize survival mode with 3 lives")
    void shouldInitializeSurvivalWithLives() {
        UUID userId = UUID.randomUUID();
        UUID quizId = UUID.randomUUID();

        Quiz quiz = Quiz.create(UUID.randomUUID(), "Survival", "desc", "N4",
                QuizMode.SURVIVAL, 0, 0, 60);
        quiz.setId(quizId);

        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
        when(attemptRepository.save(any(QuizAttempt.class))).thenAnswer(inv -> inv.getArgument(0));

        QuizAttemptDto result = useCase.start(userId, quizId, QuizMode.SURVIVAL);

        assertThat(result.livesRemaining()).isEqualTo(3);
    }
}
