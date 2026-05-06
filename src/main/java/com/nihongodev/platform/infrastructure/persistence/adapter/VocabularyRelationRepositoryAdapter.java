package com.nihongodev.platform.infrastructure.persistence.adapter;

import com.nihongodev.platform.application.port.out.VocabularyRelationRepositoryPort;
import com.nihongodev.platform.domain.model.VocabularyRelation;
import com.nihongodev.platform.infrastructure.persistence.mapper.VocabularyRelationPersistenceMapper;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaVocabularyRelationRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class VocabularyRelationRepositoryAdapter implements VocabularyRelationRepositoryPort {

    private final JpaVocabularyRelationRepository jpaRepository;
    private final VocabularyRelationPersistenceMapper mapper;

    public VocabularyRelationRepositoryAdapter(JpaVocabularyRelationRepository jpaRepository,
                                               VocabularyRelationPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public VocabularyRelation save(VocabularyRelation relation) {
        var entity = mapper.toEntity(relation);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<VocabularyRelation> findBySourceId(UUID sourceId) {
        return jpaRepository.findBySourceVocabularyId(sourceId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<VocabularyRelation> findByTargetId(UUID targetId) {
        return jpaRepository.findByTargetVocabularyId(targetId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
