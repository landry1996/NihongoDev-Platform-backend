package com.nihongodev.platform.infrastructure.persistence.adapter;

import com.nihongodev.platform.application.port.out.CodeJapaneseProgressRepositoryPort;
import com.nihongodev.platform.domain.model.CodeJapaneseProgress;
import com.nihongodev.platform.domain.model.ExerciseType;
import com.nihongodev.platform.infrastructure.persistence.mapper.CodeJapaneseProgressPersistenceMapper;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaCodeJapaneseProgressRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CodeJapaneseProgressRepositoryAdapter implements CodeJapaneseProgressRepositoryPort {

    private final JpaCodeJapaneseProgressRepository jpaRepository;
    private final CodeJapaneseProgressPersistenceMapper mapper;

    public CodeJapaneseProgressRepositoryAdapter(JpaCodeJapaneseProgressRepository jpaRepository,
                                                  CodeJapaneseProgressPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public CodeJapaneseProgress save(CodeJapaneseProgress progress) {
        var entity = mapper.toEntity(progress);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<CodeJapaneseProgress> findByUserIdAndExerciseType(UUID userId, ExerciseType exerciseType) {
        return jpaRepository.findByUserIdAndExerciseType(userId, exerciseType.name())
            .map(mapper::toDomain);
    }

    @Override
    public List<CodeJapaneseProgress> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).stream()
            .map(mapper::toDomain)
            .toList();
    }
}
