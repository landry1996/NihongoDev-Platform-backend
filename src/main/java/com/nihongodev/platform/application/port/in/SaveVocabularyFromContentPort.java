package com.nihongodev.platform.application.port.in;

import java.util.UUID;

public interface SaveVocabularyFromContentPort {
    void execute(UUID userId, UUID sessionId, UUID annotationId);
}
