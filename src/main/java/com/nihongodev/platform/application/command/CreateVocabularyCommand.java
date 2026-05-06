package com.nihongodev.platform.application.command;

import com.nihongodev.platform.domain.model.VocabularyCategory;
import com.nihongodev.platform.domain.model.VocabularyLevel;

import java.util.List;
import java.util.UUID;

public record CreateVocabularyCommand(
        String french,
        String english,
        String japanese,
        String romaji,
        String example,
        String codeExample,
        VocabularyCategory category,
        VocabularyLevel level,
        String domain,
        List<String> tags,
        UUID lessonId
) {}
