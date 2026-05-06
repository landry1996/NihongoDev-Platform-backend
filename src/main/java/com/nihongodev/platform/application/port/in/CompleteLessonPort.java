package com.nihongodev.platform.application.port.in;

import java.util.UUID;

public interface CompleteLessonPort {
    void complete(UUID userId, UUID lessonId);
}
