package com.nihongodev.platform.infrastructure.persistence.repository;

import com.nihongodev.platform.infrastructure.persistence.entity.VocabularyMasteryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaVocabularyMasteryRepository extends JpaRepository<VocabularyMasteryEntity, UUID> {
    Optional<VocabularyMasteryEntity> findByUserIdAndVocabularyId(UUID userId, UUID vocabularyId);
    List<VocabularyMasteryEntity> findByUserId(UUID userId);
    List<VocabularyMasteryEntity> findByUserIdAndNextReviewAtBefore(UUID userId, LocalDateTime before);
}
