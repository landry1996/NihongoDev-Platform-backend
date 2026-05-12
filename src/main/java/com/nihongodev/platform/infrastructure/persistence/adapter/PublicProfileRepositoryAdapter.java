package com.nihongodev.platform.infrastructure.persistence.adapter;

import com.nihongodev.platform.application.port.out.PublicProfileRepositoryPort;
import com.nihongodev.platform.domain.model.JapaneseLevel;
import com.nihongodev.platform.domain.model.PublicProfile;
import com.nihongodev.platform.infrastructure.persistence.mapper.PublicProfilePersistenceMapper;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaPublicProfileRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.nihongodev.platform.infrastructure.persistence.entity.PublicProfileEntity;

@Repository
public class PublicProfileRepositoryAdapter implements PublicProfileRepositoryPort {

    private final JpaPublicProfileRepository jpaRepository;
    private final PublicProfilePersistenceMapper mapper;
    private final EntityManager entityManager;

    public PublicProfileRepositoryAdapter(JpaPublicProfileRepository jpaRepository,
                                           PublicProfilePersistenceMapper mapper,
                                           EntityManager entityManager) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
        this.entityManager = entityManager;
    }

    @Override
    public PublicProfile save(PublicProfile profile) {
        var entity = mapper.toEntity(profile);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<PublicProfile> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).map(mapper::toDomain);
    }

    @Override
    public Optional<PublicProfile> findBySlug(String slug) {
        return jpaRepository.findBySlug(slug).map(mapper::toDomain);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return jpaRepository.existsBySlug(slug);
    }

    @Override
    public List<PublicProfile> searchProfiles(JapaneseLevel minLevel, String skill,
                                              Boolean openToWork, int offset, int limit) {
        StringBuilder jpql = new StringBuilder(
            "SELECT p FROM PublicProfileEntity p WHERE p.visibility != 'PRIVATE'");
        List<String> levels = getLevelsAbove(minLevel);
        if (minLevel != null) {
            jpql.append(" AND p.currentLevel IN :levels");
        }
        if (openToWork != null) {
            jpql.append(" AND p.openToWork = :openToWork");
        }
        jpql.append(" ORDER BY p.totalXp DESC");

        TypedQuery<PublicProfileEntity> query = entityManager.createQuery(
            jpql.toString(), PublicProfileEntity.class);
        if (minLevel != null) {
            query.setParameter("levels", levels);
        }
        if (openToWork != null) {
            query.setParameter("openToWork", openToWork);
        }
        query.setFirstResult(offset);
        query.setMaxResults(limit);

        return query.getResultList().stream().map(mapper::toDomain).toList();
    }

    @Override
    public int countSearchResults(JapaneseLevel minLevel, String skill, Boolean openToWork) {
        StringBuilder jpql = new StringBuilder(
            "SELECT COUNT(p) FROM PublicProfileEntity p WHERE p.visibility != 'PRIVATE'");
        List<String> levels = getLevelsAbove(minLevel);
        if (minLevel != null) {
            jpql.append(" AND p.currentLevel IN :levels");
        }
        if (openToWork != null) {
            jpql.append(" AND p.openToWork = :openToWork");
        }

        TypedQuery<Long> query = entityManager.createQuery(jpql.toString(), Long.class);
        if (minLevel != null) {
            query.setParameter("levels", levels);
        }
        if (openToWork != null) {
            query.setParameter("openToWork", openToWork);
        }

        return query.getSingleResult().intValue();
    }

    private List<String> getLevelsAbove(JapaneseLevel minLevel) {
        if (minLevel == null) return List.of();
        JapaneseLevel[] all = JapaneseLevel.values();
        int minOrdinal = minLevel.ordinal();
        List<String> result = new ArrayList<>();
        for (JapaneseLevel level : all) {
            if (level.ordinal() >= minOrdinal) {
                result.add(level.name());
            }
        }
        return result;
    }
}
