package com.nihongodev.platform.infrastructure.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class LoginAttemptService {

    private static final Logger log = LoggerFactory.getLogger(LoginAttemptService.class);

    private static final int BLOCK_THRESHOLD_1 = 5;
    private static final int BLOCK_THRESHOLD_2 = 10;
    private static final int BLOCK_THRESHOLD_3 = 20;
    private static final long BLOCK_DURATION_15MIN = 15 * 60;
    private static final long BLOCK_DURATION_1H = 60 * 60;
    private static final long BLOCK_DURATION_24H = 24 * 60 * 60;

    private final ConcurrentMap<String, AttemptInfo> attemptsCache = new ConcurrentHashMap<>();

    public void loginFailed(String email) {
        AttemptInfo info = attemptsCache.computeIfAbsent(email, k -> new AttemptInfo());
        info.incrementAttempts();
        info.setLastAttempt(Instant.now());

        if (info.getAttempts() >= BLOCK_THRESHOLD_3) {
            info.setBlockedUntil(Instant.now().plusSeconds(BLOCK_DURATION_24H));
            log.warn("[BRUTE_FORCE_BLOCKED] email={} attempts={} blockDuration=24h", sanitizeEmail(email), info.getAttempts());
        } else if (info.getAttempts() >= BLOCK_THRESHOLD_2) {
            info.setBlockedUntil(Instant.now().plusSeconds(BLOCK_DURATION_1H));
            log.warn("[BRUTE_FORCE_BLOCKED] email={} attempts={} blockDuration=1h", sanitizeEmail(email), info.getAttempts());
        } else if (info.getAttempts() >= BLOCK_THRESHOLD_1) {
            info.setBlockedUntil(Instant.now().plusSeconds(BLOCK_DURATION_15MIN));
            log.warn("[BRUTE_FORCE_BLOCKED] email={} attempts={} blockDuration=15min", sanitizeEmail(email), info.getAttempts());
        }
    }

    public void loginSucceeded(String email) {
        attemptsCache.remove(email);
    }

    public boolean isBlocked(String email) {
        AttemptInfo info = attemptsCache.get(email);
        if (info == null) {
            return false;
        }
        if (info.getBlockedUntil() == null) {
            return false;
        }
        if (Instant.now().isAfter(info.getBlockedUntil())) {
            attemptsCache.remove(email);
            return false;
        }
        return true;
    }

    private String sanitizeEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "***";
        }
        int atIndex = email.indexOf('@');
        return email.charAt(0) + "***" + email.substring(atIndex);
    }

    private static class AttemptInfo {
        private int attempts;
        private Instant lastAttempt;
        private Instant blockedUntil;

        void incrementAttempts() { this.attempts++; }
        int getAttempts() { return attempts; }
        void setLastAttempt(Instant lastAttempt) { this.lastAttempt = lastAttempt; }
        Instant getBlockedUntil() { return blockedUntil; }
        void setBlockedUntil(Instant blockedUntil) { this.blockedUntil = blockedUntil; }
    }
}
