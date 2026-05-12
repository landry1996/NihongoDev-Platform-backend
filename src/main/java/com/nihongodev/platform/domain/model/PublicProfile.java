package com.nihongodev.platform.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PublicProfile {

    private UUID id;
    private UUID userId;
    private String displayName;
    private String slug;
    private String bio;
    private String avatarUrl;
    private JapaneseLevel currentLevel;
    private ProfileVisibility visibility;
    private boolean openToWork;
    private String preferredRole;
    private String location;
    private int totalXp;
    private int totalBadges;
    private int lessonsCompleted;
    private int readingSessionsCompleted;
    private double averageScore;
    private List<String> highlightedSkills;
    private List<UUID> showcasedBadgeIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PublicProfile() {
        this.highlightedSkills = new ArrayList<>();
        this.showcasedBadgeIds = new ArrayList<>();
    }

    public static PublicProfile create(UUID userId, String displayName, String slug) {
        PublicProfile profile = new PublicProfile();
        profile.id = UUID.randomUUID();
        profile.userId = userId;
        profile.displayName = displayName;
        profile.slug = slug;
        profile.visibility = ProfileVisibility.PUBLIC;
        profile.openToWork = false;
        profile.totalXp = 0;
        profile.totalBadges = 0;
        profile.lessonsCompleted = 0;
        profile.readingSessionsCompleted = 0;
        profile.averageScore = 0;
        profile.createdAt = LocalDateTime.now();
        profile.updatedAt = LocalDateTime.now();
        return profile;
    }

    public void updateStats(int totalXp, int totalBadges, int lessonsCompleted,
                            int readingSessionsCompleted, double averageScore) {
        this.totalXp = totalXp;
        this.totalBadges = totalBadges;
        this.lessonsCompleted = lessonsCompleted;
        this.readingSessionsCompleted = readingSessionsCompleted;
        this.averageScore = averageScore;
        this.updatedAt = LocalDateTime.now();
    }

    public void toggleOpenToWork() {
        this.openToWork = !this.openToWork;
        this.updatedAt = LocalDateTime.now();
    }

    public void showcaseBadge(UUID badgeId) {
        if (!this.showcasedBadgeIds.contains(badgeId) && this.showcasedBadgeIds.size() < 6) {
            this.showcasedBadgeIds.add(badgeId);
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void removeBadgeShowcase(UUID badgeId) {
        this.showcasedBadgeIds.remove(badgeId);
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isVisibleTo(boolean isRecruiter) {
        return switch (visibility) {
            case PUBLIC -> true;
            case RECRUITERS_ONLY -> isRecruiter;
            case PRIVATE -> false;
        };
    }

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
    public JapaneseLevel getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(JapaneseLevel currentLevel) { this.currentLevel = currentLevel; }
    public ProfileVisibility getVisibility() { return visibility; }
    public void setVisibility(ProfileVisibility visibility) { this.visibility = visibility; }
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
    public List<UUID> getShowcasedBadgeIds() { return showcasedBadgeIds; }
    public void setShowcasedBadgeIds(List<UUID> showcasedBadgeIds) { this.showcasedBadgeIds = showcasedBadgeIds; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
