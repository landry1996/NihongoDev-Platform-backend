package com.nihongodev.platform.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_badges")
public class UserBadgeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "badge_id", nullable = false)
    private UUID badgeId;

    @Column(name = "earned_at", nullable = false)
    private LocalDateTime earnedAt;

    @Column(nullable = false)
    private boolean showcased;

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
