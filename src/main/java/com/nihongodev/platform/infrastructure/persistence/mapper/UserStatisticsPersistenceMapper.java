package com.nihongodev.platform.infrastructure.persistence.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nihongodev.platform.domain.model.*;
import com.nihongodev.platform.infrastructure.persistence.entity.UserStatisticsEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserStatisticsPersistenceMapper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public UserStatistics toDomain(UserStatisticsEntity entity) {
        if (entity == null) return null;
        UserStatistics s = new UserStatistics();
        s.setId(entity.getId());
        s.setUserId(entity.getUserId());
        s.setAverageScore7Days(entity.getAverageScore7Days());
        s.setAverageScore30Days(entity.getAverageScore30Days());
        s.setAverageScoreAllTime(entity.getAverageScoreAllTime());
        s.setLearningVelocity(entity.getLearningVelocity());
        s.setConsistencyRate(entity.getConsistencyRate());
        s.setWeakAreas(deserializeWeakAreas(entity.getWeakAreas()));
        s.setRecommendations(deserializeRecommendations(entity.getRecommendations()));
        s.setProgressTrend(entity.getProgressTrend() != null ? Trend.valueOf(entity.getProgressTrend()) : Trend.STABLE);
        s.setLastCalculatedAt(entity.getLastCalculatedAt());
        return s;
    }

    public UserStatisticsEntity toEntity(UserStatistics s) {
        if (s == null) return null;
        UserStatisticsEntity entity = new UserStatisticsEntity();
        entity.setId(s.getId());
        entity.setUserId(s.getUserId());
        entity.setAverageScore7Days(s.getAverageScore7Days());
        entity.setAverageScore30Days(s.getAverageScore30Days());
        entity.setAverageScoreAllTime(s.getAverageScoreAllTime());
        entity.setLearningVelocity(s.getLearningVelocity());
        entity.setConsistencyRate(s.getConsistencyRate());
        entity.setWeakAreas(serializeWeakAreas(s.getWeakAreas()));
        entity.setRecommendations(serializeRecommendations(s.getRecommendations()));
        entity.setProgressTrend(s.getProgressTrend() != null ? s.getProgressTrend().name() : "STABLE");
        entity.setLastCalculatedAt(s.getLastCalculatedAt());
        return entity;
    }

    private String serializeWeakAreas(List<WeakArea> weakAreas) {
        try { return objectMapper.writeValueAsString(weakAreas != null ? weakAreas : List.of()); }
        catch (Exception e) { return "[]"; }
    }

    private List<WeakArea> deserializeWeakAreas(String json) {
        try { return objectMapper.readValue(json != null ? json : "[]", new TypeReference<>() {}); }
        catch (Exception e) { return new ArrayList<>(); }
    }

    private String serializeRecommendations(List<Recommendation> recommendations) {
        try { return objectMapper.writeValueAsString(recommendations != null ? recommendations : List.of()); }
        catch (Exception e) { return "[]"; }
    }

    private List<Recommendation> deserializeRecommendations(String json) {
        try { return objectMapper.readValue(json != null ? json : "[]", new TypeReference<>() {}); }
        catch (Exception e) { return new ArrayList<>(); }
    }
}
