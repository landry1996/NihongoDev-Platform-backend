package com.nihongodev.platform.infrastructure.persistence.adapter;

import com.nihongodev.platform.application.port.out.QuizAttemptRepositoryPort;
import com.nihongodev.platform.domain.model.QuizAttempt;
import com.nihongodev.platform.infrastructure.persistence.mapper.QuizAttemptPersistenceMapper;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaQuizAttemptRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class QuizAttemptRepositoryAdapter implements QuizAttemptRepositoryPort {

    private final JpaQuizAttemptRepository jpaRepository;
    private final QuizAttemptPersistenceMapper mapper;

    public QuizAttemptRepositoryAdapter(JpaQuizAttemptRepository jpaRepository, QuizAttemptPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public QuizAttempt save(QuizAttempt attempt) {
        var entity = mapper.toEntity(attempt);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<QuizAttempt> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<QuizAttempt> findByUserIdAndQuizId(UUID userId, UUID quizId) {
        return jpaRepository.findByUserIdAndQuizId(userId, quizId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<QuizAttempt> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).stream().map(mapper::toDomain).toList();
    }
}
