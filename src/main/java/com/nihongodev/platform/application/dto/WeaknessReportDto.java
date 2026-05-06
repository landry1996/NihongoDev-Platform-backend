package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.AnnotationCategory;

import java.util.List;

public record WeaknessReportDto(
        List<WeaknessItemDto> weaknesses,
        int totalCorrections,
        double averageScore
) {
    public record WeaknessItemDto(
            AnnotationCategory category,
            String description,
            int occurrenceCount,
            String lastExample,
            boolean recurring
    ) {}
}
