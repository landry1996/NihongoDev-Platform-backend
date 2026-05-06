package com.nihongodev.platform.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "vocabularies")
public class VocabularyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "lesson_id")
    private UUID lessonId;

    @Column(length = 255)
    private String french;

    @Column(length = 255)
    private String english;

    @Column(nullable = false, length = 255)
    private String japanese;

    @Column(length = 255)
    private String romaji;

    @Column(columnDefinition = "TEXT")
    private String example;

    @Column(name = "code_example", columnDefinition = "TEXT")
    private String codeExample;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(length = 50)
    private String level;

    @Column(length = 100)
    private String domain;

    @Column(columnDefinition = "TEXT")
    private String tags;

    @Column(name = "difficulty_score")
    private double difficultyScore;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getLessonId() { return lessonId; }
    public void setLessonId(UUID lessonId) { this.lessonId = lessonId; }
    public String getFrench() { return french; }
    public void setFrench(String french) { this.french = french; }
    public String getEnglish() { return english; }
    public void setEnglish(String english) { this.english = english; }
    public String getJapanese() { return japanese; }
    public void setJapanese(String japanese) { this.japanese = japanese; }
    public String getRomaji() { return romaji; }
    public void setRomaji(String romaji) { this.romaji = romaji; }
    public String getExample() { return example; }
    public void setExample(String example) { this.example = example; }
    public String getCodeExample() { return codeExample; }
    public void setCodeExample(String codeExample) { this.codeExample = codeExample; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public double getDifficultyScore() { return difficultyScore; }
    public void setDifficultyScore(double difficultyScore) { this.difficultyScore = difficultyScore; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
