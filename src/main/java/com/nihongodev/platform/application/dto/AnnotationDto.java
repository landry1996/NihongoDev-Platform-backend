package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.AnnotationCategory;
import com.nihongodev.platform.domain.model.Severity;

public record AnnotationDto(
        int startOffset,
        int endOffset,
        Severity severity,
        AnnotationCategory category,
        String original,
        String suggestion,
        String explanation
) {}
