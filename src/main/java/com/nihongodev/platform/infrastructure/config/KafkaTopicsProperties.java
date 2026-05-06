package com.nihongodev.platform.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.kafka.topics")
public class KafkaTopicsProperties {

    private TopicDef userEvents = new TopicDef();
    private TopicDef lessonEvents = new TopicDef();
    private TopicDef quizEvents = new TopicDef();
    private TopicDef interviewEvents = new TopicDef();
    private TopicDef progressEvents = new TopicDef();
    private TopicDef vocabularyEvents = new TopicDef();
    private TopicDef notificationEvents = new TopicDef();

    public TopicDef getUserEvents() { return userEvents; }
    public void setUserEvents(TopicDef userEvents) { this.userEvents = userEvents; }
    public TopicDef getLessonEvents() { return lessonEvents; }
    public void setLessonEvents(TopicDef lessonEvents) { this.lessonEvents = lessonEvents; }
    public TopicDef getQuizEvents() { return quizEvents; }
    public void setQuizEvents(TopicDef quizEvents) { this.quizEvents = quizEvents; }
    public TopicDef getInterviewEvents() { return interviewEvents; }
    public void setInterviewEvents(TopicDef interviewEvents) { this.interviewEvents = interviewEvents; }
    public TopicDef getProgressEvents() { return progressEvents; }
    public void setProgressEvents(TopicDef progressEvents) { this.progressEvents = progressEvents; }
    public TopicDef getVocabularyEvents() { return vocabularyEvents; }
    public void setVocabularyEvents(TopicDef vocabularyEvents) { this.vocabularyEvents = vocabularyEvents; }
    public TopicDef getNotificationEvents() { return notificationEvents; }
    public void setNotificationEvents(TopicDef notificationEvents) { this.notificationEvents = notificationEvents; }

    public static class TopicDef {
        private String name;
        private int partitions = 3;
        private int replicas = 1;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getPartitions() { return partitions; }
        public void setPartitions(int partitions) { this.partitions = partitions; }
        public int getReplicas() { return replicas; }
        public void setReplicas(int replicas) { this.replicas = replicas; }
    }
}
