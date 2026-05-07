package com.nihongodev.platform.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public interface DomainEvent {
    UUID eventId();
    String eventType();
    UUID userId();
    LocalDateTime occurredAt();
}
