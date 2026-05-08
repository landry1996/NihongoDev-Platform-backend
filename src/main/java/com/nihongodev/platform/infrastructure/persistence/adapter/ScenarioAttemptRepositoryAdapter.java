package com.nihongodev.platform.infrastructure.persistence.adapter;

import com.nihongodev.platform.application.port.out.ScenarioAttemptRepositoryPort;
import com.nihongodev.platform.domain.model.ScenarioAttempt;
import com.nihongodev.platform.infrastructure.persistence.mapper.ScenarioAttemptPersistenceMapper;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaScenarioAttemptRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ScenarioAttemptRepositoryAdapter implements ScenarioAttemptRepositoryPort {

    private final JpaScenarioAttemptRepository repository;
    private final ScenarioAttemptPersistenceMapper mapper;

    public ScenarioAttemptRepositoryAdapter(JpaScenarioAttemptRepository repository,
                                            ScenarioAttemptPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public ScenarioAttempt save(ScenarioAttempt attempt) {
        var entity = mapper.toEntity(attempt);
        var saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<ScenarioAttempt> findByUserId(UUID userId) {
        return repository.findByUserId(userId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<ScenarioAttempt> findByUserIdAndScenarioId(UUID userId, UUID scenarioId) {
        return repository.findByUserIdAndScenarioId(userId, scenarioId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
