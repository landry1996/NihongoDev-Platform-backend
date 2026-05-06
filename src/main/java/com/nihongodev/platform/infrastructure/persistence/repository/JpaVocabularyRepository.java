package com.nihongodev.platform.infrastructure.persistence.repository;

import com.nihongodev.platform.infrastructure.persistence.entity.VocabularyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaVocabularyRepository extends JpaRepository<VocabularyEntity, UUID>, JpaSpecificationExecutor<VocabularyEntity> {
    List<VocabularyEntity> findByCategory(String category);
    List<VocabularyEntity> findByLevel(String level);
    List<VocabularyEntity> findByCategoryAndLevel(String category, String level);
    List<VocabularyEntity> findByLessonId(UUID lessonId);

    @Query("SELECT v FROM VocabularyEntity v WHERE " +
            "LOWER(v.french) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(v.english) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "v.japanese LIKE CONCAT('%', :query, '%') OR " +
            "LOWER(v.romaji) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<VocabularyEntity> searchByText(@Param("query") String query);
}
