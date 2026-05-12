package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.port.in.SendNotificationPort;
import com.nihongodev.platform.application.port.out.EmailSenderPort;
import com.nihongodev.platform.application.port.out.NotificationRepositoryPort;
import com.nihongodev.platform.application.port.out.UserRepositoryPort;
import com.nihongodev.platform.domain.model.Notification;
import com.nihongodev.platform.domain.model.NotificationChannel;
import com.nihongodev.platform.domain.model.NotificationType;
import com.nihongodev.platform.domain.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SendNotificationUseCase implements SendNotificationPort {

    private static final Logger log = LoggerFactory.getLogger(SendNotificationUseCase.class);

    private final NotificationRepositoryPort notificationRepository;
    private final EmailSenderPort emailSender;
    private final UserRepositoryPort userRepository;

    public SendNotificationUseCase(NotificationRepositoryPort notificationRepository,
                                   EmailSenderPort emailSender,
                                   UserRepositoryPort userRepository) {
        this.notificationRepository = notificationRepository;
        this.emailSender = emailSender;
        this.userRepository = userRepository;
    }

    @Override
    public void send(UUID userId, NotificationType type, NotificationChannel channel, String title, String message) {
        Notification notification = Notification.create(userId, type, channel, title, message);
        notificationRepository.save(notification);

        if (notification.shouldSendEmail()) {
            sendEmail(userId, notification);
        }

        log.info("Notification sent [userId={}, type={}, channel={}]", userId, type, channel);
    }

    private void sendEmail(UUID userId, Notification notification) {
        userRepository.findById(userId).ifPresent(user -> {
            try {
                String html = buildEmailHtml(user, notification);
                emailSender.sendHtml(user.getEmail(), notification.getTitle(), html);
                notification.markEmailSent();
                notificationRepository.save(notification);
            } catch (Exception e) {
                log.error("Failed to send email [userId={}, type={}]: {}", userId, notification.getType(), e.getMessage());
            }
        });
    }

    private String buildEmailHtml(User user, Notification notification) {
        return """
                <html>
                <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                    <div style="background: #1a1a2e; color: white; padding: 20px; text-align: center;">
                        <h1>NihongoDev</h1>
                    </div>
                    <div style="padding: 20px;">
                        <p>%s %s さん、</p>
                        <h2>%s</h2>
                        <p>%s</p>
                    </div>
                    <div style="background: #f0f0f0; padding: 10px; text-align: center; font-size: 12px;">
                        <p>NihongoDev Platform — Learn Japanese for IT</p>
                    </div>
                </body>
                </html>
                """.formatted(
                user.getFirstName() != null ? user.getFirstName() : "",
                user.getLastName() != null ? user.getLastName() : "",
                notification.getTitle(),
                notification.getMessage()
        );
    }
}
