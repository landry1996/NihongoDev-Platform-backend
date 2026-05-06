package com.nihongodev.platform.infrastructure.persistence.adapter;

import com.nihongodev.platform.application.port.out.QuizResultRepositoryPort;
import com.nihongodev.platform.domain.model.QuizResult;
import com.nihongodev.platform.infrastructure.persistence.mapper.QuizResultPersistenceMapper;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaQuizResultRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class QuizResultRepositoryAdapter implements QuizResultRepositoryPort {

    private final JpaQuizResultRepository jpaRepository;
    private final QuizResultPersistenceMapper mapper;

    public QuizResultRepositoryAdapter(JpaQuizResultRepository jpaRepository, QuizResultPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public QuizResult save(QuizResult result) {
        var entity = mapper.toEntity(result);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<QuizResult> findByAttemptId(UUID attemptId) {
        return jpaRepository.findByAttemptId(attemptId).map(mapper::toDomain);
    }

    @Override
    public List<QuizResult> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<QuizResult> findByQuizId(UUID quizId) {
        return jpaRepository.findByQuizId(quizId).stream().map(mapper::toDomain).toList();
    }
}
