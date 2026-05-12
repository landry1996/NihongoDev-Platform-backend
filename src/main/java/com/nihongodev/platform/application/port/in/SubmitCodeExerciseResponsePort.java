package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.command.SubmitCodeExerciseResponseCommand;
import com.nihongodev.platform.application.dto.CodeExerciseAttemptDto;

public interface SubmitCodeExerciseResponsePort {
    CodeExerciseAttemptDto execute(SubmitCodeExerciseResponseCommand command);
}
