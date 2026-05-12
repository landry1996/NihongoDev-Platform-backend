package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record RealContentDto(
    UUID id,
    String title,
    String titleReading,
    String body,
    String summary,
    String sourceUrl,
    ContentSource source,
    ContentDomain domain,
    JapaneseLevel difficulty,
    ReadingDifficulty readingDifficulty,
    List<ContentAnnotationDto> annotations,
    List<String> tags,
    List<String> keyVocabulary,
    int wordCount,
    int kanjiCount,
    int estimatedReadingMinutes,
    ContentStatus status,
    String authorName,
    LocalDateTime publishedAt
) {}
