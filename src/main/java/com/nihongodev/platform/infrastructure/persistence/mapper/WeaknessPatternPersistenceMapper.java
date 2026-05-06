package com.nihongodev.platform.infrastructure.persistence.mapper;

import com.nihongodev.platform.domain.model.AnnotationCategory;
import com.nihongodev.platform.domain.model.WeaknessPattern;
import com.nihongodev.platform.infrastructure.persistence.entity.WeaknessPatternEntity;
import org.springframework.stereotype.Component;

@Component
public class WeaknessPatternPersistenceMapper {

    public WeaknessPatternEntity toEntity(WeaknessPattern domain) {
        WeaknessPatternEntity entity = new WeaknessPatternEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setCategory(domain.getCategory().name());
        entity.setPatternDescription(domain.getPatternDescription());
        entity.setOccurrenceCount(domain.getOccurrenceCount());
        entity.setLastExample(domain.getLastExample());
        return entity;
    }

    public WeaknessPattern toDomain(WeaknessPatternEntity entity) {
        WeaknessPattern pattern = new WeaknessPattern();
        pattern.setId(entity.getId());
        pattern.setUserId(entity.getUserId());
        pattern.setCategory(AnnotationCategory.valueOf(entity.getCategory()));
        pattern.setPatternDescription(entity.getPatternDescription());
        pattern.setOccurrenceCount(entity.getOccurrenceCount());
        pattern.setLastExample(entity.getLastExample());
        return pattern;
    }
}
