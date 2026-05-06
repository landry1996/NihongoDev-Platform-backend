package com.nihongodev.platform.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    public static final String USER_EVENTS_TOPIC = "user-events";
    public static final String LESSON_EVENTS_TOPIC = "lesson-events";
    public static final String QUIZ_EVENTS_TOPIC = "quiz-events";
    public static final String INTERVIEW_EVENTS_TOPIC = "interview-events";
    public static final String PROGRESS_EVENTS_TOPIC = "progress-events";
    public static final String VOCABULARY_EVENTS_TOPIC = "vocabulary-events";
    public static final String NOTIFICATION_EVENTS_TOPIC = "notification-events";

    @Bean
    public NewTopic userEventsTopic() {
        return TopicBuilder.name(USER_EVENTS_TOPIC).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic lessonEventsTopic() {
        return TopicBuilder.name(LESSON_EVENTS_TOPIC).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic quizEventsTopic() {
        return TopicBuilder.name(QUIZ_EVENTS_TOPIC).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic interviewEventsTopic() {
        return TopicBuilder.name(INTERVIEW_EVENTS_TOPIC).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic progressEventsTopic() {
        return TopicBuilder.name(PROGRESS_EVENTS_TOPIC).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic vocabularyEventsTopic() {
        return TopicBuilder.name(VOCABULARY_EVENTS_TOPIC).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic notificationEventsTopic() {
        return TopicBuilder.name(NOTIFICATION_EVENTS_TOPIC).partitions(3).replicas(1).build();
    }
}
