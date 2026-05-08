package com.nihongodev.platform.infrastructure.security;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class LogSanitizer {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("([a-zA-Z0-9])[a-zA-Z0-9._%+-]*(@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})");
    private static final Pattern JWT_PATTERN = Pattern.compile("(eyJ[a-zA-Z0-9_-]{5})[a-zA-Z0-9_.-]+");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("(password|pwd|secret|token)([\"':=\\s]+)[^\\s,\"'}{\\]]+", Pattern.CASE_INSENSITIVE);

    public String sanitize(String logMessage) {
        if (logMessage == null) {
            return null;
        }
        String result = logMessage;
        result = EMAIL_PATTERN.matcher(result).replaceAll("$1***$2");
        result = JWT_PATTERN.matcher(result).replaceAll("$1***");
        result = PASSWORD_PATTERN.matcher(result).replaceAll("$1$2********");
        return result;
    }
}
