package com.nihongodev.platform.application.command;

import com.nihongodev.platform.domain.model.*;

import java.util.List;

public record CreateCodeExerciseCommand(
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
