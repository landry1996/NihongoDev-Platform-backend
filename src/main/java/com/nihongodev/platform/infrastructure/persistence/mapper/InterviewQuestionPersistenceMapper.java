package com.nihongodev.platform.infrastructure.persistence.mapper;

import com.nihongodev.platform.domain.model.*;
import com.nihongodev.platform.infrastructure.persistence.entity.InterviewQuestionEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class InterviewQuestionPersistenceMapper {

    public InterviewQuestion toDomain(InterviewQuestionEntity entity) {
        if (entity == null) return null;
        InterviewQuestion q = new InterviewQuestion();
        q.setId(entity.getId());
        q.setInterviewType(InterviewType.valueOf(entity.getInterviewType()));
        q.setDifficulty(InterviewDifficulty.valueOf(entity.getDifficulty()));
        q.setPhase(InterviewPhase.valueOf(entity.getPhase()));
        q.setContent(entity.getContent());
        q.setContentJapanese(entity.getContentJapanese());
        q.setModelAnswer(entity.getModelAnswer());
        q.setExpectedKeywords(parseKeywords(entity.getExpectedKeywords()));
        q.setScoringCriteria(entity.getScoringCriteria());
        q.setTimeLimitSeconds(entity.getTimeLimitSeconds());
        q.setOrderIndex(entity.getOrderIndex());
        q.setCreatedAt(entity.getCreatedAt());
        return q;
    }

    public InterviewQuestionEntity toEntity(InterviewQuestion q) {
        if (q == null) return null;
        InterviewQuestionEntity entity = new InterviewQuestionEntity();
        entity.setId(q.getId());
        entity.setInterviewType(q.getInterviewType().name());
        entity.setDifficulty(q.getDifficulty().name());
        entity.setPhase(q.getPhase().name());
        entity.setContent(q.getContent());
        entity.setContentJapanese(q.getContentJapanese());
        entity.setModelAnswer(q.getModelAnswer());
        entity.setExpectedKeywords(joinKeywords(q.getExpectedKeywords()));
        entity.setScoringCriteria(q.getScoringCriteria());
        entity.setTimeLimitSeconds(q.getTimeLimitSeconds());
        entity.setOrderIndex(q.getOrderIndex());
        entity.setCreatedAt(q.getCreatedAt());
        return entity;
    }

    private List<String> parseKeywords(String keywords) {
        if (keywords == null || keywords.isBlank()) return Collections.emptyList();
        return Arrays.asList(keywords.split("\\|\\|"));
    }

    private String joinKeywords(List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) return null;
        return String.join("||", keywords);
    }
}
