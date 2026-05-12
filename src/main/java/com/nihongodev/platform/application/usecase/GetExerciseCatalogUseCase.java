package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.CodeReviewExerciseDto;
import com.nihongodev.platform.application.port.in.GetExerciseCatalogPort;
import com.nihongodev.platform.application.port.out.CodeExerciseRepositoryPort;
import com.nihongodev.platform.domain.model.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GetExerciseCatalogUseCase implements GetExerciseCatalogPort {

    private final CodeExerciseRepositoryPort exerciseRepository;

    public GetExerciseCatalogUseCase(CodeExerciseRepositoryPort exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    @Override
    public List<CodeReviewExerciseDto> getPublished(ExerciseType type, JapaneseLevel difficulty, CodeContext context) {
        return exerciseRepository.findPublished(type, difficulty, context).stream()
            .map(this::toDto)
            .toList();
    }

    @Override
    public CodeReviewExerciseDto getById(UUID exerciseId) {
        return exerciseRepository.findById(exerciseId)
            .map(this::toDto)
            .orElseThrow(() -> new IllegalArgumentException("Exercise not found: " + exerciseId));
    }

    private CodeReviewExerciseDto toDto(CodeReviewExercise e) {
        return new CodeReviewExerciseDto(
            e.getId(), e.getTitle(), e.getTitleJp(), e.getType(), e.getCodeContext(),
            e.getDifficulty(), e.getCodeSnippet(), e.getCodeLanguage(), e.getScenario(),
            e.getScenarioJp(), e.getExpectedReviewLevel(), e.getTechnicalIssues(),
            e.getModelAnswer(), e.getModelAnswerExplanation(), e.getKeyPhrases(),
            e.getAvoidPhrases(), e.getTechnicalTermsJp(), e.getPrTemplate(),
            e.getCommitRule(), e.getCulturalNote(), e.getXpReward()
        );
    }
}
