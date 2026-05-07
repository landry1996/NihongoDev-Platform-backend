package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.domain.event.LessonCompletedEvent;

public interface UpdateProgressOnLessonCompletedPort {
    void execute(LessonCompletedEvent event);
}
