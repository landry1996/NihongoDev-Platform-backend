package com.nihongodev.platform.infrastructure.persistence.repository;

import com.nihongodev.platform.infrastructure.persistence.entity.CodeReviewExerciseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface JpaCodeReviewExerciseRepository extends JpaRepository<CodeReviewExerciseEntity, UUID> {

    @Query("SELECT e FROM CodeReviewExerciseEntity e WHERE e.published = true " +
           "AND (:type IS NULL OR e.exerciseType = :type) " +
           "AND (:difficulty IS NULL OR e.difficulty = :difficulty) " +
           "AND (:context IS NULL OR e.codeContext = :context)")
    List<CodeReviewExerciseEntity> findPublished(@Param("type") String type,
                                                 @Param("difficulty") String difficulty,
                                                 @Param("context") String context);
}
