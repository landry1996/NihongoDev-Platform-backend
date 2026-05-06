package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.VocabularyMasteryDto;

import java.util.List;
import java.util.UUID;

public interface ReviewVocabularyPort {
    VocabularyMasteryDto recordReview(UUID userId, UUID vocabularyId, boolean correct);
    List<VocabularyMasteryDto> getDueForReview(UUID userId);
    List<VocabularyMasteryDto> getUserMasteryStats(UUID userId);
}
