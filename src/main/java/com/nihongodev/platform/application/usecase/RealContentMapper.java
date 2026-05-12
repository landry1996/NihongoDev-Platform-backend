package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.ContentAnnotationDto;
import com.nihongodev.platform.application.dto.ContentReadingSessionDto;
import com.nihongodev.platform.application.dto.RealContentDto;
import com.nihongodev.platform.domain.model.ContentAnnotation;
import com.nihongodev.platform.domain.model.ContentReadingSession;
import com.nihongodev.platform.domain.model.RealContent;

import java.util.List;

public final class RealContentMapper {

    private RealContentMapper() {}

    public static RealContentDto toDto(RealContent content) {
        List<ContentAnnotationDto> annotationDtos = content.getAnnotations().stream()
            .map(RealContentMapper::toAnnotationDto)
            .toList();

        return new RealContentDto(
            content.getId(),
            content.getTitle(),
            content.getTitleReading(),
            content.getBody(),
            content.getSummary(),
            content.getSourceUrl(),
            content.getSource(),
            content.getDomain(),
            content.getDifficulty(),
            content.getReadingDifficulty(),
            annotationDtos,
            content.getTags(),
            content.getKeyVocabulary(),
            content.getWordCount(),
            content.getKanjiCount(),
            content.getEstimatedReadingMinutes(),
            content.getStatus(),
            content.getAuthorName(),
            content.getPublishedAt()
        );
    }

    public static ContentAnnotationDto toAnnotationDto(ContentAnnotation annotation) {
        return new ContentAnnotationDto(
            annotation.id(),
            annotation.startOffset(),
            annotation.endOffset(),
            annotation.surfaceForm(),
            annotation.reading(),
            annotation.meaning(),
            annotation.annotationType(),
            annotation.requiredLevel(),
            annotation.grammarNote(),
            annotation.culturalNote()
        );
    }

    public static ContentReadingSessionDto toSessionDto(ContentReadingSession session, String contentTitle) {
        return new ContentReadingSessionDto(
            session.getId(),
            session.getContentId(),
            contentTitle,
            session.getReadingTimeSeconds(),
            session.getAnnotationsViewed(),
            session.getVocabularyLookedUp(),
            session.getComprehensionScore(),
            session.getSavedVocabulary(),
            session.isCompleted(),
            session.getStartedAt(),
            session.getCompletedAt()
        );
    }
}
