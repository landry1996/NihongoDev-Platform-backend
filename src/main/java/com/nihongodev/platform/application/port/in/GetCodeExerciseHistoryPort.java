package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.CodeExerciseAttemptDto;

import java.util.List;
import java.util.UUID;

public interface GetCodeExerciseHistoryPort {
    List<CodeExerciseAttemptDto> getByUserId(UUID userId);
}
