package com.nihongodev.platform.application.port.out;

public interface EventPublisherPort {
    void publish(String topic, Object event);
}
