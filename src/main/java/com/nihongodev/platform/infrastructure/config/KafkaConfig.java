package com.nihongodev.platform.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    private final KafkaTopicsProperties topicsProperties;

    public KafkaConfig(KafkaTopicsProperties topicsProperties) {
        this.topicsProperties = topicsProperties;
    }

    @Bean
    public NewTopic userEventsTopic() {
        return buildTopic(topicsProperties.getUserEvents());
    }

    @Bean
    public NewTopic lessonEventsTopic() {
        return buildTopic(topicsProperties.getLessonEvents());
    }

    @Bean
    public NewTopic quizEventsTopic() {
        return buildTopic(topicsProperties.getQuizEvents());
    }

    @Bean
    public NewTopic interviewEventsTopic() {
        return buildTopic(topicsProperties.getInterviewEvents());
    }

    @Bean
    public NewTopic progressEventsTopic() {
        return buildTopic(topicsProperties.getProgressEvents());
    }

    @Bean
    public NewTopic vocabularyEventsTopic() {
        return buildTopic(topicsProperties.getVocabularyEvents());
    }

    @Bean
    public NewTopic correctionEventsTopic() {
        return buildTopic(topicsProperties.getCorrectionEvents());
    }

    @Bean
    public NewTopic notificationEventsTopic() {
        return buildTopic(topicsProperties.getNotificationEvents());
    }

    @Bean
    public NewTopic cvGeneratorEventsTopic() {
        return buildTopic(topicsProperties.getCvGeneratorEvents());
    }

    @Bean
    public NewTopic deadLetterEventsTopic() {
        return buildTopic(topicsProperties.getDeadLetterEvents());
    }

    private NewTopic buildTopic(KafkaTopicsProperties.TopicDef def) {
        return TopicBuilder.name(def.getName())
                .partitions(def.getPartitions())
                .replicas(def.getReplicas())
                .build();
    }
}
