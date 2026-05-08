package com.nihongodev.platform.infrastructure.security;

import com.nihongodev.platform.domain.exception.RateLimitExceededException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int AUTH_REGISTER_LIMIT = 3;
    private static final int AUTH_LOGIN_LIMIT = 10;
    private static final int AUTH_REFRESH_LIMIT = 20;
    private static final int DEFAULT_LIMIT = 100;
    private static final long WINDOW_SECONDS = 60;

    private final ConcurrentMap<String, RateBucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String clientIp = getClientIp(request);
        int limit = resolveLimit(path);
        String bucketKey = clientIp + ":" + resolveBucketCategory(path);

        RateBucket bucket = buckets.computeIfAbsent(bucketKey, k -> new RateBucket(limit, Instant.now()));

        synchronized (bucket) {
            Instant now = Instant.now();
            if (now.getEpochSecond() - bucket.windowStart.getEpochSecond() >= WINDOW_SECONDS) {
                bucket.reset(limit, now);
            }
            if (bucket.remaining <= 0) {
                response.setStatus(429);
                response.setHeader("Retry-After", String.valueOf(WINDOW_SECONDS));
                response.setContentType("application/json");
                response.getWriter().write("{\"status\":429,\"error\":\"Too Many Requests\",\"message\":\"Rate limit exceeded. Please try again later.\"}");
                return;
            }
            bucket.remaining--;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator") || path.startsWith("/swagger-ui") || path.startsWith("/api-docs");
    }

    private int resolveLimit(String path) {
        if (path.contains("/api/auth/register")) return AUTH_REGISTER_LIMIT;
        if (path.contains("/api/auth/login")) return AUTH_LOGIN_LIMIT;
        if (path.contains("/api/auth/refresh")) return AUTH_REFRESH_LIMIT;
        return DEFAULT_LIMIT;
    }

    private String resolveBucketCategory(String path) {
        if (path.contains("/api/auth/register")) return "register";
        if (path.contains("/api/auth/login")) return "login";
        if (path.contains("/api/auth/refresh")) return "refresh";
        return "default";
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private static class RateBucket {
        int remaining;
        Instant windowStart;

        RateBucket(int limit, Instant windowStart) {
            this.remaining = limit;
            this.windowStart = windowStart;
        }

        void reset(int limit, Instant now) {
            this.remaining = limit;
            this.windowStart = now;
        }
    }
}
