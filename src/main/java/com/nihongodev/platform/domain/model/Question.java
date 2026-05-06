package com.nihongodev.platform.domain.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Question {

    private UUID id;
    private UUID quizId;
    private String content;
    private String correctAnswer;
    private String explanation;
    private QuestionType questionType;
    private DifficultyLevel difficultyLevel;
    private List<String> options;
    private int points;
    private int timeLimitSeconds;
    private int orderIndex;
    private LocalDateTime createdAt;

    public Question() {}

    public static Question create(UUID quizId, String content, String correctAnswer, String explanation,
                                  QuestionType type, DifficultyLevel difficulty, List<String> options,
                                  int points, int timeLimitSeconds, int orderIndex) {
        Question q = new Question();
        q.id = UUID.randomUUID();
        q.quizId = quizId;
        q.content = content;
        q.correctAnswer = correctAnswer;
        q.explanation = explanation;
        q.questionType = type != null ? type : QuestionType.MULTIPLE_CHOICE;
        q.difficultyLevel = difficulty != null ? difficulty : DifficultyLevel.MEDIUM;
        q.options = options;
        q.points = points > 0 ? points : 1;
        q.timeLimitSeconds = timeLimitSeconds;
        q.orderIndex = orderIndex;
        q.createdAt = LocalDateTime.now();
        return q;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getQuizId() { return quizId; }
    public void setQuizId(UUID quizId) { this.quizId = quizId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    public QuestionType getQuestionType() { return questionType; }
    public void setQuestionType(QuestionType questionType) { this.questionType = questionType; }
    public DifficultyLevel getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(DifficultyLevel difficultyLevel) { this.difficultyLevel = difficultyLevel; }
    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
    public int getTimeLimitSeconds() { return timeLimitSeconds; }
    public void setTimeLimitSeconds(int timeLimitSeconds) { this.timeLimitSeconds = timeLimitSeconds; }
    public int getOrderIndex() { return orderIndex; }
    public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
