package com.nihongodev.platform.application.port.out;

import com.nihongodev.platform.domain.model.CorrectionRule;

import java.util.List;

public interface CorrectionRuleRepositoryPort {
    List<CorrectionRule> findAllActive();
}
