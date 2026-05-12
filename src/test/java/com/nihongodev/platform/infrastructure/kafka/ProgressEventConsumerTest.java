package com.nihongodev.platform.infrastructure.kafka;

import com.nihongodev.platform.application.port.in.UpdateProgressOnCodeExerciseCompletedPort;
import com.nihongodev.platform.application.port.in.UpdateProgressOnContentReadPort;
import com.nihongodev.platform.application.port.in.UpdateProgressOnCorrectionCompletedPort;
import com.nihongodev.platform.application.port.in.UpdateProgressOnInterviewCompletedPort;
import com.nihongodev.platform.application.port.in.UpdateProgressOnLessonCompletedPort;
import com.nihongodev.platform.application.port.in.UpdateProgressOnQuizCompletedPort;
import com.nihongodev.platform.application.port.in.UpdateProgressOnScenarioCompletedPort;
import com.nihongodev.platform.domain.event.InterviewCompletedEvent;
import com.nihongodev.platform.domain.event.LessonCompletedEvent;
import com.nihongodev.platform.domain.event.QuizCompletedEvent;
import com.nihongodev.platform.domain.event.ScenarioCompletedEvent;
import com.nihongodev.platform.domain.event.TextCorrectedEvent;
import com.nihongodev.platform.domain.model.ScenarioCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProgressEventConsumer")
class ProgressEventConsumerTest {

    @Mock private UpdateProgressOnLessonCompletedPort lessonPort;
    @Mock private UpdateProgressOnQuizCompletedPort quizPort;
    @Mock private UpdateProgressOnInterviewCompletedPort interviewPort;
    @Mock private UpdateProgressOnCorrectionCompletedPort correctionPort;
    @Mock private UpdateProgressOnScenarioCompletedPort scenarioPort;
    @Mock private UpdateProgressOnCodeExerciseCompletedPort codeExercisePort;
    @Mock private UpdateProgressOnContentReadPort contentReadPort;

    private ProgressEventConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new ProgressEventConsumer(lessonPort, quizPort, interviewPort, correctionPort, scenarioPort, codeExercisePort, contentReadPort);
    }

    @Test
    @DisplayName("should delegate LessonCompletedEvent to port")
    void shouldDelegateLessonEvent() {
        LessonCompletedEvent event = LessonCompletedEvent.of(
                UUID.randomUUID(), UUID.randomUUID(), "Lesson 1", "GRAMMAR", "N5");

        consumer.handleLessonCompleted(event);

        verify(lessonPort).execute(event);
    }

    @Test
    @DisplayName("should delegate QuizCompletedEvent to port")
    void shouldDelegateQuizEvent() {
        QuizCompletedEvent event = QuizCompletedEvent.of(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "Quiz 1",
                85.0, true, 5, "CLASSIC");

        consumer.handleQuizCompleted(event);

        verify(quizPort).execute(event);
    }

    @Test
    @DisplayName("should delegate InterviewCompletedEvent to port")
    void shouldDelegateInterviewEvent() {
        InterviewCompletedEvent event = InterviewCompletedEvent.of(
                UUID.randomUUID(), UUID.randomUUID(), "TECHNICAL", 75.0, 600, true);

        consumer.handleInterviewCompleted(event);

        verify(interviewPort).execute(event);
    }

    @Test
    @DisplayName("should delegate TextCorrectedEvent to port")
    void shouldDelegateCorrectionEvent() {
        TextCorrectedEvent event = TextCorrectedEvent.of(
                UUID.randomUUID(), UUID.randomUUID(), "EMAIL", 80.0, 5, 2);

        consumer.handleCorrectionCompleted(event);

        verify(correctionPort).execute(event);
    }

    @Test
    @DisplayName("should delegate ScenarioCompletedEvent to port")
    void shouldDelegateScenarioEvent() {
        ScenarioCompletedEvent event = ScenarioCompletedEvent.create(
                UUID.randomUUID(), UUID.randomUUID(), 85, ScenarioCategory.COMMUNICATION);

        consumer.handleScenarioCompleted(event);

        verify(scenarioPort).execute(event);
    }

    @Test
    @DisplayName("should throw IllegalArgumentException when eventId is null")
    void shouldThrowOnNullEventId() {
        LessonCompletedEvent event = new LessonCompletedEvent(
                null, "LESSON_COMPLETED", UUID.randomUUID(), LocalDateTime.now(),
                UUID.randomUUID(), "Test", "GRAMMAR", "N5");

        assertThatThrownBy(() -> consumer.handleLessonCompleted(event))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("eventId must not be null");
    }

    @Test
    @DisplayName("should throw IllegalArgumentException when userId is null")
    void shouldThrowOnNullUserId() {
        LessonCompletedEvent event = new LessonCompletedEvent(
                UUID.randomUUID(), "LESSON_COMPLETED", null, LocalDateTime.now(),
                UUID.randomUUID(), "Test", "GRAMMAR", "N5");

        assertThatThrownBy(() -> consumer.handleLessonCompleted(event))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("userId must not be null");
    }
}
