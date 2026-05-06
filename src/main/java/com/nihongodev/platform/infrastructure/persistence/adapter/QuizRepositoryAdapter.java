package com.nihongodev.platform.infrastructure.persistence.adapter;

import com.nihongodev.platform.application.port.out.QuizRepositoryPort;
import com.nihongodev.platform.domain.model.Quiz;
import com.nihongodev.platform.infrastructure.persistence.mapper.QuizPersistenceMapper;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaQuizRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class QuizRepositoryAdapter implements QuizRepositoryPort {

    private final JpaQuizRepository jpaRepository;
    private final QuizPersistenceMapper mapper;

    public QuizRepositoryAdapter(JpaQuizRepository jpaRepository, QuizPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Quiz save(Quiz quiz) {
        var entity = mapper.toEntity(quiz);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Quiz> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Quiz> findByLessonId(UUID lessonId) {
        return jpaRepository.findByLessonId(lessonId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Quiz> findPublished() {
        return jpaRepository.findByPublishedTrue().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }
}
