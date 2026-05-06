package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.SubmitInterviewAnswerCommand;
import com.nihongodev.platform.application.dto.InterviewAnswerResultDto;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SubmitInterviewAnswerUseCase")
class SubmitInterviewAnswerUseCaseTest {

    @Mock private InterviewSessionRepositoryPort sessionRepository;
    @Mock private InterviewQuestionRepositoryPort questionRepository;
    @Mock private InterviewAnswerRepositoryPort answerRepository;
    @Mock private EventPublisherPort eventPublisher;

    private SubmitInterviewAnswerUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new SubmitInterviewAnswerUseCase(sessionRepository, questionRepository,
                answerRepository, eventPublisher);
    }

    @Test
    @DisplayName("should submit answer and return feedback with scores")
    void shouldSubmitAnswerWithFeedback() {
        UUID userId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();

        InterviewSession session = InterviewSession.start(userId, InterviewType.HR_JAPANESE,
                InterviewDifficulty.BEGINNER, 3);
        session.setId(sessionId);
        session.advanceQuestion(); // simulate first question answered

        InterviewQuestion question = InterviewQuestion.create(
                InterviewType.HR_JAPANESE, InterviewDifficulty.BEGINNER, InterviewPhase.MAIN_QUESTIONS,
                "What are your strengths?", "あなたの長所は何ですか？",
                "問題解決能力が私の長所です。チームと協力して複雑な課題を解決するのが得意です。",
                List.of("長所", "問題解決", "チーム"), "Concrete example", 120, 1
        );
        question.setId(questionId);

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(answerRepository.save(any(InterviewAnswer.class))).thenAnswer(inv -> inv.getArgument(0));
        when(sessionRepository.save(any(InterviewSession.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(eventPublisher).publish(anyString(), any());

        SubmitInterviewAnswerCommand command = new SubmitInterviewAnswerCommand(
                sessionId, questionId, "私の長所は問題解決能力です。チームと協力して働くことが得意です。", 45
        );

        InterviewAnswerResultDto result = useCase.submit(userId, command);

        assertThat(result).isNotNull();
        assertThat(result.questionId()).isEqualTo(questionId);
        assertThat(result.overallScore()).isGreaterThan(0);
        assertThat(result.feedback()).isNotNull();
        assertThat(result.feedback().languageScore()).isGreaterThan(0);
        assertThat(result.feedback().technicalScore()).isGreaterThan(0);
        verify(answerRepository).save(any(InterviewAnswer.class));
        verify(eventPublisher).publish(anyString(), any());
    }

    @Test
    @DisplayName("should throw when session not found")
    void shouldThrowWhenSessionNotFound() {
        UUID sessionId = UUID.randomUUID();
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        SubmitInterviewAnswerCommand command = new SubmitInterviewAnswerCommand(
                sessionId, UUID.randomUUID(), "answer", 30
        );

        assertThatThrownBy(() -> useCase.submit(UUID.randomUUID(), command))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("InterviewSession");
    }

    @Test
    @DisplayName("should throw when session belongs to another user")
    void shouldThrowWhenUnauthorized() {
        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();

        InterviewSession session = InterviewSession.start(otherUserId, InterviewType.HR_JAPANESE,
                InterviewDifficulty.BEGINNER, 3);
        session.setId(sessionId);

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        SubmitInterviewAnswerCommand command = new SubmitInterviewAnswerCommand(
                sessionId, UUID.randomUUID(), "answer", 30
        );

        assertThatThrownBy(() -> useCase.submit(userId, command))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Unauthorized");
    }

    @Test
    @DisplayName("should throw when session is no longer in progress")
    void shouldThrowWhenSessionCompleted() {
        UUID userId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();

        InterviewSession session = InterviewSession.start(userId, InterviewType.HR_JAPANESE,
                InterviewDifficulty.BEGINNER, 3);
        session.setId(sessionId);
        session.complete();

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        SubmitInterviewAnswerCommand command = new SubmitInterviewAnswerCommand(
                sessionId, UUID.randomUUID(), "answer", 30
        );

        assertThatThrownBy(() -> useCase.submit(userId, command))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("no longer in progress");
    }

    @Test
    @DisplayName("should detect session complete when last question answered")
    void shouldDetectSessionComplete() {
        UUID userId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();

        InterviewSession session = InterviewSession.start(userId, InterviewType.TECH_JAVA,
                InterviewDifficulty.BEGINNER, 1);
        session.setId(sessionId);

        InterviewQuestion question = InterviewQuestion.create(
                InterviewType.TECH_JAVA, InterviewDifficulty.BEGINNER, InterviewPhase.MAIN_QUESTIONS,
                "What is JVM?", "JVMとは何ですか？", "Java Virtual Machine",
                List.of("JVM", "virtual", "machine"), "Technical", 120, 1
        );
        question.setId(questionId);

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(answerRepository.save(any(InterviewAnswer.class))).thenAnswer(inv -> inv.getArgument(0));
        when(sessionRepository.save(any(InterviewSession.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(eventPublisher).publish(anyString(), any());

        SubmitInterviewAnswerCommand command = new SubmitInterviewAnswerCommand(
                sessionId, questionId, "JVM is the Java Virtual Machine that executes bytecode", 60
        );

        InterviewAnswerResultDto result = useCase.submit(userId, command);

        assertThat(result.sessionComplete()).isTrue();
    }

    @Test
    @DisplayName("should evaluate with keywords for HR type")
    void shouldEvaluateWithKeywords() {
        UUID userId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();

        InterviewSession session = InterviewSession.start(userId, InterviewType.HR_JAPANESE,
                InterviewDifficulty.BEGINNER, 5);
        session.setId(sessionId);
        session.advanceQuestion();

        InterviewQuestion question = InterviewQuestion.create(
                InterviewType.HR_JAPANESE, InterviewDifficulty.BEGINNER, InterviewPhase.MAIN_QUESTIONS,
                "Why apply?", "志望動機は？", "御社の技術力に魅力を感じています",
                List.of("御社", "技術", "魅力"), "Specific reference", 150, 1
        );
        question.setId(questionId);

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(answerRepository.save(any(InterviewAnswer.class))).thenAnswer(inv -> inv.getArgument(0));
        when(sessionRepository.save(any(InterviewSession.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(eventPublisher).publish(anyString(), any());

        SubmitInterviewAnswerCommand command = new SubmitInterviewAnswerCommand(
                sessionId, questionId, "御社の技術力と成長環境に魅力を感じています。", 40
        );

        InterviewAnswerResultDto result = useCase.submit(userId, command);

        assertThat(result.feedback().technicalScore()).isGreaterThan(50);
    }
}
