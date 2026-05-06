package com.nihongodev.platform.infrastructure.persistence.adapter;

import com.nihongodev.platform.application.port.out.QuestionRepositoryPort;
import com.nihongodev.platform.domain.model.Question;
import com.nihongodev.platform.infrastructure.persistence.mapper.QuestionPersistenceMapper;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaQuestionRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class QuestionRepositoryAdapter implements QuestionRepositoryPort {

    private final JpaQuestionRepository jpaRepository;
    private final QuestionPersistenceMapper mapper;

    public QuestionRepositoryAdapter(JpaQuestionRepository jpaRepository, QuestionPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Question save(Question question) {
        var entity = mapper.toEntity(question);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Question> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Question> findByQuizId(UUID quizId) {
        return jpaRepository.findByQuizIdOrderByOrderIndexAsc(quizId).stream().map(mapper::toDomain).toList();
    }
}
