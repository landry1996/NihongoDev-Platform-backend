package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.CodeReviewExerciseDto;
import com.nihongodev.platform.domain.model.*;

import java.util.List;
import java.util.UUID;

public interface GetExerciseCatalogPort {
    List<CodeReviewExerciseDto> getPublished(ExerciseType type, JapaneseLevel difficulty, CodeContext context);
    CodeReviewExerciseDto getById(UUID exerciseId);
}
