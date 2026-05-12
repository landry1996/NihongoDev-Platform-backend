package com.nihongodev.platform.application.port.out;

import com.nihongodev.platform.domain.model.CodeExerciseAttempt;

import java.util.List;
import java.util.UUID;

public interface CodeExerciseAttemptRepositoryPort {
    CodeExerciseAttempt save(CodeExerciseAttempt attempt);
    List<CodeExerciseAttempt> findByUserId(UUID userId);
    List<CodeExerciseAttempt> findByUserIdAndExerciseId(UUID userId, UUID exerciseId);
}
