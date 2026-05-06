package com.nihongodev.platform.infrastructure.persistence.mapper;

import com.nihongodev.platform.domain.model.*;
import com.nihongodev.platform.infrastructure.persistence.entity.CorrectionRuleEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class CorrectionRulePersistenceMapper {

    public CorrectionRule toDomain(CorrectionRuleEntity entity) {
        CorrectionRule rule = new CorrectionRule();
        rule.setId(entity.getId());
        rule.setName(entity.getName());
        rule.setCategory(AnnotationCategory.valueOf(entity.getCategory()));
        rule.setSeverity(Severity.valueOf(entity.getSeverity()));
        rule.setPattern(entity.getPattern());
        rule.setSuggestionTemplate(entity.getSuggestionTemplate());
        rule.setExplanationTemplate(entity.getExplanationTemplate());
        rule.setApplicableContexts(parseContexts(entity.getApplicableContexts()));
        rule.setMinLevel(entity.getMinLevel() != null ? JapaneseLevel.valueOf(entity.getMinLevel()) : null);
        rule.setActive(entity.isActive());
        return rule;
    }

    private List<TextType> parseContexts(String contexts) {
        if (contexts == null || contexts.isBlank()) return List.of();
        return Arrays.stream(contexts.split("\\|\\|"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(TextType::valueOf)
                .toList();
    }
}
