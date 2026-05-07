package com.nihongodev.platform.infrastructure.persistence.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nihongodev.platform.domain.model.CvProfile;
import com.nihongodev.platform.domain.model.TargetCompanyType;
import com.nihongodev.platform.domain.model.WorkExperience;
import com.nihongodev.platform.infrastructure.persistence.entity.CvProfileEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CvProfilePersistenceMapper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public CvProfile toDomain(CvProfileEntity entity) {
        if (entity == null) return null;
        CvProfile p = new CvProfile();
        p.setId(entity.getId());
        p.setUserId(entity.getUserId());
        p.setFullName(entity.getFullName());
        p.setCurrentRole(entity.getCurrentRole());
        p.setTargetRole(entity.getTargetRole());
        p.setYearsOfExperience(entity.getYearsOfExperience());
        p.setTargetCompanyType(TargetCompanyType.valueOf(entity.getTargetCompanyType()));
        p.setTechStack(deserializeStringList(entity.getTechStack()));
        p.setExperiences(deserializeExperiences(entity.getExperiences()));
        p.setCertifications(deserializeStringList(entity.getCertifications()));
        p.setNotableProjects(deserializeStringList(entity.getNotableProjects()));
        p.setMotivationJapan(entity.getMotivationJapan());
        p.setJapaneseLevel(entity.getJapaneseLevel());
        p.setCreatedAt(entity.getCreatedAt());
        p.setUpdatedAt(entity.getUpdatedAt());
        return p;
    }

    public CvProfileEntity toEntity(CvProfile p) {
        if (p == null) return null;
        CvProfileEntity entity = new CvProfileEntity();
        entity.setId(p.getId());
        entity.setUserId(p.getUserId());
        entity.setFullName(p.getFullName());
        entity.setCurrentRole(p.getCurrentRole());
        entity.setTargetRole(p.getTargetRole());
        entity.setYearsOfExperience(p.getYearsOfExperience());
        entity.setTargetCompanyType(p.getTargetCompanyType().name());
        entity.setTechStack(serializeList(p.getTechStack()));
        entity.setExperiences(serializeList(p.getExperiences()));
        entity.setCertifications(serializeList(p.getCertifications()));
        entity.setNotableProjects(serializeList(p.getNotableProjects()));
        entity.setMotivationJapan(p.getMotivationJapan());
        entity.setJapaneseLevel(p.getJapaneseLevel());
        entity.setCreatedAt(p.getCreatedAt());
        entity.setUpdatedAt(p.getUpdatedAt());
        return entity;
    }

    private String serializeList(Object list) {
        try { return objectMapper.writeValueAsString(list != null ? list : List.of()); }
        catch (Exception e) { return "[]"; }
    }

    private List<String> deserializeStringList(String json) {
        try { return objectMapper.readValue(json != null ? json : "[]", new TypeReference<>() {}); }
        catch (Exception e) { return new ArrayList<>(); }
    }

    private List<WorkExperience> deserializeExperiences(String json) {
        try { return objectMapper.readValue(json != null ? json : "[]", new TypeReference<>() {}); }
        catch (Exception e) { return new ArrayList<>(); }
    }
}
