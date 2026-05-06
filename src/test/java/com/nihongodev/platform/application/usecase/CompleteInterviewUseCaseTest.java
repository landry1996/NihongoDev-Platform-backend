package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.InterviewSessionDto;
import com.nihongodev.platform.application.port.out.EventPublisherPort;
import com.nihongodev.platform.application.port.out.InterviewSessionRepositoryPort;
import com.nihongodev.platform.domain.exception.BusinessException;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.*;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CompleteInterviewUseCase")
class CompleteInterviewUseCaseTest {

    @Mock private InterviewSessionRepositoryPort sessionRepository;
    @Mock private EventPublisherPort eventPublisher;

    private CompleteInterviewUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CompleteInterviewUseCase(sessionRepository, eventPublisher);
    }

    @Test
    @DisplayName("should complete session and calculate final scores")
    void shouldCompleteSession() {
        UUID userId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();

        InterviewSession session = InterviewSession.start(userId, InterviewType.TECH_JAVA,
                InterviewDifficulty.INTERMEDIATE, 5);
        session.setId(sessionId);
        session.advanceQuestion();
        session.addScore(70, 80, 65, 60);
        session.advanceQuestion();
        session.addScore(75, 85, 70, 65);

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(sessionRepository.save(any(InterviewSession.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(eventPublisher).publish(anyString(), any());

        InterviewSessionDto result = useCase.complete(userId, sessionId);

        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(SessionStatus.COMPLETED);
        assertThat(result.overallScore()).isGreaterThan(0);
        assertThat(result.completedAt()).isNotNull();
        verify(eventPublisher).publish(anyString(), any());
    }

    @Test
    @DisplayName("should mark as passed when score >= 60")
    void shouldMarkAsPassed() {
        UUID userId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();

        InterviewSession session = InterviewSession.start(userId, InterviewType.HR_JAPANESE,
                InterviewDifficulty.BEGINNER, 3);
        session.setId(sessionId);
        session.advanceQuestion();
        session.addScore(80, 75, 70, 65);

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(sessionRepository.save(any(InterviewSession.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(eventPublisher).publish(anyString(), any());

        InterviewSessionDto result = useCase.complete(userId, sessionId);

        assertThat(result.passed()).isTrue();
    }

    @Test
    @DisplayName("should throw when session not found")
    void shouldThrowWhenNotFound() {
        UUID sessionId = UUID.randomUUID();
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.complete(UUID.randomUUID(), sessionId))
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

        assertThatThrownBy(() -> useCase.complete(userId, sessionId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Unauthorized");
    }

    @Test
    @DisplayName("should throw when session already completed")
    void shouldThrowWhenAlreadyCompleted() {
        UUID userId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();

        InterviewSession session = InterviewSession.start(userId, InterviewType.BEHAVIORAL,
                InterviewDifficulty.INTERMEDIATE, 5);
        session.setId(sessionId);
        session.complete();

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        assertThatThrownBy(() -> useCase.complete(userId, sessionId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already completed");
    }
}
