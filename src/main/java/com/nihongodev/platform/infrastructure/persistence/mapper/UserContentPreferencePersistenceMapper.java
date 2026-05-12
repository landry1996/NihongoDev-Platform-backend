package com.nihongodev.platform.infrastructure.persistence.mapper;

import com.nihongodev.platform.domain.model.*;
import com.nihongodev.platform.infrastructure.persistence.entity.UserContentPreferenceEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserContentPreferencePersistenceMapper {

    public UserContentPreference toDomain(UserContentPreferenceEntity entity) {
        List<ContentDomain> domains = entity.getPreferredDomains() != null
            ? entity.getPreferredDomains().stream().map(ContentDomain::valueOf).toList()
            : List.of();

        List<ContentSource> sources = entity.getPreferredSources() != null
            ? entity.getPreferredSources().stream().map(ContentSource::valueOf).toList()
            : List.of();

        return new UserContentPreference(
            entity.getUserId(),
            domains,
            entity.getCurrentLevel() != null ? JapaneseLevel.valueOf(entity.getCurrentLevel()) : JapaneseLevel.N3,
            entity.getMaxDifficulty() != null ? ReadingDifficulty.valueOf(entity.getMaxDifficulty()) : ReadingDifficulty.INTERMEDIATE,
            entity.getPreferredReadingMinutes(),
            sources
        );
    }

    public UserContentPreferenceEntity toEntity(UserContentPreference preference) {
        UserContentPreferenceEntity entity = new UserContentPreferenceEntity();
        entity.setUserId(preference.userId());
        entity.setPreferredDomains(preference.preferredDomains().stream().map(Enum::name).toList());
        entity.setCurrentLevel(preference.currentLevel().name());
        entity.setMaxDifficulty(preference.maxDifficulty().name());
        entity.setPreferredReadingMinutes(preference.preferredReadingMinutes());
        entity.setPreferredSources(preference.preferredSources().stream().map(Enum::name).toList());
        return entity;
    }
}
