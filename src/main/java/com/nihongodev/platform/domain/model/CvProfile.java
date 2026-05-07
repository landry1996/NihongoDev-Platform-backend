package com.nihongodev.platform.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CvProfile {

    private UUID id;
    private UUID userId;
    private String fullName;
    private String currentRole;
    private String targetRole;
    private int yearsOfExperience;
    private TargetCompanyType targetCompanyType;
    private List<String> techStack;
    private List<WorkExperience> experiences;
    private List<String> certifications;
    private List<String> notableProjects;
    private String motivationJapan;
    private String japaneseLevel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CvProfile() {}

    public static CvProfile create(UUID userId, String fullName, String targetRole,
                                   int yearsOfExperience, TargetCompanyType targetCompanyType) {
        CvProfile profile = new CvProfile();
        profile.id = UUID.randomUUID();
        profile.userId = userId;
        profile.fullName = fullName;
        profile.targetRole = targetRole;
        profile.yearsOfExperience = yearsOfExperience;
        profile.targetCompanyType = targetCompanyType;
        profile.techStack = new ArrayList<>();
        profile.experiences = new ArrayList<>();
        profile.certifications = new ArrayList<>();
        profile.notableProjects = new ArrayList<>();
        profile.createdAt = LocalDateTime.now();
        profile.updatedAt = LocalDateTime.now();
        return profile;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getCurrentRole() { return currentRole; }
    public void setCurrentRole(String currentRole) { this.currentRole = currentRole; }
    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }
    public int getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(int yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }
    public TargetCompanyType getTargetCompanyType() { return targetCompanyType; }
    public void setTargetCompanyType(TargetCompanyType targetCompanyType) { this.targetCompanyType = targetCompanyType; }
    public List<String> getTechStack() { return techStack; }
    public void setTechStack(List<String> techStack) { this.techStack = techStack; }
    public List<WorkExperience> getExperiences() { return experiences; }
    public void setExperiences(List<WorkExperience> experiences) { this.experiences = experiences; }
    public List<String> getCertifications() { return certifications; }
    public void setCertifications(List<String> certifications) { this.certifications = certifications; }
    public List<String> getNotableProjects() { return notableProjects; }
    public void setNotableProjects(List<String> notableProjects) { this.notableProjects = notableProjects; }
    public String getMotivationJapan() { return motivationJapan; }
    public void setMotivationJapan(String motivationJapan) { this.motivationJapan = motivationJapan; }
    public String getJapaneseLevel() { return japaneseLevel; }
    public void setJapaneseLevel(String japaneseLevel) { this.japaneseLevel = japaneseLevel; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
