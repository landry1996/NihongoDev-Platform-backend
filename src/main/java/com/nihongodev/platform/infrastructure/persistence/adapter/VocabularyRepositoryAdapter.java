package com.nihongodev.platform.infrastructure.persistence.adapter;

import com.nihongodev.platform.application.port.out.VocabularyRepositoryPort;
import com.nihongodev.platform.domain.model.Vocabulary;
import com.nihongodev.platform.domain.model.VocabularyCategory;
import com.nihongodev.platform.domain.model.VocabularyLevel;
import com.nihongodev.platform.infrastructure.persistence.mapper.VocabularyPersistenceMapper;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaVocabularyRepository;
import com.nihongodev.platform.infrastructure.persistence.specification.VocabularySpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class VocabularyRepositoryAdapter implements VocabularyRepositoryPort {

    private final JpaVocabularyRepository jpaRepository;
    private final VocabularyPersistenceMapper mapper;

    public VocabularyRepositoryAdapter(JpaVocabularyRepository jpaRepository, VocabularyPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Vocabulary save(Vocabulary vocabulary) {
        var entity = mapper.toEntity(vocabulary);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<Vocabulary> saveAll(List<Vocabulary> vocabularies) {
        var entities = vocabularies.stream().map(mapper::toEntity).toList();
        var saved = jpaRepository.saveAll(entities);
        return saved.stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Vocabulary> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Vocabulary> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Vocabulary> findByCategory(VocabularyCategory category) {
        return jpaRepository.findByCategory(category.name()).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Vocabulary> findByLevel(VocabularyLevel level) {
        return jpaRepository.findByLevel(level.name()).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Vocabulary> findByCategoryAndLevel(VocabularyCategory category, VocabularyLevel level) {
        return jpaRepository.findByCategoryAndLevel(category.name(), level.name()).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Vocabulary> findByLessonId(UUID lessonId) {
        return jpaRepository.findByLessonId(lessonId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Vocabulary> search(String query, VocabularyCategory category, VocabularyLevel level, UUID lessonId, String tag) {
        Specification<com.nihongodev.platform.infrastructure.persistence.entity.VocabularyEntity> spec =
                Specification.where(VocabularySpecification.textSearch(query))
                        .and(VocabularySpecification.hasCategory(category != null ? category.name() : null))
                        .and(VocabularySpecification.hasLevel(level != null ? level.name() : null))
                        .and(VocabularySpecification.hasLessonId(lessonId))
                        .and(VocabularySpecification.hasTag(tag));

        return jpaRepository.findAll(spec).stream().map(mapper::toDomain).toList();
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
