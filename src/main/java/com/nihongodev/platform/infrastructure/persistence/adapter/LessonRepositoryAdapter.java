package com.nihongodev.platform.infrastructure.persistence.adapter;

import com.nihongodev.platform.application.port.out.LessonRepositoryPort;
import com.nihongodev.platform.domain.model.Lesson;
import com.nihongodev.platform.domain.model.LessonLevel;
import com.nihongodev.platform.domain.model.LessonType;
import com.nihongodev.platform.infrastructure.persistence.mapper.LessonPersistenceMapper;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaLessonRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class LessonRepositoryAdapter implements LessonRepositoryPort {

    private final JpaLessonRepository jpaLessonRepository;
    private final LessonPersistenceMapper mapper;

    public LessonRepositoryAdapter(JpaLessonRepository jpaLessonRepository, LessonPersistenceMapper mapper) {
        this.jpaLessonRepository = jpaLessonRepository;
        this.mapper = mapper;
    }

    @Override
    public Lesson save(Lesson lesson) {
        var entity = mapper.toEntity(lesson);
        var saved = jpaLessonRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Lesson> findById(UUID id) {
        return jpaLessonRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Lesson> findAll() {
        return jpaLessonRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Lesson> findByType(LessonType type) {
        return jpaLessonRepository.findByType(type.name()).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Lesson> findByLevel(LessonLevel level) {
        return jpaLessonRepository.findByLevel(level.name()).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Lesson> findByTypeAndLevel(LessonType type, LessonLevel level) {
        return jpaLessonRepository.findByTypeAndLevel(type.name(), level.name()).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Lesson> findPublished() {
        return jpaLessonRepository.findByPublishedTrue().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaLessonRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaLessonRepository.existsById(id);
    }
}
