package com.nihongodev.platform.infrastructure.kafka;

import com.nihongodev.platform.application.port.out.EventPublisherPort;
import com.nihongodev.platform.domain.event.DomainEvent;
import com.nihongodev.platform.infrastructure.config.KafkaTopicsProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@ConditionalOnBean(KafkaTemplate.class)
public class KafkaEventPublisherAdapter implements EventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(KafkaEventPublisherAdapter.class);
    private final KafkaOperations<String, Object> kafkaOperations;
    private final Map<String, String> topicRegistry;

    public KafkaEventPublisherAdapter(KafkaTemplate<String, Object> kafkaTemplate,
                                      KafkaTopicsProperties topicsProperties) {
        this(((KafkaOperations<String, Object>) kafkaTemplate), topicsProperties);
    }

    KafkaEventPublisherAdapter(KafkaOperations<String, Object> kafkaOperations,
                               KafkaTopicsProperties topicsProperties) {
        this.kafkaOperations = kafkaOperations;
        this.topicRegistry = buildTopicRegistry(topicsProperties);
    }

    @Override
    public void publish(String channel, Object event) {
        String resolvedTopic = topicRegistry.getOrDefault(channel, channel);
        String key = extractEventId(event);

        kafkaOperations.send(resolvedTopic, key, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish event [topic={}, eventId={}]: {}",
                                resolvedTopic, key, ex.getMessage());
                    } else {
                        log.info("Event published [topic={}, eventId={}, offset={}]",
                                resolvedTopic, key, result.getRecordMetadata().offset());
                    }
                });
    }

    private String extractEventId(Object event) {
        if (event instanceof DomainEvent domainEvent) {
            return domainEvent.eventId().toString();
        }
        return UUID.randomUUID().toString();
    }

    private Map<String, String> buildTopicRegistry(KafkaTopicsProperties props) {
        Map<String, String> registry = new HashMap<>();
        registry.put("user-events", props.getUserEvents().getName());
        registry.put("lesson-events", props.getLessonEvents().getName());
        registry.put("quiz-events", props.getQuizEvents().getName());
        registry.put("interview-events", props.getInterviewEvents().getName());
        registry.put("progress-events", props.getProgressEvents().getName());
        registry.put("vocabulary-events", props.getVocabularyEvents().getName());
        registry.put("correction-events", props.getCorrectionEvents().getName());
        registry.put("notification-events", props.getNotificationEvents().getName());
        registry.put("cv-generator-events", props.getCvGeneratorEvents().getName());
        registry.put("dead-letter-events", props.getDeadLetterEvents().getName());
        return registry;
    }
}
