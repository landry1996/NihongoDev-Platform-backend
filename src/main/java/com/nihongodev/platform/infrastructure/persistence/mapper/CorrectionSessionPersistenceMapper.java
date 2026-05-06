package com.nihongodev.platform.infrastructure.persistence.mapper;

import com.nihongodev.platform.domain.model.*;
import com.nihongodev.platform.infrastructure.persistence.entity.CorrectionAnnotationEntity;
import com.nihongodev.platform.infrastructure.persistence.entity.CorrectionSessionEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CorrectionSessionPersistenceMapper {

    public CorrectionSessionEntity toEntity(CorrectionSession domain) {
        CorrectionSessionEntity entity = new CorrectionSessionEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setOriginalText(domain.getOriginalText());
        entity.setTextType(domain.getTextType().name());
        entity.setTargetLevel(domain.getTargetLevel().name());
        entity.setGrammarScore(domain.getScore().getGrammarScore());
        entity.setVocabularyScore(domain.getScore().getVocabularyScore());
        entity.setPolitenessScore(domain.getScore().getPolitenessScore());
        entity.setClarityScore(domain.getScore().getClarityScore());
        entity.setNaturalnessScore(domain.getScore().getNaturalnessScore());
        entity.setProfessionalScore(domain.getScore().getProfessionalScore());
        entity.setOverallScore(domain.getScore().getOverallScore());
        entity.setTotalAnnotations(domain.getTotalAnnotations());
        entity.setErrorCount(domain.getErrorCount());
        entity.setWarningCount(domain.getWarningCount());
        entity.setInfoCount(domain.getInfoCount());
        entity.setCreatedAt(domain.getCreatedAt());

        if (domain.getAnnotations() != null) {
            List<CorrectionAnnotationEntity> annotationEntities = domain.getAnnotations().stream()
                    .map(a -> toAnnotationEntity(a, entity))
                    .toList();
            entity.setAnnotations(new ArrayList<>(annotationEntities));
        }

        return entity;
    }

    public CorrectionSession toDomain(CorrectionSessionEntity entity) {
        CorrectionSession session = new CorrectionSession();
        session.setId(entity.getId());
        session.setUserId(entity.getUserId());
        session.setOriginalText(entity.getOriginalText());
        session.setTextType(TextType.valueOf(entity.getTextType()));
        session.setTargetLevel(JapaneseLevel.valueOf(entity.getTargetLevel()));

        CorrectionScore score = CorrectionScore.of(
                entity.getGrammarScore(), entity.getVocabularyScore(),
                entity.getPolitenessScore(), entity.getClarityScore(),
                entity.getNaturalnessScore(), entity.getProfessionalScore()
        );
        session.setScore(score);
        session.setTotalAnnotations(entity.getTotalAnnotations());
        session.setErrorCount(entity.getErrorCount());
        session.setWarningCount(entity.getWarningCount());
        session.setInfoCount(entity.getInfoCount());
        session.setCreatedAt(entity.getCreatedAt());

        if (entity.getAnnotations() != null) {
            List<Annotation> annotations = entity.getAnnotations().stream()
                    .map(this::toAnnotationDomain)
                    .toList();
            session.setAnnotations(new ArrayList<>(annotations));
        } else {
            session.setAnnotations(new ArrayList<>());
        }

        return session;
    }

    private CorrectionAnnotationEntity toAnnotationEntity(Annotation annotation,
                                                          CorrectionSessionEntity session) {
        CorrectionAnnotationEntity entity = new CorrectionAnnotationEntity();
        entity.setId(annotation.getId());
        entity.setSession(session);
        entity.setStartOffset(annotation.getStartOffset());
        entity.setEndOffset(annotation.getEndOffset());
        entity.setSeverity(annotation.getSeverity().name());
        entity.setCategory(annotation.getCategory().name());
        entity.setOriginalText(annotation.getOriginal());
        entity.setSuggestion(annotation.getSuggestion());
        entity.setExplanation(annotation.getExplanation());
        entity.setRuleId(annotation.getRuleId());
        return entity;
    }

    private Annotation toAnnotationDomain(CorrectionAnnotationEntity entity) {
        Annotation a = new Annotation();
        a.setId(entity.getId());
        a.setStartOffset(entity.getStartOffset());
        a.setEndOffset(entity.getEndOffset());
        a.setSeverity(Severity.valueOf(entity.getSeverity()));
        a.setCategory(AnnotationCategory.valueOf(entity.getCategory()));
        a.setOriginal(entity.getOriginalText());
        a.setSuggestion(entity.getSuggestion());
        a.setExplanation(entity.getExplanation());
        a.setRuleId(entity.getRuleId());
        return a;
    }
}
