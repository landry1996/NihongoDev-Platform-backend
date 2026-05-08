package com.nihongodev.platform.infrastructure.web.request;

import jakarta.validation.constraints.*;

import java.util.List;

public record CreateScenarioRequest(
        @NotBlank @Size(max = 200) String title,
        @Size(max = 200) String titleJp,
        @NotBlank @Size(max = 2000) String situation,
        @Size(max = 2000) String situationJp,
        @NotBlank String context,
        @NotBlank String relationship,
        @NotBlank String mode,
        @NotBlank String category,
        @NotBlank String expectedKeigoLevel,
        @NotBlank String difficulty,
        List<ChoiceData> choices,
        @Size(max = 2000) String modelAnswer,
        @Size(max = 2000) String modelAnswerExplanation,
        List<String> keyPhrases,
        List<String> avoidPhrases,
        @Size(max = 1000) String culturalNote,
        @Min(10) @Max(500) int xpReward
) {
    public record ChoiceData(
            String text,
            String textJp,
            boolean isOptimal,
            int culturalScore,
            String feedbackIfChosen
    ) {}
}
