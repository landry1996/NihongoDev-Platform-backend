package com.nihongodev.platform.infrastructure.mail;

import com.nihongodev.platform.application.port.out.EmailSenderPort;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderAdapter implements EmailSenderPort {

    private static final Logger log = LoggerFactory.getLogger(EmailSenderAdapter.class);

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:noreply@nihongodev.com}")
    private String fromAddress;

    public EmailSenderAdapter(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    @Async
    public void sendHtml(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("HTML email sent [to={}, subject={}]", to, subject);
        } catch (MessagingException e) {
            log.error("Failed to send HTML email [to={}, subject={}]: {}", to, subject, e.getMessage());
        }
    }

    @Override
    @Async
    public void sendPlain(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
        log.info("Plain email sent [to={}, subject={}]", to, subject);
    }
}
