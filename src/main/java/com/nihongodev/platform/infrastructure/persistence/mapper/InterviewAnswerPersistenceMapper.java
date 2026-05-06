package com.nihongodev.platform.infrastructure.persistence.mapper;

import com.nihongodev.platform.domain.model.InterviewAnswer;
import com.nihongodev.platform.infrastructure.persistence.entity.InterviewAnswerEntity;
import org.springframework.stereotype.Component;

@Component
public class InterviewAnswerPersistenceMapper {

    public InterviewAnswer toDomain(InterviewAnswerEntity entity) {
        if (entity == null) return null;
        InterviewAnswer answer = new InterviewAnswer();
        answer.setId(entity.getId());
        answer.setSessionId(entity.getSessionId());
        answer.setQuestionId(entity.getQuestionId());
        answer.setAnswerText(entity.getAnswerText());
        answer.setTimeSpentSeconds(entity.getTimeSpentSeconds());
        answer.setLanguageScore(entity.getLanguageScore());
        answer.setTechnicalScore(entity.getTechnicalScore());
        answer.setCommunicationScore(entity.getCommunicationScore());
        answer.setCulturalScore(entity.getCulturalScore());
        answer.setOverallScore(entity.getOverallScore());
        answer.setSubmittedAt(entity.getSubmittedAt());
        return answer;
    }

    public InterviewAnswerEntity toEntity(InterviewAnswer answer) {
        if (answer == null) return null;
        InterviewAnswerEntity entity = new InterviewAnswerEntity();
        entity.setId(answer.getId());
        entity.setSessionId(answer.getSessionId());
        entity.setQuestionId(answer.getQuestionId());
        entity.setAnswerText(answer.getAnswerText());
        entity.setTimeSpentSeconds(answer.getTimeSpentSeconds());
        entity.setLanguageScore(answer.getLanguageScore());
        entity.setTechnicalScore(answer.getTechnicalScore());
        entity.setCommunicationScore(answer.getCommunicationScore());
        entity.setCulturalScore(answer.getCulturalScore());
        entity.setOverallScore(answer.getOverallScore());
        entity.setSubmittedAt(answer.getSubmittedAt());
        return entity;
    }
}
