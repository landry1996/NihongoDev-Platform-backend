package com.nihongodev.platform.infrastructure.persistence.mapper;

import com.nihongodev.platform.domain.model.GeneratedPitch;
import com.nihongodev.platform.domain.model.PitchType;
import com.nihongodev.platform.infrastructure.persistence.entity.GeneratedPitchEntity;
import org.springframework.stereotype.Component;

@Component
public class GeneratedPitchPersistenceMapper {

    public GeneratedPitch toDomain(GeneratedPitchEntity entity) {
        if (entity == null) return null;
        GeneratedPitch p = new GeneratedPitch();
        p.setId(entity.getId());
        p.setUserId(entity.getUserId());
        p.setPitchType(PitchType.valueOf(entity.getPitchType()));
        p.setContent(entity.getContent());
        p.setProfileSnapshotId(entity.getProfileSnapshotId());
        p.setGeneratedAt(entity.getGeneratedAt());
        return p;
    }

    public GeneratedPitchEntity toEntity(GeneratedPitch p) {
        if (p == null) return null;
        GeneratedPitchEntity entity = new GeneratedPitchEntity();
        entity.setId(p.getId());
        entity.setUserId(p.getUserId());
        entity.setPitchType(p.getPitchType().name());
        entity.setContent(p.getContent());
        entity.setProfileSnapshotId(p.getProfileSnapshotId());
        entity.setGeneratedAt(p.getGeneratedAt());
        return entity;
    }
}
