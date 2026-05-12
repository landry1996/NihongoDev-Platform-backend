package com.nihongodev.platform.infrastructure.web.request;

import com.nihongodev.platform.domain.model.*;
import jakarta.validation.constraints.*;

import java.util.List;

public record CreateCodeExerciseRequest(
    @NotBlank @Size(max = 200) String title,
    @Size(max = 200) String titleJp,
    @NotNull ExerciseType type,
    @NotNull CodeContext codeContext,
    @NotNull JapaneseLevel difficulty,
    @Size(max = 5000) String codeSnippet,
    @Size(max = 30) String codeLanguage,
    @NotBlank @Size(max = 2000) String scenario,
    @Size(max = 2000) String scenarioJp,
    ReviewLevel expectedReviewLevel,
    List<String> technicalIssues,
    @Size(max = 2000) String modelAnswer,
    @Size(max = 2000) String modelAnswerExplanation,
    List<String> keyPhrases,
    List<String> avoidPhrases,
    List<String> technicalTermsJp,
    PRTemplate prTemplate,
    CommitMessageRule commitRule,
    @Size(max = 1000) String culturalNote,
    @Min(10) @Max(500) int xpReward
) {}
