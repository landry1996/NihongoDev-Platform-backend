package com.nihongodev.platform.infrastructure.persistence.mapper;

import com.nihongodev.platform.domain.model.Lesson;
import com.nihongodev.platform.domain.model.LessonLevel;
import com.nihongodev.platform.domain.model.LessonType;
import com.nihongodev.platform.infrastructure.persistence.entity.LessonEntity;
import org.springframework.stereotype.Component;

@Component
public class LessonPersistenceMapper {

    public Lesson toDomain(LessonEntity entity) {
        if (entity == null) return null;
        Lesson lesson = new Lesson();
        lesson.setId(entity.getId());
        lesson.setTitle(entity.getTitle());
        lesson.setDescription(entity.getDescription());
        lesson.setType(LessonType.valueOf(entity.getType()));
        lesson.setLevel(LessonLevel.valueOf(entity.getLevel()));
        lesson.setContent(entity.getContent());
        lesson.setOrderIndex(entity.getOrderIndex());
        lesson.setPublished(entity.isPublished());
        lesson.setCreatedAt(entity.getCreatedAt());
        lesson.setUpdatedAt(entity.getUpdatedAt());
        return lesson;
    }

    public LessonEntity toEntity(Lesson lesson) {
        if (lesson == null) return null;
        LessonEntity entity = new LessonEntity();
        entity.setId(lesson.getId());
        entity.setTitle(lesson.getTitle());
        entity.setDescription(lesson.getDescription());
        entity.setType(lesson.getType().name());
        entity.setLevel(lesson.getLevel().name());
        entity.setContent(lesson.getContent());
        entity.setOrderIndex(lesson.getOrderIndex());
        entity.setPublished(lesson.isPublished());
        entity.setCreatedAt(lesson.getCreatedAt());
        entity.setUpdatedAt(lesson.getUpdatedAt());
        return entity;
    }
}
