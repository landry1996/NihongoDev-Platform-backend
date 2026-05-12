package com.nihongodev.platform.infrastructure.persistence.mapper;

import com.nihongodev.platform.domain.model.UserBadge;
import com.nihongodev.platform.infrastructure.persistence.entity.UserBadgeEntity;
import org.springframework.stereotype.Component;

@Component
public class UserBadgePersistenceMapper {

    public UserBadge toDomain(UserBadgeEntity entity) {
        UserBadge userBadge = new UserBadge();
        userBadge.setId(entity.getId());
        userBadge.setUserId(entity.getUserId());
        userBadge.setBadgeId(entity.getBadgeId());
        userBadge.setEarnedAt(entity.getEarnedAt());
        userBadge.setShowcased(entity.isShowcased());
        return userBadge;
    }

    public UserBadgeEntity toEntity(UserBadge userBadge) {
        UserBadgeEntity entity = new UserBadgeEntity();
        entity.setId(userBadge.getId());
        entity.setUserId(userBadge.getUserId());
        entity.setBadgeId(userBadge.getBadgeId());
        entity.setEarnedAt(userBadge.getEarnedAt());
        entity.setShowcased(userBadge.isShowcased());
        return entity;
    }
}
