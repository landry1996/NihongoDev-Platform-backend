package com.nihongodev.platform.infrastructure.persistence.mapper;

import com.nihongodev.platform.domain.model.Vocabulary;
import com.nihongodev.platform.domain.model.VocabularyCategory;
import com.nihongodev.platform.domain.model.VocabularyLevel;
import com.nihongodev.platform.infrastructure.persistence.entity.VocabularyEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class VocabularyPersistenceMapper {

    public Vocabulary toDomain(VocabularyEntity entity) {
        if (entity == null) return null;
        Vocabulary v = new Vocabulary();
        v.setId(entity.getId());
        v.setLessonId(entity.getLessonId());
        v.setFrench(entity.getFrench());
        v.setEnglish(entity.getEnglish());
        v.setJapanese(entity.getJapanese());
        v.setRomaji(entity.getRomaji());
        v.setExample(entity.getExample());
        v.setCodeExample(entity.getCodeExample());
        v.setCategory(VocabularyCategory.valueOf(entity.getCategory()));
        v.setLevel(entity.getLevel() != null ? VocabularyLevel.valueOf(entity.getLevel()) : VocabularyLevel.BEGINNER);
        v.setDomain(entity.getDomain());
        v.setTags(parseTags(entity.getTags()));
        v.setDifficultyScore(entity.getDifficultyScore());
        v.setCreatedAt(entity.getCreatedAt());
        v.setUpdatedAt(entity.getUpdatedAt());
        return v;
    }

    public VocabularyEntity toEntity(Vocabulary v) {
        if (v == null) return null;
        VocabularyEntity entity = new VocabularyEntity();
        entity.setId(v.getId());
        entity.setLessonId(v.getLessonId());
        entity.setFrench(v.getFrench());
        entity.setEnglish(v.getEnglish());
        entity.setJapanese(v.getJapanese());
        entity.setRomaji(v.getRomaji());
        entity.setExample(v.getExample());
        entity.setCodeExample(v.getCodeExample());
        entity.setCategory(v.getCategory().name());
        entity.setLevel(v.getLevel() != null ? v.getLevel().name() : null);
        entity.setDomain(v.getDomain());
        entity.setTags(joinTags(v.getTags()));
        entity.setDifficultyScore(v.getDifficultyScore());
        entity.setCreatedAt(v.getCreatedAt());
        entity.setUpdatedAt(v.getUpdatedAt());
        return entity;
    }

    private List<String> parseTags(String tags) {
        if (tags == null || tags.isBlank()) return Collections.emptyList();
        return Arrays.asList(tags.split(","));
    }

    private String joinTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) return null;
        return String.join(",", tags);
    }
}
