package com.nihongodev.platform.application.port.out;

import com.nihongodev.platform.domain.model.VocabularyMastery;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VocabularyMasteryRepositoryPort {
    VocabularyMastery save(VocabularyMastery mastery);
    Optional<VocabularyMastery> findByUserIdAndVocabularyId(UUID userId, UUID vocabularyId);
    List<VocabularyMastery> findByUserId(UUID userId);
    List<VocabularyMastery> findDueForReview(UUID userId, LocalDateTime before);
}
