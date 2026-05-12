package com.nihongodev.platform.infrastructure.persistence.adapter;

import com.nihongodev.platform.application.port.out.UserBadgeRepositoryPort;
import com.nihongodev.platform.domain.model.UserBadge;
import com.nihongodev.platform.infrastructure.persistence.mapper.UserBadgePersistenceMapper;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaUserBadgeRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserBadgeRepositoryAdapter implements UserBadgeRepositoryPort {

    private final JpaUserBadgeRepository jpaRepository;
    private final UserBadgePersistenceMapper mapper;

    public UserBadgeRepositoryAdapter(JpaUserBadgeRepository jpaRepository,
                                       UserBadgePersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public UserBadge save(UserBadge userBadge) {
        var entity = mapper.toEntity(userBadge);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<UserBadge> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).stream()
            .map(mapper::toDomain).toList();
    }

    @Override
    public Optional<UserBadge> findByUserIdAndBadgeId(UUID userId, UUID badgeId) {
        return jpaRepository.findByUserIdAndBadgeId(userId, badgeId).map(mapper::toDomain);
    }

    @Override
    public boolean existsByUserIdAndBadgeId(UUID userId, UUID badgeId) {
        return jpaRepository.existsByUserIdAndBadgeId(userId, badgeId);
    }

    @Override
    public int countByUserId(UUID userId) {
        return jpaRepository.countByUserId(userId);
    }
}
