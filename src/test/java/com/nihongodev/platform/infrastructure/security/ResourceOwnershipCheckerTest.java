package com.nihongodev.platform.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ResourceOwnershipCheckerTest {

    private ResourceOwnershipChecker checker;
    private final UUID userId = UUID.randomUUID();
    private final UUID otherUserId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        checker = new ResourceOwnershipChecker();
        SecurityContextHolder.clearContext();
    }

    @Test
    void verifyOwnership_shouldPass_whenUserOwnsResource() {
        assertDoesNotThrow(() -> checker.verifyOwnership(userId, userId));
    }

    @Test
    void verifyOwnership_shouldThrow_whenUserDoesNotOwnResource() {
        setupAuth(userId, "LEARNER");
        assertThatThrownBy(() -> checker.verifyOwnership(otherUserId, userId))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void verifyOwnership_shouldPass_whenUserIsAdmin() {
        setupAuth(userId, "ADMIN");
        assertDoesNotThrow(() -> checker.verifyOwnership(otherUserId, userId));
    }

    @Test
    void verifyOwnership_shouldThrow_whenResourceOwnerIdIsNull() {
        assertThatThrownBy(() -> checker.verifyOwnership(null, userId))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void verifyOwnership_singleParam_shouldThrow_whenNotAuthenticated() {
        assertThatThrownBy(() -> checker.verifyOwnership(otherUserId))
                .isInstanceOf(AccessDeniedException.class);
    }

    private void setupAuth(UUID uid, String role) {
        var principal = new AuthenticatedUser(uid, "test@test.com", role);
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
        var auth = new UsernamePasswordAuthenticationToken(principal, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
