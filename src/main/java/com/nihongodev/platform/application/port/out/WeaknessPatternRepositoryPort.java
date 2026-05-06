package com.nihongodev.platform.application.port.out;

import com.nihongodev.platform.domain.model.AnnotationCategory;
import com.nihongodev.platform.domain.model.WeaknessPattern;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WeaknessPatternRepositoryPort {
    WeaknessPattern save(WeaknessPattern pattern);
    List<WeaknessPattern> findByUserId(UUID userId);
    Optional<WeaknessPattern> findByUserIdAndCategoryAndDescription(UUID userId,
                                                                     AnnotationCategory category,
                                                                     String description);
}
