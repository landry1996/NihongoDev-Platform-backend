package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.JapaneseLevel;
import com.nihongodev.platform.domain.model.TextType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CorrectionSessionDto(
        UUID id,
        TextType textType,
        JapaneseLevel targetLevel,
        CorrectionScoreDto score,
        List<AnnotationDto> annotations,
        int totalAnnotations,
        int errorCount,
        int warningCount,
        int infoCount,
        LocalDateTime createdAt
) {}
