package com.nihongodev.platform.application.port.out;

import com.nihongodev.platform.domain.model.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CodeExerciseRepositoryPort {
    CodeReviewExercise save(CodeReviewExercise exercise);
    Optional<CodeReviewExercise> findById(UUID id);
    List<CodeReviewExercise> findPublished(ExerciseType type, JapaneseLevel difficulty, CodeContext context);
}
