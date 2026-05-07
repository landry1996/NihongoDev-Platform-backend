package com.nihongodev.platform.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cv_profiles")
public class CvProfileEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    @Column(name = "current_role", length = 200)
    private String currentRole;

    @Column(name = "target_role", nullable = false, length = 200)
    private String targetRole;

    @Column(name = "years_of_experience", nullable = false)
    private int yearsOfExperience;

    @Column(name = "target_company_type", nullable = false, length = 50)
    private String targetCompanyType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tech_stack", nullable = false, columnDefinition = "jsonb")
    private String techStack;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "experiences", nullable = false, columnDefinition = "jsonb")
    private String experiences;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "certifications", nullable = false, columnDefinition = "jsonb")
    private String certifications;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "notable_projects", nullable = false, columnDefinition = "jsonb")
    private String notableProjects;

    @Column(name = "motivation_japan", columnDefinition = "TEXT")
    private String motivationJapan;

    @Column(name = "japanese_level", length = 10)
    private String japaneseLevel;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
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
    public String getTargetCompanyType() { return targetCompanyType; }
    public void setTargetCompanyType(String targetCompanyType) { this.targetCompanyType = targetCompanyType; }
    public String getTechStack() { return techStack; }
    public void setTechStack(String techStack) { this.techStack = techStack; }
    public String getExperiences() { return experiences; }
    public void setExperiences(String experiences) { this.experiences = experiences; }
    public String getCertifications() { return certifications; }
    public void setCertifications(String certifications) { this.certifications = certifications; }
    public String getNotableProjects() { return notableProjects; }
    public void setNotableProjects(String notableProjects) { this.notableProjects = notableProjects; }
    public String getMotivationJapan() { return motivationJapan; }
    public void setMotivationJapan(String motivationJapan) { this.motivationJapan = motivationJapan; }
    public String getJapaneseLevel() { return japaneseLevel; }
    public void setJapaneseLevel(String japaneseLevel) { this.japaneseLevel = japaneseLevel; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
