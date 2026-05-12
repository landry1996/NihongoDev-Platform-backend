package com.nihongodev.platform.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserBadge {

    private UUID id;
    private UUID userId;
    private UUID badgeId;
    private LocalDateTime earnedAt;
    private boolean showcased;

    public static UserBadge award(UUID userId, UUID badgeId) {
        UserBadge ub = new UserBadge();
        ub.id = UUID.randomUUID();
        ub.userId = userId;
        ub.badgeId = badgeId;
        ub.earnedAt = LocalDateTime.now();
        ub.showcased = false;
        return ub;
    }

    public void showcase() {
        this.showcased = true;
    }

    public void unshowcase() {
        this.showcased = false;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public UUID getBadgeId() { return badgeId; }
    public void setBadgeId(UUID badgeId) { this.badgeId = badgeId; }
    public LocalDateTime getEarnedAt() { return earnedAt; }
    public void setEarnedAt(LocalDateTime earnedAt) { this.earnedAt = earnedAt; }
    public boolean isShowcased() { return showcased; }
    public void setShowcased(boolean showcased) { this.showcased = showcased; }
}
