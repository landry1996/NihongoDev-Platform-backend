package com.nihongodev.platform.infrastructure.kafka;

import com.nihongodev.platform.application.port.in.*;
import com.nihongodev.platform.domain.event.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProgressEventConsumer")
class ProgressEventConsumerTest {

    @Mock private UpdateProgressOnLessonCompletedPort lessonPort;
    @Mock private UpdateProgressOnQuizCompletedPort quizPort;
    @Mock private UpdateProgressOnInterviewCompletedPort interviewPort;
    @Mock private UpdateProgressOnCorrectionCompletedPort correctionPort;

    private ProgressEventConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new ProgressEventConsumer(lessonPort, quizPort, interviewPort, correctionPort);
    }

    @Test
    @DisplayName("should delegate LessonCompletedEvent to port")
    void shouldDelegateLessonEvent() {
        LessonCompletedEvent event = new LessonCompletedEvent(
                UUID.randomUUID(), UUID.randomUUID(), "Lesson 1", "GRAMMAR", "N5", LocalDateTime.now());

        consumer.handleLessonCompleted(event);

        verify(lessonPort).execute(event);
    }

    @Test
    @DisplayName("should delegate QuizCompletedEvent to port")
    void shouldDelegateQuizEvent() {
        QuizCompletedEvent event = new QuizCompletedEvent(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "Quiz 1",
                85.0, true, 5, "CLASSIC", LocalDateTime.now());

        consumer.handleQuizCompleted(event);

        verify(quizPort).execute(event);
    }

    @Test
    @DisplayName("should delegate InterviewCompletedEvent to port")
    void shouldDelegateInterviewEvent() {
        InterviewCompletedEvent event = new InterviewCompletedEvent(
                UUID.randomUUID(), UUID.randomUUID(), "TECHNICAL", 75.0, 600, true);

        consumer.handleInterviewCompleted(event);

        verify(interviewPort).execute(event);
    }

    @Test
    @DisplayName("should delegate TextCorrectedEvent to port")
    void shouldDelegateCorrectionEvent() {
        TextCorrectedEvent event = new TextCorrectedEvent(
                UUID.randomUUID(), UUID.randomUUID(), "EMAIL", 80.0, 5, 2, LocalDateTime.now());

        consumer.handleCorrectionCompleted(event);

        verify(correctionPort).execute(event);
    }

    @Test
    @DisplayName("should handle exception gracefully without propagating")
    void shouldHandleExceptionGracefully() {
        LessonCompletedEvent event = new LessonCompletedEvent(
                UUID.randomUUID(), UUID.randomUUID(), "Lesson 1", "GRAMMAR", "N5", LocalDateTime.now());

        doThrow(new RuntimeException("DB error")).when(lessonPort).execute(event);

        consumer.handleLessonCompleted(event);

        verify(lessonPort).execute(event);
    }
}
