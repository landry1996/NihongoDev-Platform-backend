package com.nihongodev.platform.application.dto;

import java.util.List;
import java.util.UUID;

public record VocabularyQuizItemDto(
        UUID vocabularyId,
        String question,
        String correctAnswer,
        List<String> options
) {}
