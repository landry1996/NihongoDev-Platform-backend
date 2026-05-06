package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.VocabularyMasteryDto;
import com.nihongodev.platform.application.port.in.ReviewVocabularyPort;
import com.nihongodev.platform.application.port.out.VocabularyMasteryRepositoryPort;
import com.nihongodev.platform.application.port.out.VocabularyRepositoryPort;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.VocabularyMastery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ReviewVocabularyUseCase implements ReviewVocabularyPort {

    private final VocabularyMasteryRepositoryPort masteryRepository;
    private final VocabularyRepositoryPort vocabularyRepository;

    public ReviewVocabularyUseCase(VocabularyMasteryRepositoryPort masteryRepository,
                                   VocabularyRepositoryPort vocabularyRepository) {
        this.masteryRepository = masteryRepository;
        this.vocabularyRepository = vocabularyRepository;
    }

    @Override
    @Transactional
    public VocabularyMasteryDto recordReview(UUID userId, UUID vocabularyId, boolean correct) {
        if (!vocabularyRepository.existsById(vocabularyId)) {
            throw new ResourceNotFoundException("Vocabulary", "id", vocabularyId);
        }

        VocabularyMastery mastery = masteryRepository.findByUserIdAndVocabularyId(userId, vocabularyId)
                .orElseGet(() -> VocabularyMastery.create(userId, vocabularyId));

        mastery.recordReview(correct);
        VocabularyMastery saved = masteryRepository.save(mastery);
        return mapToDto(saved);
    }

    @Override
    public List<VocabularyMasteryDto> getDueForReview(UUID userId) {
        return masteryRepository.findDueForReview(userId, LocalDateTime.now())
                .stream().map(this::mapToDto).toList();
    }

    @Override
    public List<VocabularyMasteryDto> getUserMasteryStats(UUID userId) {
        return masteryRepository.findByUserId(userId)
                .stream().map(this::mapToDto).toList();
    }

    private VocabularyMasteryDto mapToDto(VocabularyMastery m) {
        return new VocabularyMasteryDto(
                m.getId(), m.getVocabularyId(), m.getMasteryLevel(),
                m.getEaseFactor(), m.getIntervalDays(), m.getRepetitions(),
                m.getNextReviewAt(), m.getCorrectCount(), m.getIncorrectCount()
        );
    }
}
