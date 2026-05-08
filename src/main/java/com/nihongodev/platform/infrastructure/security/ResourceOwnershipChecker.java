package com.nihongodev.platform.infrastructure.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ResourceOwnershipChecker {

    public void verifyOwnership(UUID resourceOwnerId, UUID authenticatedUserId) {
        if (resourceOwnerId == null || authenticatedUserId == null) {
            throw new AccessDeniedException("Access denied");
        }
        if (isAdmin()) {
            return;
        }
        if (!resourceOwnerId.equals(authenticatedUserId)) {
            throw new AccessDeniedException("Access denied");
        }
    }

    public void verifyOwnership(UUID resourceOwnerId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            throw new AccessDeniedException("Access denied");
        }
        verifyOwnership(resourceOwnerId, user.id());
    }

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
