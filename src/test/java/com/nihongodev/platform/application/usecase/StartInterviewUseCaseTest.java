package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.StartInterviewCommand;
import com.nihongodev.platform.application.dto.InterviewSessionDto;
import com.nihongodev.platform.application.port.out.EventPublisherPort;
import com.nihongodev.platform.application.port.out.InterviewQuestionRepositoryPort;
import com.nihongodev.platform.application.port.out.InterviewSessionRepositoryPort;
import com.nihongodev.platform.domain.exception.BusinessException;
import com.nihongodev.platform.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StartInterviewUseCase")
class StartInterviewUseCaseTest {

    @Mock private InterviewSessionRepositoryPort sessionRepository;
    @Mock private InterviewQuestionRepositoryPort questionRepository;
    @Mock private EventPublisherPort eventPublisher;

    private StartInterviewUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new StartInterviewUseCase(sessionRepository, questionRepository, eventPublisher);
    }

    @Test
    @DisplayName("should start an interview session successfully")
    void shouldStartSession() {
        UUID userId = UUID.randomUUID();
        StartInterviewCommand command = new StartInterviewCommand(InterviewType.HR_JAPANESE, InterviewDifficulty.BEGINNER);

        List<InterviewQuestion> questions = List.of(
                InterviewQuestion.create(InterviewType.HR_JAPANESE, InterviewDifficulty.BEGINNER, InterviewPhase.INTRODUCTION, "Q1", "Q1JP", "A1", List.of("k1"), "criteria", 120, 1),
                InterviewQuestion.create(InterviewType.HR_JAPANESE, InterviewDifficulty.BEGINNER, InterviewPhase.MAIN_QUESTIONS, "Q2", "Q2JP", "A2", List.of("k2"), "criteria", 120, 2),
                InterviewQuestion.create(InterviewType.HR_JAPANESE, InterviewDifficulty.BEGINNER, InterviewPhase.MAIN_QUESTIONS, "Q3", "Q3JP", "A3", List.of("k3"), "criteria", 120, 3)
        );

        when(questionRepository.findByTypeAndDifficulty(InterviewType.HR_JAPANESE, InterviewDifficulty.BEGINNER))
                .thenReturn(questions);
        when(sessionRepository.save(any(InterviewSession.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(eventPublisher).publish(anyString(), any());

        InterviewSessionDto result = useCase.start(userId, command);

        assertThat(result).isNotNull();
        assertThat(result.interviewType()).isEqualTo(InterviewType.HR_JAPANESE);
        assertThat(result.difficulty()).isEqualTo(InterviewDifficulty.BEGINNER);
        assertThat(result.status()).isEqualTo(SessionStatus.IN_PROGRESS);
        assertThat(result.totalQuestions()).isEqualTo(3);
        assertThat(result.currentQuestionIndex()).isEqualTo(0);
        verify(sessionRepository).save(any(InterviewSession.class));
        verify(eventPublisher).publish(anyString(), any());
    }

    @Test
    @DisplayName("should throw when no questions available")
    void shouldThrowWhenNoQuestions() {
        UUID userId = UUID.randomUUID();
        StartInterviewCommand command = new StartInterviewCommand(InterviewType.TECH_AWS, InterviewDifficulty.ADVANCED);

        when(questionRepository.findByTypeAndDifficulty(InterviewType.TECH_AWS, InterviewDifficulty.ADVANCED))
                .thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> useCase.start(userId, command))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("No questions available");
    }

    @Test
    @DisplayName("should limit questions to 8 maximum")
    void shouldLimitQuestionsTo8() {
        UUID userId = UUID.randomUUID();
        StartInterviewCommand command = new StartInterviewCommand(InterviewType.TECH_JAVA, InterviewDifficulty.BEGINNER);

        List<InterviewQuestion> manyQuestions = new java.util.ArrayList<>();
        for (int i = 0; i < 15; i++) {
            manyQuestions.add(InterviewQuestion.create(InterviewType.TECH_JAVA, InterviewDifficulty.BEGINNER,
                    InterviewPhase.MAIN_QUESTIONS, "Q" + i, "QJP" + i, "A" + i, List.of("k"), "c", 120, i));
        }

        when(questionRepository.findByTypeAndDifficulty(InterviewType.TECH_JAVA, InterviewDifficulty.BEGINNER))
                .thenReturn(manyQuestions);
        when(sessionRepository.save(any(InterviewSession.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(eventPublisher).publish(anyString(), any());

        InterviewSessionDto result = useCase.start(userId, command);

        assertThat(result.totalQuestions()).isEqualTo(8);
    }

    @Test
    @DisplayName("should start with INTRODUCTION phase")
    void shouldStartWithIntroductionPhase() {
        UUID userId = UUID.randomUUID();
        StartInterviewCommand command = new StartInterviewCommand(InterviewType.SELF_INTRODUCTION, InterviewDifficulty.INTERMEDIATE);

        List<InterviewQuestion> questions = List.of(
                InterviewQuestion.create(InterviewType.SELF_INTRODUCTION, InterviewDifficulty.INTERMEDIATE,
                        InterviewPhase.INTRODUCTION, "Introduce yourself", "自己紹介をしてください", "model", List.of("名前"), "criteria", 120, 1)
        );

        when(questionRepository.findByTypeAndDifficulty(InterviewType.SELF_INTRODUCTION, InterviewDifficulty.INTERMEDIATE))
                .thenReturn(questions);
        when(sessionRepository.save(any(InterviewSession.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(eventPublisher).publish(anyString(), any());

        InterviewSessionDto result = useCase.start(userId, command);

        assertThat(result.currentPhase()).isEqualTo(InterviewPhase.INTRODUCTION);
    }
}
