package com.nihongodev.platform.application.port.out;

import com.nihongodev.platform.domain.model.VocabularyRelation;

import java.util.List;
import java.util.UUID;

public interface VocabularyRelationRepositoryPort {
    VocabularyRelation save(VocabularyRelation relation);
    List<VocabularyRelation> findBySourceId(UUID sourceId);
    List<VocabularyRelation> findByTargetId(UUID targetId);
    void deleteById(UUID id);
}
