package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.domain.event.QuizCompletedEvent;

public interface UpdateProgressOnQuizCompletedPort {
    void execute(QuizCompletedEvent event);
}
