package com.nihongodev.platform.domain.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Vocabulary {

    private UUID id;
    private UUID lessonId;
    private String french;
    private String english;
    private String japanese;
    private String romaji;
    private String example;
    private String codeExample;
    private VocabularyCategory category;
    private VocabularyLevel level;
    private String domain;
    private List<String> tags;
    private double difficultyScore;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Vocabulary() {}

    public static Vocabulary create(String french, String english, String japanese, String romaji,
                                    String example, String codeExample, VocabularyCategory category,
                                    VocabularyLevel level, String domain, List<String> tags, UUID lessonId) {
        Vocabulary v = new Vocabulary();
        v.id = UUID.randomUUID();
        v.french = french;
        v.english = english;
        v.japanese = japanese;
        v.romaji = romaji;
        v.example = example;
        v.codeExample = codeExample;
        v.category = category;
        v.level = level != null ? level : VocabularyLevel.BEGINNER;
        v.domain = domain;
        v.tags = tags;
        v.lessonId = lessonId;
        v.difficultyScore = 0.5;
        v.createdAt = LocalDateTime.now();
        v.updatedAt = LocalDateTime.now();
        return v;
    }

    public void update(String french, String english, String japanese, String romaji,
                       String example, String codeExample, VocabularyCategory category,
                       VocabularyLevel level, String domain, List<String> tags, UUID lessonId) {
        if (french != null) this.french = french;
        if (english != null) this.english = english;
        if (japanese != null) this.japanese = japanese;
        if (romaji != null) this.romaji = romaji;
        if (example != null) this.example = example;
        if (codeExample != null) this.codeExample = codeExample;
        if (category != null) this.category = category;
        if (level != null) this.level = level;
        if (domain != null) this.domain = domain;
        if (tags != null) this.tags = tags;
        if (lessonId != null) this.lessonId = lessonId;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateDifficultyScore(double globalFailRate) {
        this.difficultyScore = Math.min(1.0, Math.max(0.0, globalFailRate));
        this.updatedAt = LocalDateTime.now();
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
    public VocabularyCategory getCategory() { return category; }
    public void setCategory(VocabularyCategory category) { this.category = category; }
    public VocabularyLevel getLevel() { return level; }
    public void setLevel(VocabularyLevel level) { this.level = level; }
    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public double getDifficultyScore() { return difficultyScore; }
    public void setDifficultyScore(double difficultyScore) { this.difficultyScore = difficultyScore; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
