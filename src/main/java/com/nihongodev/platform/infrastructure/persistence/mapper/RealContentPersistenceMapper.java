package com.nihongodev.platform.infrastructure.persistence.mapper;

import com.nihongodev.platform.domain.model.*;
import com.nihongodev.platform.infrastructure.persistence.entity.RealContentEntity;
import com.nihongodev.platform.infrastructure.persistence.entity.RealContentEntity.AnnotationJson;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class RealContentPersistenceMapper {

    public RealContent toDomain(RealContentEntity entity) {
        RealContent content = new RealContent();
        content.setId(entity.getId());
        content.setTitle(entity.getTitle());
        content.setTitleReading(entity.getTitleReading());
        content.setBody(entity.getBody());
        content.setSummary(entity.getSummary());
        content.setSourceUrl(entity.getSourceUrl());
        content.setSource(ContentSource.valueOf(entity.getSource()));
        content.setDomain(ContentDomain.valueOf(entity.getDomain()));
        if (entity.getDifficulty() != null) {
            content.setDifficulty(JapaneseLevel.valueOf(entity.getDifficulty()));
        }
        if (entity.getReadingDifficulty() != null) {
            content.setReadingDifficulty(ReadingDifficulty.valueOf(entity.getReadingDifficulty()));
        }
        content.setTags(entity.getTags() != null ? entity.getTags() : new ArrayList<>());
        content.setKeyVocabulary(entity.getKeyVocabulary() != null ? entity.getKeyVocabulary() : new ArrayList<>());
        content.setWordCount(entity.getWordCount());
        content.setKanjiCount(entity.getKanjiCount());
        content.setEstimatedReadingMinutes(entity.getEstimatedReadingMinutes());
        content.setStatus(ContentStatus.valueOf(entity.getStatus()));
        content.setAuthorName(entity.getAuthorName());
        content.setPublishedAt(entity.getPublishedAt());
        content.setIngestedAt(entity.getIngestedAt());
        content.setAnnotatedAt(entity.getAnnotatedAt());

        if (entity.getAnnotations() != null) {
            List<ContentAnnotation> annotations = entity.getAnnotations().stream()
                .map(this::toAnnotation)
                .toList();
            content.setAnnotations(new ArrayList<>(annotations));
        }

        return content;
    }

    public RealContentEntity toEntity(RealContent content) {
        RealContentEntity entity = new RealContentEntity();
        entity.setId(content.getId());
        entity.setTitle(content.getTitle());
        entity.setTitleReading(content.getTitleReading());
        entity.setBody(content.getBody());
        entity.setSummary(content.getSummary());
        entity.setSourceUrl(content.getSourceUrl());
        entity.setSource(content.getSource().name());
        entity.setDomain(content.getDomain().name());
        if (content.getDifficulty() != null) {
            entity.setDifficulty(content.getDifficulty().name());
        }
        if (content.getReadingDifficulty() != null) {
            entity.setReadingDifficulty(content.getReadingDifficulty().name());
        }
        entity.setTags(content.getTags());
        entity.setKeyVocabulary(content.getKeyVocabulary());
        entity.setWordCount(content.getWordCount());
        entity.setKanjiCount(content.getKanjiCount());
        entity.setEstimatedReadingMinutes(content.getEstimatedReadingMinutes());
        entity.setStatus(content.getStatus().name());
        entity.setAuthorName(content.getAuthorName());
        entity.setPublishedAt(content.getPublishedAt());
        entity.setIngestedAt(content.getIngestedAt());
        entity.setAnnotatedAt(content.getAnnotatedAt());

        if (content.getAnnotations() != null) {
            List<AnnotationJson> annotationJsons = content.getAnnotations().stream()
                .map(this::toAnnotationJson)
                .toList();
            entity.setAnnotations(annotationJsons);
        }

        return entity;
    }

    private ContentAnnotation toAnnotation(AnnotationJson json) {
        return new ContentAnnotation(
            UUID.fromString(json.id()),
            json.startOffset(),
            json.endOffset(),
            json.surfaceForm(),
            json.reading(),
            json.meaning(),
            AnnotationType.valueOf(json.annotationType()),
            JapaneseLevel.valueOf(json.requiredLevel()),
            json.grammarNote(),
            json.culturalNote()
        );
    }

    private AnnotationJson toAnnotationJson(ContentAnnotation annotation) {
        return new AnnotationJson(
            annotation.id().toString(),
            annotation.startOffset(),
            annotation.endOffset(),
            annotation.surfaceForm(),
            annotation.reading(),
            annotation.meaning(),
            annotation.annotationType().name(),
            annotation.requiredLevel().name(),
            annotation.grammarNote(),
            annotation.culturalNote()
        );
    }
}
