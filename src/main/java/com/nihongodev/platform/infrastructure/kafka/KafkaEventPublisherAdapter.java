package com.nihongodev.platform.infrastructure.kafka;

import com.nihongodev.platform.application.port.out.EventPublisherPort;
import com.nihongodev.platform.infrastructure.config.KafkaTopicsProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class KafkaEventPublisherAdapter implements EventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(KafkaEventPublisherAdapter.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Map<String, String> topicRegistry;

    public KafkaEventPublisherAdapter(KafkaTemplate<String, Object> kafkaTemplate,
                                      KafkaTopicsProperties topicsProperties) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicRegistry = buildTopicRegistry(topicsProperties);
    }

    @Override
    public void publish(String channel, Object event) {
        String topic = topicRegistry.getOrDefault(channel, channel);
        log.info("Publishing event to topic [{}]: {}", topic, event.getClass().getSimpleName());
        kafkaTemplate.send(topic, event);
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
        return registry;
    }
}
