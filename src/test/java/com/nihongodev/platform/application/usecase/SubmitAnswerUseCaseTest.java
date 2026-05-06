package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.SubmitAnswerCommand;
import com.nihongodev.platform.application.dto.AnswerResultDto;
import com.nihongodev.platform.application.port.out.QuestionRepositoryPort;
import com.nihongodev.platform.application.port.out.QuizAttemptRepositoryPort;
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
@DisplayName("SubmitAnswerUseCase")
class SubmitAnswerUseCaseTest {

    @Mock private QuizAttemptRepositoryPort attemptRepository;
    @Mock private QuestionRepositoryPort questionRepository;

    private SubmitAnswerUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new SubmitAnswerUseCase(attemptRepository, questionRepository);
    }

    @Test
    @DisplayName("should return correct result for multiple choice correct answer")
    void shouldHandleCorrectMultipleChoice() {
        UUID attemptId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();

        QuizAttempt attempt = QuizAttempt.start(UUID.randomUUID(), UUID.randomUUID(), QuizMode.CLASSIC);
        attempt.setId(attemptId);

        Question question = Question.create(UUID.randomUUID(), "What is 変数?",
                "variable", "変数 means variable in Japanese",
                QuestionType.MULTIPLE_CHOICE, DifficultyLevel.EASY,
                List.of("variable", "function", "class", "object"), 2, 30, 1);
        question.setId(questionId);

        when(attemptRepository.findById(attemptId)).thenReturn(Optional.of(attempt));
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(attemptRepository.save(any(QuizAttempt.class))).thenAnswer(inv -> inv.getArgument(0));

        SubmitAnswerCommand command = new SubmitAnswerCommand(attemptId, questionId, "variable", 5);
        AnswerResultDto result = useCase.submit(command);

        assertThat(result.correct()).isTrue();
        assertThat(result.pointsEarned()).isGreaterThan(0);
        assertThat(result.correctAnswer()).isEqualTo("variable");
        assertThat(result.currentStreak()).isEqualTo(1);
        assertThat(result.gameOver()).isFalse();
        verify(attemptRepository).save(any(QuizAttempt.class));
    }

    @Test
    @DisplayName("should return incorrect result for wrong answer")
    void shouldHandleIncorrectAnswer() {
        UUID attemptId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();

        QuizAttempt attempt = QuizAttempt.start(UUID.randomUUID(), UUID.randomUUID(), QuizMode.CLASSIC);
        attempt.setId(attemptId);

        Question question = Question.create(UUID.randomUUID(), "What is 関数?",
                "function", "関数 means function",
                QuestionType.MULTIPLE_CHOICE, DifficultyLevel.MEDIUM,
                List.of("variable", "function", "class", "object"), 2, 30, 1);
        question.setId(questionId);

        when(attemptRepository.findById(attemptId)).thenReturn(Optional.of(attempt));
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(attemptRepository.save(any(QuizAttempt.class))).thenAnswer(inv -> inv.getArgument(0));

        SubmitAnswerCommand command = new SubmitAnswerCommand(attemptId, questionId, "variable", 5);
        AnswerResultDto result = useCase.submit(command);

        assertThat(result.correct()).isFalse();
        assertThat(result.pointsEarned()).isEqualTo(0);
        assertThat(result.currentStreak()).isEqualTo(0);
    }

    @Test
    @DisplayName("should handle true/false correct answer")
    void shouldHandleTrueFalseCorrect() {
        UUID attemptId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();

        QuizAttempt attempt = QuizAttempt.start(UUID.randomUUID(), UUID.randomUUID(), QuizMode.CLASSIC);
        attempt.setId(attemptId);

        Question question = Question.create(UUID.randomUUID(), "オブジェクト means object?",
                "true", "Correct, オブジェクト means object",
                QuestionType.TRUE_FALSE, DifficultyLevel.EASY,
                List.of("true", "false"), 1, 15, 1);
        question.setId(questionId);

        when(attemptRepository.findById(attemptId)).thenReturn(Optional.of(attempt));
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(attemptRepository.save(any(QuizAttempt.class))).thenAnswer(inv -> inv.getArgument(0));

        SubmitAnswerCommand command = new SubmitAnswerCommand(attemptId, questionId, "TRUE", 3);
        AnswerResultDto result = useCase.submit(command);

        assertThat(result.correct()).isTrue();
    }

    @Test
    @DisplayName("should handle text input with normalization")
    void shouldHandleTextInputWithNormalization() {
        UUID attemptId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();

        QuizAttempt attempt = QuizAttempt.start(UUID.randomUUID(), UUID.randomUUID(), QuizMode.CLASSIC);
        attempt.setId(attemptId);

        Question question = Question.create(UUID.randomUUID(), "Translate: デバッグ",
                "debug", "デバッグ means debug",
                QuestionType.TEXT_INPUT, DifficultyLevel.MEDIUM,
                List.of(), 3, 45, 1);
        question.setId(questionId);

        when(attemptRepository.findById(attemptId)).thenReturn(Optional.of(attempt));
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(attemptRepository.save(any(QuizAttempt.class))).thenAnswer(inv -> inv.getArgument(0));

        SubmitAnswerCommand command = new SubmitAnswerCommand(attemptId, questionId, "  Debug  ", 8);
        AnswerResultDto result = useCase.submit(command);

        assertThat(result.correct()).isTrue();
    }

    @Test
    @DisplayName("should trigger game over in survival mode after losing all lives")
    void shouldTriggerGameOverInSurvival() {
        UUID attemptId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();

        QuizAttempt attempt = QuizAttempt.start(UUID.randomUUID(), UUID.randomUUID(), QuizMode.SURVIVAL);
        attempt.setId(attemptId);
        attempt.recordIncorrectAnswer();
        attempt.recordIncorrectAnswer();

        Question question = Question.create(UUID.randomUUID(), "What is クラス?",
                "class", "クラス means class",
                QuestionType.MULTIPLE_CHOICE, DifficultyLevel.EASY,
                List.of("class", "method", "interface", "enum"), 1, 30, 1);
        question.setId(questionId);

        when(attemptRepository.findById(attemptId)).thenReturn(Optional.of(attempt));
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(attemptRepository.save(any(QuizAttempt.class))).thenAnswer(inv -> inv.getArgument(0));

        SubmitAnswerCommand command = new SubmitAnswerCommand(attemptId, questionId, "method", 4);
        AnswerResultDto result = useCase.submit(command);

        assertThat(result.correct()).isFalse();
        assertThat(result.gameOver()).isTrue();
    }

    @Test
    @DisplayName("should throw when attempt not found")
    void shouldThrowWhenAttemptNotFound() {
        UUID attemptId = UUID.randomUUID();
        when(attemptRepository.findById(attemptId)).thenReturn(Optional.empty());

        SubmitAnswerCommand command = new SubmitAnswerCommand(attemptId, UUID.randomUUID(), "answer", 5);

        assertThatThrownBy(() -> useCase.submit(command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("QuizAttempt");
    }

    @Test
    @DisplayName("should throw when attempt is not in progress")
    void shouldThrowWhenAttemptNotInProgress() {
        UUID attemptId = UUID.randomUUID();
        QuizAttempt attempt = QuizAttempt.start(UUID.randomUUID(), UUID.randomUUID(), QuizMode.CLASSIC);
        attempt.setId(attemptId);
        attempt.complete(120);

        when(attemptRepository.findById(attemptId)).thenReturn(Optional.of(attempt));

        SubmitAnswerCommand command = new SubmitAnswerCommand(attemptId, UUID.randomUUID(), "answer", 5);

        assertThatThrownBy(() -> useCase.submit(command))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("no longer in progress");
    }

    @Test
    @DisplayName("should increase streak multiplier with consecutive correct answers")
    void shouldIncreaseStreakMultiplier() {
        UUID attemptId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();

        QuizAttempt attempt = QuizAttempt.start(UUID.randomUUID(), UUID.randomUUID(), QuizMode.CLASSIC);
        attempt.setId(attemptId);
        attempt.recordCorrectAnswer();
        attempt.recordCorrectAnswer();

        Question question = Question.create(UUID.randomUUID(), "What is メソッド?",
                "method", "メソッド means method",
                QuestionType.MULTIPLE_CHOICE, DifficultyLevel.MEDIUM,
                List.of("method", "module", "model", "mock"), 2, 30, 3);
        question.setId(questionId);

        when(attemptRepository.findById(attemptId)).thenReturn(Optional.of(attempt));
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(attemptRepository.save(any(QuizAttempt.class))).thenAnswer(inv -> inv.getArgument(0));

        SubmitAnswerCommand command = new SubmitAnswerCommand(attemptId, questionId, "method", 3);
        AnswerResultDto result = useCase.submit(command);

        assertThat(result.correct()).isTrue();
        assertThat(result.streakMultiplier()).isGreaterThan(1.0);
        assertThat(result.currentStreak()).isEqualTo(3);
    }
}
