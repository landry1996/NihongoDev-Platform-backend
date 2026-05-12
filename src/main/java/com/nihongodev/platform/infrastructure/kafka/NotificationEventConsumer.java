package com.nihongodev.platform.infrastructure.kafka;

import com.nihongodev.platform.application.port.in.SendNotificationPort;
import com.nihongodev.platform.domain.event.*;
import com.nihongodev.platform.domain.model.NotificationChannel;
import com.nihongodev.platform.domain.model.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(KafkaTemplate.class)
public class NotificationEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationEventConsumer.class);

    private final SendNotificationPort sendNotificationPort;

    public NotificationEventConsumer(SendNotificationPort sendNotificationPort) {
        this.sendNotificationPort = sendNotificationPort;
    }

    @KafkaListener(topics = "user-events", groupId = "notification-consumer-group")
    public void handleUserRegistered(UserRegisteredEvent event) {
        log.info("Notification: user registered [userId={}]", event.userId());
        sendNotificationPort.send(
                event.userId(),
                NotificationType.WELCOME,
                NotificationChannel.BOTH,
                "NihongoDev へようこそ！",
                "Welcome to NihongoDev, " + event.firstName() + "! Your journey to mastering Japanese for IT starts now. 頑張りましょう！"
        );
    }

    @KafkaListener(topics = "badge-events", groupId = "notification-consumer-group")
    public void handleBadgeEarned(BadgeEarnedEvent event) {
        log.info("Notification: badge earned [userId={}, badge={}]", event.userId(), event.badgeCode());
        sendNotificationPort.send(
                event.userId(),
                NotificationType.BADGE_EARNED,
                NotificationChannel.BOTH,
                "新しいバッジを獲得！ — " + event.badgeNameJp(),
                "Congratulations! You earned the badge '" + event.badgeNameJp() + "' (+" + event.xpReward() + " XP). Keep up the great work!"
        );
    }

    @KafkaListener(topics = "progress-events", groupId = "notification-consumer-group")
    public void handleProgressUpdated(ProgressUpdatedEvent event) {
        log.info("Notification: progress updated [userId={}, level={}]", event.userId(), event.level());
        sendNotificationPort.send(
                event.userId(),
                NotificationType.LEVEL_UP,
                NotificationChannel.IN_APP,
                "レベルアップ！ — Level " + event.level(),
                "You reached level " + event.level() + " with " + event.totalXp() + " XP! Your global score is " + String.format("%.1f", event.globalScore()) + "%."
        );
    }

    @KafkaListener(topics = "quiz-events", groupId = "notification-consumer-group")
    public void handleQuizCompleted(QuizCompletedEvent event) {
        log.info("Notification: quiz completed [userId={}]", event.userId());
        sendNotificationPort.send(
                event.userId(),
                NotificationType.QUIZ_COMPLETED,
                NotificationChannel.IN_APP,
                "クイズ完了！",
                "You completed a quiz with a score of " + String.format("%.0f", event.percentage()) + "%. "
                        + (event.percentage() >= 80 ? "素晴らしい！ Excellent work!" : "Keep practicing, you'll improve!")
        );
    }

    @KafkaListener(topics = "interview-events", groupId = "notification-consumer-group",
            containerFactory = "kafkaListenerContainerFactory",
            properties = {"spring.json.value.default.type=com.nihongodev.platform.domain.event.InterviewCompletedEvent"})
    public void handleInterviewCompleted(InterviewCompletedEvent event) {
        log.info("Notification: interview completed [userId={}]", event.userId());
        sendNotificationPort.send(
                event.userId(),
                NotificationType.INTERVIEW_COMPLETED,
                NotificationChannel.IN_APP,
                "面接シミュレーション完了",
                "Interview simulation completed! Review your feedback to improve for next time."
        );
    }

    @KafkaListener(topics = "cv-generator-events", groupId = "notification-consumer-group")
    public void handlePitchGenerated(PitchGeneratedEvent event) {
        log.info("Notification: pitch generated [userId={}, type={}]", event.userId(), event.pitchType());
        sendNotificationPort.send(
                event.userId(),
                NotificationType.PITCH_GENERATED,
                NotificationChannel.IN_APP,
                "CV/Pitch が生成されました",
                "Your " + event.pitchType().toLowerCase().replace('_', ' ') + " has been generated. Check it out in your profile!"
        );
    }
}
