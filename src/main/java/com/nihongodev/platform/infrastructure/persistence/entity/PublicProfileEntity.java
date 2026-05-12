package com.nihongodev.platform.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "public_profiles")
public class PublicProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(nullable = false, unique = true)
    private String slug;

    private String bio;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "current_level")
    private String currentLevel;

    @Column(nullable = false)
    private String visibility;

    @Column(name = "open_to_work", nullable = false)
    private boolean openToWork;

    @Column(name = "preferred_role")
    private String preferredRole;

    private String location;

    @Column(name = "total_xp")
    private int totalXp;

    @Column(name = "total_badges")
    private int totalBadges;

    @Column(name = "lessons_completed")
    private int lessonsCompleted;

    @Column(name = "reading_sessions_completed")
    private int readingSessionsCompleted;

    @Column(name = "average_score")
    private double averageScore;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "highlighted_skills", columnDefinition = "jsonb")
    private List<String> highlightedSkills;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "showcased_badge_ids", columnDefinition = "jsonb")
    private List<String> showcasedBadgeIds;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public String getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(String currentLevel) { this.currentLevel = currentLevel; }
    public String getVisibility() { return visibility; }
    public void setVisibility(String visibility) { this.visibility = visibility; }
    public boolean isOpenToWork() { return openToWork; }
    public void setOpenToWork(boolean openToWork) { this.openToWork = openToWork; }
    public String getPreferredRole() { return preferredRole; }
    public void setPreferredRole(String preferredRole) { this.preferredRole = preferredRole; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public int getTotalXp() { return totalXp; }
    public void setTotalXp(int totalXp) { this.totalXp = totalXp; }
    public int getTotalBadges() { return totalBadges; }
    public void setTotalBadges(int totalBadges) { this.totalBadges = totalBadges; }
    public int getLessonsCompleted() { return lessonsCompleted; }
    public void setLessonsCompleted(int lessonsCompleted) { this.lessonsCompleted = lessonsCompleted; }
    public int getReadingSessionsCompleted() { return readingSessionsCompleted; }
    public void setReadingSessionsCompleted(int readingSessionsCompleted) { this.readingSessionsCompleted = readingSessionsCompleted; }
    public double getAverageScore() { return averageScore; }
    public void setAverageScore(double averageScore) { this.averageScore = averageScore; }
    public List<String> getHighlightedSkills() { return highlightedSkills; }
    public void setHighlightedSkills(List<String> highlightedSkills) { this.highlightedSkills = highlightedSkills; }
    public List<String> getShowcasedBadgeIds() { return showcasedBadgeIds; }
    public void setShowcasedBadgeIds(List<String> showcasedBadgeIds) { this.showcasedBadgeIds = showcasedBadgeIds; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
