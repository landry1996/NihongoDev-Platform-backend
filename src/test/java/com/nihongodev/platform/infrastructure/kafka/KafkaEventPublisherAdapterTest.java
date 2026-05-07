package com.nihongodev.platform.infrastructure.kafka;

import com.nihongodev.platform.domain.event.LessonCompletedEvent;
import com.nihongodev.platform.infrastructure.config.KafkaTopicsProperties;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.support.SendResult;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("KafkaEventPublisherAdapter")
class KafkaEventPublisherAdapterTest {

    @Mock
    private KafkaOperations<String, Object> kafkaOperations;

    private KafkaEventPublisherAdapter adapter;

    @BeforeEach
    void setUp() {
        KafkaTopicsProperties props = buildTopicsProperties();
        adapter = new KafkaEventPublisherAdapter(kafkaOperations, props);
    }

    @Test
    @DisplayName("should send event to resolved topic with eventId as key")
    void shouldSendToResolvedTopicWithEventIdKey() {
        LessonCompletedEvent event = LessonCompletedEvent.of(
                UUID.randomUUID(), UUID.randomUUID(), "Lesson 1", "GRAMMAR", "N5");

        when(kafkaOperations.send(anyString(), anyString(), any()))
                .thenReturn(CompletableFuture.completedFuture(buildSendResult()));

        adapter.publish("lesson-events", event);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaOperations).send(topicCaptor.capture(), keyCaptor.capture(), eq(event));

        assertThat(topicCaptor.getValue()).isEqualTo("lesson-events");
        assertThat(keyCaptor.getValue()).isEqualTo(event.eventId().toString());
    }

    @Test
    @DisplayName("should extract eventId from DomainEvent")
    void shouldExtractEventIdFromDomainEvent() {
        LessonCompletedEvent event = LessonCompletedEvent.of(
                UUID.randomUUID(), UUID.randomUUID(), "Test", "VOCAB", "N4");

        when(kafkaOperations.send(anyString(), anyString(), any()))
                .thenReturn(CompletableFuture.completedFuture(buildSendResult()));

        adapter.publish("lesson-events", event);

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaOperations).send(anyString(), keyCaptor.capture(), any());
        assertThat(keyCaptor.getValue()).isEqualTo(event.eventId().toString());
    }

    @Test
    @DisplayName("should generate random key for non-DomainEvent objects")
    void shouldGenerateRandomKeyForNonDomainEvent() {
        Object plainEvent = new Object();

        when(kafkaOperations.send(anyString(), anyString(), any()))
                .thenReturn(CompletableFuture.completedFuture(buildSendResult()));

        adapter.publish("some-topic", plainEvent);

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaOperations).send(eq("some-topic"), keyCaptor.capture(), eq(plainEvent));
        assertThat(UUID.fromString(keyCaptor.getValue())).isNotNull();
    }

    @Test
    @DisplayName("should use channel as topic when not in registry")
    void shouldFallbackToChannelAsTopic() {
        Object event = new Object();

        when(kafkaOperations.send(anyString(), anyString(), any()))
                .thenReturn(CompletableFuture.completedFuture(buildSendResult()));

        adapter.publish("unknown-topic", event);

        verify(kafkaOperations).send(eq("unknown-topic"), anyString(), eq(event));
    }

    private SendResult<String, Object> buildSendResult() {
        RecordMetadata metadata = new RecordMetadata(
                new TopicPartition("test-topic", 0), 0L, 0, 0L, 0, 0);
        return new SendResult<>(null, metadata);
    }

    private KafkaTopicsProperties buildTopicsProperties() {
        KafkaTopicsProperties props = new KafkaTopicsProperties();
        props.setUserEvents(topicDef("user-events"));
        props.setLessonEvents(topicDef("lesson-events"));
        props.setQuizEvents(topicDef("quiz-events"));
        props.setInterviewEvents(topicDef("interview-events"));
        props.setProgressEvents(topicDef("progress-events"));
        props.setVocabularyEvents(topicDef("vocabulary-events"));
        props.setCorrectionEvents(topicDef("correction-events"));
        props.setNotificationEvents(topicDef("notification-events"));
        props.setCvGeneratorEvents(topicDef("cv-generator-events"));
        props.setDeadLetterEvents(topicDef("dead-letter-events"));
        return props;
    }

    private KafkaTopicsProperties.TopicDef topicDef(String name) {
        KafkaTopicsProperties.TopicDef def = new KafkaTopicsProperties.TopicDef();
        def.setName(name);
        def.setPartitions(3);
        def.setReplicas(1);
        return def;
    }
}
