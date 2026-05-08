package com.nihongodev.platform.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoginAttemptServiceTest {

    private LoginAttemptService service;

    @BeforeEach
    void setUp() {
        service = new LoginAttemptService();
    }

    @Test
    void isBlocked_shouldReturnFalse_whenNoAttempts() {
        assertThat(service.isBlocked("test@example.com")).isFalse();
    }

    @Test
    void isBlocked_shouldReturnFalse_afterLessThan5Attempts() {
        for (int i = 0; i < 4; i++) {
            service.loginFailed("test@example.com");
        }
        assertThat(service.isBlocked("test@example.com")).isFalse();
    }

    @Test
    void isBlocked_shouldReturnTrue_after5Attempts() {
        for (int i = 0; i < 5; i++) {
            service.loginFailed("test@example.com");
        }
        assertThat(service.isBlocked("test@example.com")).isTrue();
    }

    @Test
    void isBlocked_shouldReturnFalse_afterSuccessfulLogin() {
        for (int i = 0; i < 5; i++) {
            service.loginFailed("test@example.com");
        }
        service.loginSucceeded("test@example.com");
        assertThat(service.isBlocked("test@example.com")).isFalse();
    }

    @Test
    void isBlocked_shouldNotAffectOtherEmails() {
        for (int i = 0; i < 5; i++) {
            service.loginFailed("blocked@example.com");
        }
        assertThat(service.isBlocked("other@example.com")).isFalse();
    }
}
