package com.nihongodev.platform.infrastructure.persistence.repository;

import com.nihongodev.platform.infrastructure.persistence.entity.VocabularyRelationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaVocabularyRelationRepository extends JpaRepository<VocabularyRelationEntity, UUID> {
    List<VocabularyRelationEntity> findBySourceVocabularyId(UUID sourceId);
    List<VocabularyRelationEntity> findByTargetVocabularyId(UUID targetId);
}
