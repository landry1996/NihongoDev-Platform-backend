package com.nihongodev.platform.infrastructure.persistence.mapper;

import com.nihongodev.platform.domain.model.MasteryLevel;
import com.nihongodev.platform.domain.model.VocabularyMastery;
import com.nihongodev.platform.infrastructure.persistence.entity.VocabularyMasteryEntity;
import org.springframework.stereotype.Component;

@Component
public class VocabularyMasteryPersistenceMapper {

    public VocabularyMastery toDomain(VocabularyMasteryEntity entity) {
        if (entity == null) return null;
        VocabularyMastery m = new VocabularyMastery();
        m.setId(entity.getId());
        m.setUserId(entity.getUserId());
        m.setVocabularyId(entity.getVocabularyId());
        m.setMasteryLevel(MasteryLevel.valueOf(entity.getMasteryLevel()));
        m.setEaseFactor(entity.getEaseFactor());
        m.setIntervalDays(entity.getIntervalDays());
        m.setRepetitions(entity.getRepetitions());
        m.setNextReviewAt(entity.getNextReviewAt());
        m.setLastReviewedAt(entity.getLastReviewedAt());
        m.setCorrectCount(entity.getCorrectCount());
        m.setIncorrectCount(entity.getIncorrectCount());
        m.setCreatedAt(entity.getCreatedAt());
        m.setUpdatedAt(entity.getUpdatedAt());
        return m;
    }

    public VocabularyMasteryEntity toEntity(VocabularyMastery m) {
        if (m == null) return null;
        VocabularyMasteryEntity entity = new VocabularyMasteryEntity();
        entity.setId(m.getId());
        entity.setUserId(m.getUserId());
        entity.setVocabularyId(m.getVocabularyId());
        entity.setMasteryLevel(m.getMasteryLevel().name());
        entity.setEaseFactor(m.getEaseFactor());
        entity.setIntervalDays(m.getIntervalDays());
        entity.setRepetitions(m.getRepetitions());
        entity.setNextReviewAt(m.getNextReviewAt());
        entity.setLastReviewedAt(m.getLastReviewedAt());
        entity.setCorrectCount(m.getCorrectCount());
        entity.setIncorrectCount(m.getIncorrectCount());
        entity.setCreatedAt(m.getCreatedAt());
        entity.setUpdatedAt(m.getUpdatedAt());
        return entity;
    }
}
