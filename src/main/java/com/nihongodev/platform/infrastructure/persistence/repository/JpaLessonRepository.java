package com.nihongodev.platform.infrastructure.persistence.repository;

import com.nihongodev.platform.infrastructure.persistence.entity.LessonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaLessonRepository extends JpaRepository<LessonEntity, UUID> {
    List<LessonEntity> findByType(String type);
    List<LessonEntity> findByLevel(String level);
    List<LessonEntity> findByTypeAndLevel(String type, String level);
    List<LessonEntity> findByPublishedTrue();
}
