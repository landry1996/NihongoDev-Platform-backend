package com.nihongodev.platform.infrastructure.web.request;

import java.util.List;
import java.util.UUID;

public record UpdateVocabularyRequest(
        String french,
        String english,
        String japanese,
        String romaji,
        String example,
        String codeExample,
        String category,
        String level,
        String domain,
        List<String> tags,
        UUID lessonId
) {}
