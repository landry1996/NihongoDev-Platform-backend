package com.nihongodev.platform.application.port.out;

public interface EmailSenderPort {
    void sendHtml(String to, String subject, String htmlBody);
    void sendPlain(String to, String subject, String body);
}
