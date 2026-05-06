package com.nihongodev.platform.infrastructure.persistence.adapter;

import com.nihongodev.platform.application.port.out.VocabularyMasteryRepositoryPort;
import com.nihongodev.platform.domain.model.VocabularyMastery;
import com.nihongodev.platform.infrastructure.persistence.mapper.VocabularyMasteryPersistenceMapper;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaVocabularyMasteryRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class VocabularyMasteryRepositoryAdapter implements VocabularyMasteryRepositoryPort {

    private final JpaVocabularyMasteryRepository jpaRepository;
    private final VocabularyMasteryPersistenceMapper mapper;

    public VocabularyMasteryRepositoryAdapter(JpaVocabularyMasteryRepository jpaRepository,
                                              VocabularyMasteryPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public VocabularyMastery save(VocabularyMastery mastery) {
        var entity = mapper.toEntity(mastery);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<VocabularyMastery> findByUserIdAndVocabularyId(UUID userId, UUID vocabularyId) {
        return jpaRepository.findByUserIdAndVocabularyId(userId, vocabularyId).map(mapper::toDomain);
    }

    @Override
    public List<VocabularyMastery> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<VocabularyMastery> findDueForReview(UUID userId, LocalDateTime before) {
        return jpaRepository.findByUserIdAndNextReviewAtBefore(userId, before).stream().map(mapper::toDomain).toList();
    }
}
