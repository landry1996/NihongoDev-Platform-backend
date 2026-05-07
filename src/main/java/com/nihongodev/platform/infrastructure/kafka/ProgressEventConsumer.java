package com.nihongodev.platform.infrastructure.kafka;

import com.nihongodev.platform.application.port.in.*;
import com.nihongodev.platform.domain.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
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
        log.info("Received LessonCompletedEvent for user: {}", event.userId());
        try {
            lessonCompletedPort.execute(event);
        } catch (Exception e) {
            log.error("Error processing LessonCompletedEvent for user {}: {}", event.userId(), e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "quiz-events", groupId = "progress-consumer-group")
    public void handleQuizCompleted(QuizCompletedEvent event) {
        log.info("Received QuizCompletedEvent for user: {}", event.userId());
        try {
            quizCompletedPort.execute(event);
        } catch (Exception e) {
            log.error("Error processing QuizCompletedEvent for user {}: {}", event.userId(), e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "interview-events", groupId = "progress-consumer-group")
    public void handleInterviewCompleted(InterviewCompletedEvent event) {
        log.info("Received InterviewCompletedEvent for user: {}", event.userId());
        try {
            interviewCompletedPort.execute(event);
        } catch (Exception e) {
            log.error("Error processing InterviewCompletedEvent for user {}: {}", event.userId(), e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "correction-events", groupId = "progress-consumer-group")
    public void handleCorrectionCompleted(TextCorrectedEvent event) {
        log.info("Received TextCorrectedEvent for user: {}", event.userId());
        try {
            correctionCompletedPort.execute(event);
        } catch (Exception e) {
            log.error("Error processing TextCorrectedEvent for user {}: {}", event.userId(), e.getMessage(), e);
        }
    }
}
