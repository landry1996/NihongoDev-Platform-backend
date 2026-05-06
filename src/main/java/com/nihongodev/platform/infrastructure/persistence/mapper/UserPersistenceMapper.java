package com.nihongodev.platform.infrastructure.persistence.mapper;

import com.nihongodev.platform.domain.model.JapaneseLevel;
import com.nihongodev.platform.domain.model.Role;
import com.nihongodev.platform.domain.model.User;
import com.nihongodev.platform.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserPersistenceMapper {

    public User toDomain(UserEntity entity) {
        if (entity == null) return null;
        User user = new User();
        user.setId(entity.getId());
        user.setFirstName(entity.getFirstName());
        user.setLastName(entity.getLastName());
        user.setEmail(entity.getEmail());
        user.setPassword(entity.getPassword());
        user.setRole(Role.valueOf(entity.getRole()));
        user.setJapaneseLevel(entity.getJapaneseLevel() != null ? JapaneseLevel.valueOf(entity.getJapaneseLevel()) : JapaneseLevel.BEGINNER);
        user.setObjective(entity.getObjective());
        user.setAvatarUrl(entity.getAvatarUrl());
        user.setActive(entity.isActive());
        user.setCreatedAt(entity.getCreatedAt());
        user.setUpdatedAt(entity.getUpdatedAt());
        return user;
    }

    public UserEntity toEntity(User user) {
        if (user == null) return null;
        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setFirstName(user.getFirstName());
        entity.setLastName(user.getLastName());
        entity.setEmail(user.getEmail());
        entity.setPassword(user.getPassword());
        entity.setRole(user.getRole().name());
        entity.setJapaneseLevel(user.getJapaneseLevel() != null ? user.getJapaneseLevel().name() : null);
        entity.setObjective(user.getObjective());
        entity.setAvatarUrl(user.getAvatarUrl());
        entity.setActive(user.isActive());
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());
        return entity;
    }
}
