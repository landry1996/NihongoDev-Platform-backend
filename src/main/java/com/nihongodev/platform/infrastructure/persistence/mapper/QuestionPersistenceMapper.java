package com.nihongodev.platform.infrastructure.persistence.mapper;

import com.nihongodev.platform.domain.model.DifficultyLevel;
import com.nihongodev.platform.domain.model.Question;
import com.nihongodev.platform.domain.model.QuestionType;
import com.nihongodev.platform.infrastructure.persistence.entity.QuestionEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class QuestionPersistenceMapper {

    public Question toDomain(QuestionEntity entity) {
        if (entity == null) return null;
        Question q = new Question();
        q.setId(entity.getId());
        q.setQuizId(entity.getQuizId());
        q.setContent(entity.getContent());
        q.setCorrectAnswer(entity.getCorrectAnswer());
        q.setExplanation(entity.getExplanation());
        q.setQuestionType(entity.getQuestionType() != null ? QuestionType.valueOf(entity.getQuestionType()) : QuestionType.MULTIPLE_CHOICE);
        q.setDifficultyLevel(entity.getDifficultyLevel() != null ? DifficultyLevel.valueOf(entity.getDifficultyLevel()) : DifficultyLevel.MEDIUM);
        q.setOptions(parseOptions(entity.getOptions()));
        q.setPoints(entity.getPoints());
        q.setTimeLimitSeconds(entity.getTimeLimitSeconds());
        q.setOrderIndex(entity.getOrderIndex());
        q.setCreatedAt(entity.getCreatedAt());
        return q;
    }

    public QuestionEntity toEntity(Question q) {
        if (q == null) return null;
        QuestionEntity entity = new QuestionEntity();
        entity.setId(q.getId());
        entity.setQuizId(q.getQuizId());
        entity.setContent(q.getContent());
        entity.setCorrectAnswer(q.getCorrectAnswer());
        entity.setExplanation(q.getExplanation());
        entity.setQuestionType(q.getQuestionType() != null ? q.getQuestionType().name() : null);
        entity.setDifficultyLevel(q.getDifficultyLevel() != null ? q.getDifficultyLevel().name() : null);
        entity.setOptions(joinOptions(q.getOptions()));
        entity.setPoints(q.getPoints());
        entity.setTimeLimitSeconds(q.getTimeLimitSeconds());
        entity.setOrderIndex(q.getOrderIndex());
        entity.setCreatedAt(q.getCreatedAt());
        return entity;
    }

    private List<String> parseOptions(String options) {
        if (options == null || options.isBlank()) return Collections.emptyList();
        return Arrays.asList(options.split("\\|\\|"));
    }

    private String joinOptions(List<String> options) {
        if (options == null || options.isEmpty()) return null;
        return String.join("||", options);
    }
}
