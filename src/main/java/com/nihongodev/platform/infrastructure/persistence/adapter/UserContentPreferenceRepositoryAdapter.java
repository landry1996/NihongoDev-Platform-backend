package com.nihongodev.platform.infrastructure.persistence.adapter;

import com.nihongodev.platform.application.port.out.UserContentPreferenceRepositoryPort;
import com.nihongodev.platform.domain.model.UserContentPreference;
import com.nihongodev.platform.infrastructure.persistence.mapper.UserContentPreferencePersistenceMapper;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaUserContentPreferenceRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UserContentPreferenceRepositoryAdapter implements UserContentPreferenceRepositoryPort {

    private final JpaUserContentPreferenceRepository jpaRepository;
    private final UserContentPreferencePersistenceMapper mapper;

    public UserContentPreferenceRepositoryAdapter(JpaUserContentPreferenceRepository jpaRepository,
                                                   UserContentPreferencePersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public UserContentPreference save(UserContentPreference preference) {
        var existing = jpaRepository.findByUserId(preference.userId());
        var entity = mapper.toEntity(preference);
        existing.ifPresent(e -> entity.setId(e.getId()));
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<UserContentPreference> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).map(mapper::toDomain);
    }
}
