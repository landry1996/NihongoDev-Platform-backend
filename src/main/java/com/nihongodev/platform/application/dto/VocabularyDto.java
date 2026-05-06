package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.VocabularyCategory;
import com.nihongodev.platform.domain.model.VocabularyLevel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record VocabularyDto(
        UUID id,
        UUID lessonId,
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
        double difficultyScore,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
