package com.nihongodev.platform.infrastructure.persistence.adapter;

import com.nihongodev.platform.application.port.out.CorrectionRuleRepositoryPort;
import com.nihongodev.platform.domain.model.CorrectionRule;
import com.nihongodev.platform.infrastructure.persistence.mapper.CorrectionRulePersistenceMapper;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaCorrectionRuleRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CorrectionRuleRepositoryAdapter implements CorrectionRuleRepositoryPort {

    private final JpaCorrectionRuleRepository repository;
    private final CorrectionRulePersistenceMapper mapper;

    public CorrectionRuleRepositoryAdapter(JpaCorrectionRuleRepository repository,
                                           CorrectionRulePersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<CorrectionRule> findAllActive() {
        return repository.findByActiveTrue().stream()
                .map(mapper::toDomain)
                .toList();
    }
}
