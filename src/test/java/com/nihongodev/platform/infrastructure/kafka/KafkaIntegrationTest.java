package com.nihongodev.platform.infrastructure.kafka;

import com.nihongodev.platform.domain.event.LessonCompletedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.condition.EmbeddedKafkaCondition;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@EmbeddedKafka(
        partitions = 1,
        topics = {"lesson-events", "dead-letter-events"}
)
@DisplayName("Kafka Integration")
class KafkaIntegrationTest {

    private static final EmbeddedKafkaBroker broker = EmbeddedKafkaCondition.getBroker();

    @Test
    @DisplayName("should publish event and consume it successfully")
    void shouldPublishAndConsume() throws Exception {
        String brokers = broker.getBrokersAsString();

        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        producerProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, true);

        KafkaTemplate<String, Object> template = new KafkaTemplate<>(
                new DefaultKafkaProducerFactory<>(producerProps));

        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "integration-test-group");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        DefaultKafkaConsumerFactory<String, String> consumerFactory =
                new DefaultKafkaConsumerFactory<>(consumerProps);

        ContainerProperties containerProps = new ContainerProperties("lesson-events");
        BlockingQueue<ConsumerRecord<String, String>> records = new LinkedBlockingQueue<>();
        containerProps.setMessageListener((MessageListener<String, String>) records::add);

        KafkaMessageListenerContainer<String, String> container =
                new KafkaMessageListenerContainer<>(consumerFactory, containerProps);
        container.start();
        ContainerTestUtils.waitForAssignment(container, broker.getPartitionsPerTopic());

        LessonCompletedEvent event = LessonCompletedEvent.of(
                UUID.randomUUID(), UUID.randomUUID(), "Test Lesson", "GRAMMAR", "N5");

        template.send("lesson-events", event.eventId().toString(), event).get(5, TimeUnit.SECONDS);

        ConsumerRecord<String, String> received = records.poll(10, TimeUnit.SECONDS);
        assertThat(received).isNotNull();
        assertThat(received.key()).isEqualTo(event.eventId().toString());
        assertThat(received.value()).contains("LESSON_COMPLETED");

        container.stop();
    }

    @Test
    @DisplayName("should route invalid messages to dead letter topic when consumed with error handler")
    void shouldRouteToDeadLetterTopic() throws Exception {
        String brokers = broker.getBrokersAsString();

        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        KafkaTemplate<String, Object> dltTemplate = new KafkaTemplate<>(
                new DefaultKafkaProducerFactory<>(producerProps));

        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "dlt-verify-group");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        DefaultKafkaConsumerFactory<String, String> consumerFactory =
                new DefaultKafkaConsumerFactory<>(consumerProps);

        ContainerProperties dltContainerProps = new ContainerProperties("dead-letter-events");
        BlockingQueue<ConsumerRecord<String, String>> dltRecords = new LinkedBlockingQueue<>();
        dltContainerProps.setMessageListener((MessageListener<String, String>) dltRecords::add);

        KafkaMessageListenerContainer<String, String> dltContainer =
                new KafkaMessageListenerContainer<>(consumerFactory, dltContainerProps);
        dltContainer.start();
        ContainerTestUtils.waitForAssignment(dltContainer, broker.getPartitionsPerTopic());

        dltTemplate.send("dead-letter-events", "test-key", "failed-event-payload").get(5, TimeUnit.SECONDS);

        ConsumerRecord<String, String> dltRecord = dltRecords.poll(10, TimeUnit.SECONDS);
        assertThat(dltRecord).isNotNull();
        assertThat(dltRecord.topic()).isEqualTo("dead-letter-events");

        dltContainer.stop();
    }
}
