package com.nihongodev.platform.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Quiz {

    private UUID id;
    private UUID lessonId;
    private String title;
    private String description;
    private String level;
    private QuizMode mode;
    private int timeLimitSeconds;
    private int maxAttempts;
    private int passingScore;
    private boolean published;
    private List<Question> questions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Quiz() {
        this.questions = new ArrayList<>();
    }

    public static Quiz create(UUID lessonId, String title, String description, String level,
                              QuizMode mode, int timeLimitSeconds, int maxAttempts, int passingScore) {
        Quiz quiz = new Quiz();
        quiz.id = UUID.randomUUID();
        quiz.lessonId = lessonId;
        quiz.title = title;
        quiz.description = description;
        quiz.level = level;
        quiz.mode = mode != null ? mode : QuizMode.CLASSIC;
        quiz.timeLimitSeconds = timeLimitSeconds;
        quiz.maxAttempts = maxAttempts;
        quiz.passingScore = passingScore > 0 ? passingScore : 60;
        quiz.published = true;
        quiz.questions = new ArrayList<>();
        quiz.createdAt = LocalDateTime.now();
        quiz.updatedAt = LocalDateTime.now();
        return quiz;
    }

    public void addQuestion(Question question) {
        this.questions.add(question);
        this.updatedAt = LocalDateTime.now();
    }

    public int getTotalPoints() {
        return questions.stream().mapToInt(Question::getPoints).sum();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getLessonId() { return lessonId; }
    public void setLessonId(UUID lessonId) { this.lessonId = lessonId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public QuizMode getMode() { return mode; }
    public void setMode(QuizMode mode) { this.mode = mode; }
    public int getTimeLimitSeconds() { return timeLimitSeconds; }
    public void setTimeLimitSeconds(int timeLimitSeconds) { this.timeLimitSeconds = timeLimitSeconds; }
    public int getMaxAttempts() { return maxAttempts; }
    public void setMaxAttempts(int maxAttempts) { this.maxAttempts = maxAttempts; }
    public int getPassingScore() { return passingScore; }
    public void setPassingScore(int passingScore) { this.passingScore = passingScore; }
    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }
    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
