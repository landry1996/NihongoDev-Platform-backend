package com.nihongodev.platform.application.command;

import com.nihongodev.platform.domain.model.VocabularyCategory;
import com.nihongodev.platform.domain.model.VocabularyLevel;

public record GenerateVocabularyQuizCommand(
        String quizType,
        VocabularyCategory category,
        VocabularyLevel level,
        int wordCount
) {}
