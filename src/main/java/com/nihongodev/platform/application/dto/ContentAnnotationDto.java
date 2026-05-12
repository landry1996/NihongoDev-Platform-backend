package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.AnnotationType;
import com.nihongodev.platform.domain.model.JapaneseLevel;

import java.util.UUID;

public record ContentAnnotationDto(
    UUID id,
    int startOffset,
    int endOffset,
    String surfaceForm,
    String reading,
    String meaning,
    AnnotationType annotationType,
    JapaneseLevel requiredLevel,
    String grammarNote,
    String culturalNote
) {}
