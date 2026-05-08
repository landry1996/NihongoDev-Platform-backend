# Security Policy

## Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| 0.x.x   | :white_check_mark: |

## Reporting a Vulnerability

If you discover a security vulnerability, please report it responsibly:

1. **Do NOT** open a public GitHub issue
2. Send an email to security@nihongodev.com with:
   - Description of the vulnerability
   - Steps to reproduce
   - Potential impact
   - Suggested fix (if any)

We will acknowledge receipt within 48 hours and provide a detailed response within 7 days.

## Security Measures

This application implements the following security controls:

### Authentication & Authorization
- JWT-based authentication with HS512 signing
- Access tokens with 15-minute TTL
- Refresh token rotation with family-based revocation detection
- BCrypt password hashing (strength 12)
- Role-Based Access Control (RBAC): ADMIN, TEACHER, LEARNER
- Method-level security via @PreAuthorize

### Input Protection
- Bean Validation on all DTOs
- XSS sanitization on text inputs
- Payload size limits (1MB file, 2MB request)
- Rate limiting on authentication endpoints

### Brute Force Protection
- Progressive blocking: 5 attempts (15min), 10 (1h), 20 (24h)
- Per-IP rate limiting on auth endpoints

### Security Headers
- X-Content-Type-Options: nosniff
- X-Frame-Options: DENY
- Strict-Transport-Security (HSTS)
- Content-Security-Policy: default-src 'self'
- Referrer-Policy: strict-origin-when-cross-origin
- Permissions-Policy: camera=(), microphone=(), geolocation=()

### CORS
- Strict origin whitelist in production
- No wildcard origins with credentials

### Logging & Audit
- Correlation ID (X-Request-ID) on all requests
- Security audit logging (login, logout, access denied, rate limited)
- Sensitive data masking in logs (emails, tokens, passwords)
- No stack traces exposed to clients

### Data Protection
- Secrets via environment variables (never in config files)
- SQL injection prevention via JPA parameterized queries
- HTTPS enforced in production (HSTS)

## Configuration

### Secrets
Copy `application-secret.example.yml` to `application-secret.yml` and fill in production values.
The JWT secret MUST be at least 64 characters for HS512.

### Environment Variables
| Variable | Description |
|----------|-------------|
| JWT_SECRET | JWT signing secret (min 64 chars) |
| DATABASE_URL | PostgreSQL connection URL |
| DATABASE_USERNAME | Database username |
| DATABASE_PASSWORD | Database password |
| REDIS_HOST | Redis host |
| REDIS_PASSWORD | Redis password |
| CORS_ALLOWED_ORIGINS | Comma-separated allowed origins |
