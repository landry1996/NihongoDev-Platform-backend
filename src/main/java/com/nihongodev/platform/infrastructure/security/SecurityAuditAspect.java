package com.nihongodev.platform.infrastructure.security;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SecurityAuditAspect {

    private static final Logger log = LoggerFactory.getLogger(SecurityAuditAspect.class);

    @AfterReturning("execution(* com.nihongodev.platform.infrastructure.web.controller.AuthController.login(..))")
    public void auditLoginSuccess(JoinPoint joinPoint) {
        log.info("[AUDIT] action=LOGIN_SUCCESS userId={} requestId={}", getCurrentUserId(), MDC.get("requestId"));
    }

    @AfterThrowing(value = "execution(* com.nihongodev.platform.infrastructure.web.controller.AuthController.login(..))", throwing = "ex")
    public void auditLoginFailure(JoinPoint joinPoint, Exception ex) {
        log.warn("[AUDIT] action=LOGIN_FAILURE reason={} requestId={}", ex.getMessage(), MDC.get("requestId"));
    }

    @AfterReturning("execution(* com.nihongodev.platform.infrastructure.web.controller.AuthController.logout(..))")
    public void auditLogout(JoinPoint joinPoint) {
        log.info("[AUDIT] action=LOGOUT userId={} requestId={}", getCurrentUserId(), MDC.get("requestId"));
    }

    @AfterThrowing(value = "execution(* com.nihongodev.platform.infrastructure.web.controller..*(..))", throwing = "ex")
    public void auditAccessDenied(JoinPoint joinPoint, AccessDeniedException ex) {
        log.warn("[AUDIT] action=ACCESS_DENIED userId={} method={} requestId={}",
                getCurrentUserId(), joinPoint.getSignature().toShortString(), MDC.get("requestId"));
    }

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AuthenticatedUser user) {
            return user.id().toString();
        }
        return "anonymous";
    }
}
