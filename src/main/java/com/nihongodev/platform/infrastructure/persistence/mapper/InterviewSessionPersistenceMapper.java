package com.nihongodev.platform.infrastructure.persistence.mapper;

import com.nihongodev.platform.domain.model.*;
import com.nihongodev.platform.infrastructure.persistence.entity.InterviewSessionEntity;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InterviewSessionPersistenceMapper {

    public InterviewSession toDomain(InterviewSessionEntity entity) {
        if (entity == null) return null;
        InterviewSession session = new InterviewSession();
        session.setId(entity.getId());
        session.setUserId(entity.getUserId());
        session.setInterviewType(InterviewType.valueOf(entity.getInterviewType()));
        session.setDifficulty(InterviewDifficulty.valueOf(entity.getDifficulty()));
        session.setCurrentPhase(InterviewPhase.valueOf(entity.getCurrentPhase()));
        session.setStatus(SessionStatus.valueOf(entity.getStatus()));
        session.setCurrentQuestionIndex(entity.getCurrentQuestionIndex());
        session.setTotalQuestions(entity.getTotalQuestions());
        session.setLanguageScore(entity.getLanguageScore());
        session.setTechnicalScore(entity.getTechnicalScore());
        session.setCommunicationScore(entity.getCommunicationScore());
        session.setCulturalScore(entity.getCulturalScore());
        session.setOverallScore(entity.getOverallScore());
        session.setTotalTimeSpentSeconds(entity.getTotalTimeSpentSeconds());
        session.setPassed(entity.isPassed());
        session.setQuestionIds(parseUuidList(entity.getQuestionIds()));
        session.setStartedAt(entity.getStartedAt());
        session.setCompletedAt(entity.getCompletedAt());
        return session;
    }

    public InterviewSessionEntity toEntity(InterviewSession session) {
        if (session == null) return null;
        InterviewSessionEntity entity = new InterviewSessionEntity();
        entity.setId(session.getId());
        entity.setUserId(session.getUserId());
        entity.setInterviewType(session.getInterviewType().name());
        entity.setDifficulty(session.getDifficulty().name());
        entity.setCurrentPhase(session.getCurrentPhase().name());
        entity.setStatus(session.getStatus().name());
        entity.setCurrentQuestionIndex(session.getCurrentQuestionIndex());
        entity.setTotalQuestions(session.getTotalQuestions());
        entity.setLanguageScore(session.getLanguageScore());
        entity.setTechnicalScore(session.getTechnicalScore());
        entity.setCommunicationScore(session.getCommunicationScore());
        entity.setCulturalScore(session.getCulturalScore());
        entity.setOverallScore(session.getOverallScore());
        entity.setTotalTimeSpentSeconds(session.getTotalTimeSpentSeconds());
        entity.setPassed(session.isPassed());
        entity.setQuestionIds(joinUuidList(session.getQuestionIds()));
        entity.setStartedAt(session.getStartedAt());
        entity.setCompletedAt(session.getCompletedAt());
        return entity;
    }

    private List<UUID> parseUuidList(String ids) {
        if (ids == null || ids.isBlank()) return new ArrayList<>();
        return Arrays.stream(ids.split(","))
                .map(String::trim)
                .map(UUID::fromString)
                .collect(Collectors.toList());
    }

    private String joinUuidList(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) return null;
        return ids.stream().map(UUID::toString).collect(Collectors.joining(","));
    }
}
