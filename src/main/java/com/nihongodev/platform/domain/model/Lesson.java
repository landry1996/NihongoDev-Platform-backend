package com.nihongodev.platform.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Lesson {

    private UUID id;
    private String title;
    private String description;
    private LessonType type;
    private LessonLevel level;
    private String content;
    private int orderIndex;
    private boolean published;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Lesson() {}

    public static Lesson create(String title, String description, LessonType type, LessonLevel level, String content, int orderIndex) {
        Lesson lesson = new Lesson();
        lesson.id = UUID.randomUUID();
        lesson.title = title;
        lesson.description = description;
        lesson.type = type;
        lesson.level = level;
        lesson.content = content;
        lesson.orderIndex = orderIndex;
        lesson.published = true;
        lesson.createdAt = LocalDateTime.now();
        lesson.updatedAt = LocalDateTime.now();
        return lesson;
    }

    public void update(String title, String description, LessonType type, LessonLevel level, String content, Integer orderIndex) {
        if (title != null) this.title = title;
        if (description != null) this.description = description;
        if (type != null) this.type = type;
        if (level != null) this.level = level;
        if (content != null) this.content = content;
        if (orderIndex != null) this.orderIndex = orderIndex;
        this.updatedAt = LocalDateTime.now();
    }

    public void publish() {
        this.published = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void unpublish() {
        this.published = false;
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LessonType getType() { return type; }
    public void setType(LessonType type) { this.type = type; }
    public LessonLevel getLevel() { return level; }
    public void setLevel(LessonLevel level) { this.level = level; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public int getOrderIndex() { return orderIndex; }
    public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }
    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
