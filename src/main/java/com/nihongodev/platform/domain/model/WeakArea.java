package com.nihongodev.platform.domain.model;

public class WeakArea {
    private ModuleType moduleType;
    private String topic;
    private double averageScore;
    private Priority priority;

    public WeakArea() {}

    public WeakArea(ModuleType moduleType, String topic, double averageScore, Priority priority) {
        this.moduleType = moduleType;
        this.topic = topic;
        this.averageScore = averageScore;
        this.priority = priority;
    }

    public static WeakArea identify(ModuleType moduleType, String topic, double averageScore) {
        Priority priority;
        if (averageScore < 60) {
            priority = Priority.HIGH;
        } else if (averageScore < 75) {
            priority = Priority.MEDIUM;
        } else {
            priority = Priority.LOW;
        }
        return new WeakArea(moduleType, topic, averageScore, priority);
    }

    public ModuleType getModuleType() { return moduleType; }
    public void setModuleType(ModuleType moduleType) { this.moduleType = moduleType; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public double getAverageScore() { return averageScore; }
    public void setAverageScore(double averageScore) { this.averageScore = averageScore; }
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
}
