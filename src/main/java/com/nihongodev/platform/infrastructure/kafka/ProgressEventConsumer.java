package com.nihongodev.platform.infrastructure.kafka;

import com.nihongodev.platform.application.port.in.UpdateProgressOnCorrectionCompletedPort;
import com.nihongodev.platform.application.port.in.UpdateProgressOnInterviewCompletedPort;
import com.nihongodev.platform.application.port.in.UpdateProgressOnLessonCompletedPort;
import com.nihongodev.platform.application.port.in.UpdateProgressOnQuizCompletedPort;
import com.nihongodev.platform.domain.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(KafkaTemplate.class)
public class ProgressEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ProgressEventConsumer.class);

    private final UpdateProgressOnLessonCompletedPort lessonCompletedPort;
    private final UpdateProgressOnQuizCompletedPort quizCompletedPort;
    private final UpdateProgressOnInterviewCompletedPort interviewCompletedPort;
    private final UpdateProgressOnCorrectionCompletedPort correctionCompletedPort;

    public ProgressEventConsumer(UpdateProgressOnLessonCompletedPort lessonCompletedPort,
                                 UpdateProgressOnQuizCompletedPort quizCompletedPort,
                                 UpdateProgressOnInterviewCompletedPort interviewCompletedPort,
                                 UpdateProgressOnCorrectionCompletedPort correctionCompletedPort) {
        this.lessonCompletedPort = lessonCompletedPort;
        this.quizCompletedPort = quizCompletedPort;
        this.interviewCompletedPort = interviewCompletedPort;
        this.correctionCompletedPort = correctionCompletedPort;
    }

    @KafkaListener(topics = "lesson-events", groupId = "progress-consumer-group")
    public void handleLessonCompleted(LessonCompletedEvent event) {
        validateEvent(event);
        log.info("Processing event [eventId={}, type=LESSON_COMPLETED, userId={}]", event.eventId(), event.userId());
        lessonCompletedPort.execute(event);
    }

    @KafkaListener(topics = "quiz-events", groupId = "progress-consumer-group")
    public void handleQuizCompleted(QuizCompletedEvent event) {
        validateEvent(event);
        log.info("Processing event [eventId={}, type=QUIZ_COMPLETED, userId={}]", event.eventId(), event.userId());
        quizCompletedPort.execute(event);
    }

    @KafkaListener(topics = "interview-events", groupId = "progress-consumer-group")
    public void handleInterviewCompleted(InterviewCompletedEvent event) {
        validateEvent(event);
        log.info("Processing event [eventId={}, type=INTERVIEW_COMPLETED, userId={}]", event.eventId(), event.userId());
        interviewCompletedPort.execute(event);
    }

    @KafkaListener(topics = "correction-events", groupId = "progress-consumer-group")
    public void handleCorrectionCompleted(TextCorrectedEvent event) {
        validateEvent(event);
        log.info("Processing event [eventId={}, type=TEXT_CORRECTED, userId={}]", event.eventId(), event.userId());
        correctionCompletedPort.execute(event);
    }

    private void validateEvent(DomainEvent event) {
        if (event.eventId() == null) {
            throw new IllegalArgumentException("eventId must not be null");
        }
        if (event.userId() == null) {
            throw new IllegalArgumentException("userId must not be null");
        }
    }
}
