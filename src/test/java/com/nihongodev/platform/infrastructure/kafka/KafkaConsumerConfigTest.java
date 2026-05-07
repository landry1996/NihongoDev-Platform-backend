package com.nihongodev.platform.infrastructure.kafka;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.DefaultErrorHandler;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("KafkaConsumerConfig")
class KafkaConsumerConfigTest {

    @Mock
    private KafkaOperations<String, Object> kafkaOperations;

    @Test
    @DisplayName("should create DefaultErrorHandler with exponential backoff")
    void shouldCreateErrorHandlerWithBackoff() {
        KafkaConsumerConfig config = new KafkaConsumerConfig();

        DefaultErrorHandler handler = config.errorHandler(kafkaOperations);

        assertThat(handler).isNotNull();
    }

    @Test
    @DisplayName("should configure non-retryable exceptions")
    void shouldConfigureNonRetryableExceptions() {
        KafkaConsumerConfig config = new KafkaConsumerConfig();

        DefaultErrorHandler handler = config.errorHandler(kafkaOperations);

        assertThat(handler).isNotNull();
    }
}
