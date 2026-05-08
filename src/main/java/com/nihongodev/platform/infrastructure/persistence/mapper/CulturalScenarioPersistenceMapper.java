package com.nihongodev.platform.infrastructure.persistence.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nihongodev.platform.domain.model.*;
import com.nihongodev.platform.infrastructure.persistence.entity.CulturalScenarioEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CulturalScenarioPersistenceMapper {

    private final ObjectMapper objectMapper;

    public CulturalScenarioPersistenceMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public CulturalScenario toDomain(CulturalScenarioEntity entity) {
        if (entity == null) return null;
        CulturalScenario scenario = new CulturalScenario();
        scenario.setId(entity.getId());
        scenario.setTitle(entity.getTitle());
        scenario.setTitleJp(entity.getTitleJp());
        scenario.setSituation(entity.getSituation());
        scenario.setSituationJp(entity.getSituationJp());
        scenario.setContext(entity.getContext() != null ? WorkplaceContext.valueOf(entity.getContext()) : null);
        scenario.setRelationship(entity.getRelationship() != null ? RelationshipType.valueOf(entity.getRelationship()) : null);
        scenario.setMode(entity.getMode() != null ? ScenarioMode.valueOf(entity.getMode()) : null);
        scenario.setCategory(entity.getCategory() != null ? ScenarioCategory.valueOf(entity.getCategory()) : null);
        scenario.setExpectedKeigoLevel(entity.getExpectedKeigoLevel() != null ? KeigoLevel.valueOf(entity.getExpectedKeigoLevel()) : null);
        scenario.setDifficulty(entity.getDifficulty() != null ? JapaneseLevel.valueOf(entity.getDifficulty()) : null);
        scenario.setChoices(deserializeChoices(entity.getChoices()));
        scenario.setModelAnswer(entity.getModelAnswer());
        scenario.setModelAnswerExplanation(entity.getModelAnswerExplanation());
        scenario.setKeyPhrases(deserializeStringList(entity.getKeyPhrases()));
        scenario.setAvoidPhrases(deserializeStringList(entity.getAvoidPhrases()));
        scenario.setCulturalNote(entity.getCulturalNote());
        scenario.setXpReward(entity.getXpReward());
        scenario.setPublished(entity.isPublished());
        scenario.setCreatedAt(entity.getCreatedAt());
        return scenario;
    }

    public CulturalScenarioEntity toEntity(CulturalScenario domain) {
        if (domain == null) return null;
        CulturalScenarioEntity entity = new CulturalScenarioEntity();
        entity.setId(domain.getId());
        entity.setTitle(domain.getTitle());
        entity.setTitleJp(domain.getTitleJp());
        entity.setSituation(domain.getSituation());
        entity.setSituationJp(domain.getSituationJp());
        entity.setContext(domain.getContext() != null ? domain.getContext().name() : null);
        entity.setRelationship(domain.getRelationship() != null ? domain.getRelationship().name() : null);
        entity.setMode(domain.getMode() != null ? domain.getMode().name() : null);
        entity.setCategory(domain.getCategory() != null ? domain.getCategory().name() : null);
        entity.setExpectedKeigoLevel(domain.getExpectedKeigoLevel() != null ? domain.getExpectedKeigoLevel().name() : null);
        entity.setDifficulty(domain.getDifficulty() != null ? domain.getDifficulty().name() : null);
        entity.setChoices(serializeChoices(domain.getChoices()));
        entity.setModelAnswer(domain.getModelAnswer());
        entity.setModelAnswerExplanation(domain.getModelAnswerExplanation());
        entity.setKeyPhrases(serializeStringList(domain.getKeyPhrases()));
        entity.setAvoidPhrases(serializeStringList(domain.getAvoidPhrases()));
        entity.setCulturalNote(domain.getCulturalNote());
        entity.setXpReward(domain.getXpReward());
        entity.setPublished(domain.isPublished());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }

    private String serializeChoices(List<ScenarioChoice> choices) {
        try { return objectMapper.writeValueAsString(choices != null ? choices : List.of()); }
        catch (Exception e) { return "[]"; }
    }

    private List<ScenarioChoice> deserializeChoices(String json) {
        try { return objectMapper.readValue(json != null ? json : "[]", new TypeReference<List<ScenarioChoice>>() {}); }
        catch (Exception e) { return new ArrayList<>(); }
    }

    private String serializeStringList(List<String> list) {
        try { return objectMapper.writeValueAsString(list != null ? list : List.of()); }
        catch (Exception e) { return "[]"; }
    }

    private List<String> deserializeStringList(String json) {
        try { return objectMapper.readValue(json != null ? json : "[]", new TypeReference<List<String>>() {}); }
        catch (Exception e) { return new ArrayList<>(); }
    }
}
