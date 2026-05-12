package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.command.CreateCodeExerciseCommand;
import com.nihongodev.platform.application.dto.CodeReviewExerciseDto;

public interface CreateCodeExercisePort {
    CodeReviewExerciseDto execute(CreateCodeExerciseCommand command);
}
