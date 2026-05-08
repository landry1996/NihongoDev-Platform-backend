package com.nihongodev.platform.infrastructure.persistence.adapter;

import com.nihongodev.platform.application.port.out.ScenarioRepositoryPort;
import com.nihongodev.platform.domain.model.*;
import com.nihongodev.platform.infrastructure.persistence.mapper.CulturalScenarioPersistenceMapper;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaCulturalScenarioRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ScenarioRepositoryAdapter implements ScenarioRepositoryPort {

    private final JpaCulturalScenarioRepository repository;
    private final CulturalScenarioPersistenceMapper mapper;

    public ScenarioRepositoryAdapter(JpaCulturalScenarioRepository repository,
                                     CulturalScenarioPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public CulturalScenario save(CulturalScenario scenario) {
        var entity = mapper.toEntity(scenario);
        var saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<CulturalScenario> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<CulturalScenario> findPublished(WorkplaceContext context, JapaneseLevel difficulty, ScenarioCategory category) {
        String contextStr = context != null ? context.name() : null;
        String difficultyStr = difficulty != null ? difficulty.name() : null;
        String categoryStr = category != null ? category.name() : null;
        return repository.findPublished(contextStr, difficultyStr, categoryStr).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
