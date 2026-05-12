package com.nihongodev.platform.application.port.out;

import com.nihongodev.platform.domain.model.CodeJapaneseProgress;
import com.nihongodev.platform.domain.model.ExerciseType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CodeJapaneseProgressRepositoryPort {
    CodeJapaneseProgress save(CodeJapaneseProgress progress);
    Optional<CodeJapaneseProgress> findByUserIdAndExerciseType(UUID userId, ExerciseType exerciseType);
    List<CodeJapaneseProgress> findByUserId(UUID userId);
}
