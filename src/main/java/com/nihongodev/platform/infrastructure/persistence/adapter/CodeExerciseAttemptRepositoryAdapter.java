package com.nihongodev.platform.infrastructure.persistence.adapter;

import com.nihongodev.platform.application.port.out.CodeExerciseAttemptRepositoryPort;
import com.nihongodev.platform.domain.model.CodeExerciseAttempt;
import com.nihongodev.platform.infrastructure.persistence.mapper.CodeExerciseAttemptPersistenceMapper;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaCodeExerciseAttemptRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class CodeExerciseAttemptRepositoryAdapter implements CodeExerciseAttemptRepositoryPort {

    private final JpaCodeExerciseAttemptRepository jpaRepository;
    private final CodeExerciseAttemptPersistenceMapper mapper;

    public CodeExerciseAttemptRepositoryAdapter(JpaCodeExerciseAttemptRepository jpaRepository,
                                                CodeExerciseAttemptPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public CodeExerciseAttempt save(CodeExerciseAttempt attempt) {
        var entity = mapper.toEntity(attempt);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<CodeExerciseAttempt> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<CodeExerciseAttempt> findByUserIdAndExerciseId(UUID userId, UUID exerciseId) {
        return jpaRepository.findByUserIdAndExerciseId(userId, exerciseId).stream()
            .map(mapper::toDomain)
            .toList();
    }
}
