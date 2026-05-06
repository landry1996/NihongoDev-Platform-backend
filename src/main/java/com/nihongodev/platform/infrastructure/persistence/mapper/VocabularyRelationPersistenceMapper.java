package com.nihongodev.platform.infrastructure.persistence.mapper;

import com.nihongodev.platform.domain.model.RelationType;
import com.nihongodev.platform.domain.model.VocabularyRelation;
import com.nihongodev.platform.infrastructure.persistence.entity.VocabularyRelationEntity;
import org.springframework.stereotype.Component;

@Component
public class VocabularyRelationPersistenceMapper {

    public VocabularyRelation toDomain(VocabularyRelationEntity entity) {
        if (entity == null) return null;
        VocabularyRelation r = new VocabularyRelation();
        r.setId(entity.getId());
        r.setSourceVocabularyId(entity.getSourceVocabularyId());
        r.setTargetVocabularyId(entity.getTargetVocabularyId());
        r.setRelationType(RelationType.valueOf(entity.getRelationType()));
        r.setCreatedAt(entity.getCreatedAt());
        return r;
    }

    public VocabularyRelationEntity toEntity(VocabularyRelation r) {
        if (r == null) return null;
        VocabularyRelationEntity entity = new VocabularyRelationEntity();
        entity.setId(r.getId());
        entity.setSourceVocabularyId(r.getSourceVocabularyId());
        entity.setTargetVocabularyId(r.getTargetVocabularyId());
        entity.setRelationType(r.getRelationType().name());
        entity.setCreatedAt(r.getCreatedAt());
        return entity;
    }
}
