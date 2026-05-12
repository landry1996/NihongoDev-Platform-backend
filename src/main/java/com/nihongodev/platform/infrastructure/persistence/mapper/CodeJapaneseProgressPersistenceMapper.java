package com.nihongodev.platform.infrastructure.persistence.mapper;

import com.nihongodev.platform.domain.model.*;
import com.nihongodev.platform.infrastructure.persistence.entity.CodeJapaneseProgressEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CodeJapaneseProgressPersistenceMapper {

    public CodeJapaneseProgress toDomain(CodeJapaneseProgressEntity entity) {
        CodeJapaneseProgress progress = new CodeJapaneseProgress();
        progress.setId(entity.getId());
        progress.setUserId(entity.getUserId());
        progress.setExerciseType(ExerciseType.valueOf(entity.getExerciseType()));
        progress.setExercisesCompleted(entity.getExercisesCompleted());
        progress.setTotalScore(entity.getTotalScore());
        progress.setAverageScore(entity.getAverageScore());
        progress.setBestScore(entity.getBestScore());
        progress.setCurrentStreak(entity.getCurrentStreak());
        if (entity.getRecurringViolations() != null) {
            Map<ViolationType, Integer> violations = new HashMap<>();
            entity.getRecurringViolations().forEach((key, value) -> {
                try {
                    violations.put(ViolationType.valueOf(key), value);
                } catch (IllegalArgumentException ignored) {}
            });
            progress.setRecurringViolations(violations);
        }
        progress.setLastActivityAt(entity.getLastActivityAt());
        return progress;
    }

    public CodeJapaneseProgressEntity toEntity(CodeJapaneseProgress domain) {
        CodeJapaneseProgressEntity entity = new CodeJapaneseProgressEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setExerciseType(domain.getExerciseType().name());
        entity.setExercisesCompleted(domain.getExercisesCompleted());
        entity.setTotalScore(domain.getTotalScore());
        entity.setAverageScore(domain.getAverageScore());
        entity.setBestScore(domain.getBestScore());
        entity.setCurrentStreak(domain.getCurrentStreak());
        if (domain.getRecurringViolations() != null) {
            Map<String, Integer> violations = new HashMap<>();
            domain.getRecurringViolations().forEach((key, value) -> violations.put(key.name(), value));
            entity.setRecurringViolations(violations);
        }
        entity.setLastActivityAt(domain.getLastActivityAt());
        return entity;
    }
}
