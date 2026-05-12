package com.nihongodev.platform.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Notification {

    private UUID id;
    private UUID userId;
    private NotificationType type;
    private NotificationChannel channel;
    private String title;
    private String message;
    private boolean read;
    private boolean emailSent;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;

    public Notification() {}

    public static Notification create(UUID userId, NotificationType type, NotificationChannel channel,
                                      String title, String message) {
        Notification n = new Notification();
        n.id = UUID.randomUUID();
        n.userId = userId;
        n.type = type;
        n.channel = channel;
        n.title = title;
        n.message = message;
        n.read = false;
        n.emailSent = false;
        n.createdAt = LocalDateTime.now();
        return n;
    }

    public void markAsRead() {
        this.read = true;
        this.readAt = LocalDateTime.now();
    }

    public void markEmailSent() {
        this.emailSent = true;
    }

    public boolean shouldSendEmail() {
        return (channel == NotificationChannel.EMAIL || channel == NotificationChannel.BOTH) && !emailSent;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }
    public NotificationChannel getChannel() { return channel; }
    public void setChannel(NotificationChannel channel) { this.channel = channel; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
    public boolean isEmailSent() { return emailSent; }
    public void setEmailSent(boolean emailSent) { this.emailSent = emailSent; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }
}
