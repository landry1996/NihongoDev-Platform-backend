package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.*;

import java.util.List;
import java.util.UUID;

public record CodeReviewExerciseDto(
    UUID id,
    String title,
    String titleJp,
    ExerciseType type,
    CodeContext codeContext,
    JapaneseLevel difficulty,
    String codeSnippet,
    String codeLanguage,
    String scenario,
    String scenarioJp,
    ReviewLevel expectedReviewLevel,
    List<String> technicalIssues,
    String modelAnswer,
    String modelAnswerExplanation,
    List<String> keyPhrases,
    List<String> avoidPhrases,
    List<String> technicalTermsJp,
    PRTemplate prTemplate,
    CommitMessageRule commitRule,
    String culturalNote,
    int xpReward
) {}
