package com.nihongodev.platform.application.dto;

import java.util.List;

public record VocabularyQuizDto(
        String quizType,
        int wordCount,
        List<VocabularyQuizItemDto> items
) {}
