package com.nihongodev.platform.infrastructure.persistence.adapter;

import com.nihongodev.platform.application.port.out.CodeExerciseRepositoryPort;
import com.nihongodev.platform.domain.model.*;
import com.nihongodev.platform.infrastructure.persistence.mapper.CodeReviewExercisePersistenceMapper;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaCodeReviewExerciseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CodeExerciseRepositoryAdapter implements CodeExerciseRepositoryPort {

    private final JpaCodeReviewExerciseRepository jpaRepository;
    private final CodeReviewExercisePersistenceMapper mapper;

    public CodeExerciseRepositoryAdapter(JpaCodeReviewExerciseRepository jpaRepository,
                                         CodeReviewExercisePersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public CodeReviewExercise save(CodeReviewExercise exercise) {
        var entity = mapper.toEntity(exercise);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<CodeReviewExercise> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<CodeReviewExercise> findPublished(ExerciseType type, JapaneseLevel difficulty, CodeContext context) {
        String typeStr = type != null ? type.name() : null;
        String diffStr = difficulty != null ? difficulty.name() : null;
        String contextStr = context != null ? context.name() : null;
        return jpaRepository.findPublished(typeStr, diffStr, contextStr).stream()
            .map(mapper::toDomain)
            .toList();
    }
}
