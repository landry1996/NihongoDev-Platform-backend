package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.domain.event.CodeExerciseCompletedEvent;

public interface UpdateProgressOnCodeExerciseCompletedPort {
    void execute(CodeExerciseCompletedEvent event);
}
