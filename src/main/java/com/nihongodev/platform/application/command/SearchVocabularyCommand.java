package com.nihongodev.platform.application.command;

import com.nihongodev.platform.domain.model.VocabularyCategory;
import com.nihongodev.platform.domain.model.VocabularyLevel;

import java.util.UUID;

public record SearchVocabularyCommand(
        String query,
        VocabularyCategory category,
        VocabularyLevel level,
        UUID lessonId,
        String tag
) {}
