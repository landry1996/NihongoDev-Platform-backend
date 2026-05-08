package com.nihongodev.platform;

import com.nihongodev.platform.application.port.out.EventPublisherPort;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public EventPublisherPort eventPublisherPort() {
        return (channel, event) -> {};
    }
}
