# Security Policy — NihongoDev Platform

## Architecture de Securite

```
Client -> [HTTPS] -> [CORS] -> [Rate Limit] -> [JWT Auth] -> [RBAC] -> Controller
                                                                            |
                                                                    [Input Validation]
                                                                            |
                                                                    [Ownership Check]
                                                                            |
                                                                      Use Case
```

---

## Checklist Securite

### Authentification & Autorisation
- [x] JWT avec signature HS512
- [x] Refresh token stocke en DB
- [ ] Refresh token rotation (invalidation a chaque usage)
- [ ] JWT jti claim pour revocation
- [ ] Issuer + Audience claims
- [x] BCrypt password hashing
- [ ] BCrypt strength 12
- [ ] Brute force protection (LoginAttemptService)
- [ ] Rate limiting (Bucket4j) sur /api/auth/**
- [x] Roles : ADMIN, TEACHER, LEARNER
- [ ] @PreAuthorize sur tous les controllers
- [ ] IDOR protection (ResourceOwnershipChecker)

### Validation des Inputs
- [x] Bean Validation (@Valid, @NotBlank, @Size) sur DTOs principaux
- [ ] Bean Validation complete sur TOUS les DTOs
- [ ] InputSanitizer (strip HTML, normalize unicode)
- [ ] Payload size limits (2MB max)
- [x] CorrectTextRequest @Size(max=5000)

### Headers & Transport
- [x] CORS configure
- [ ] CORS strict par profil (dev vs prod)
- [ ] X-Content-Type-Options: nosniff
- [ ] X-Frame-Options: DENY
- [ ] Strict-Transport-Security (HSTS)
- [ ] Content-Security-Policy
- [ ] Cache-Control: no-store sur auth endpoints

### Logging & Audit
- [x] Structured logging (SLF4J)
- [ ] Correlation ID (X-Request-ID)
- [ ] Audit logs (login, access denied, password change)
- [ ] Log sanitization (pas de passwords/tokens dans les logs)
- [x] Pas de stacktrace pour 500 cote client

### Configuration & Secrets
- [x] Secrets en variables d'environnement (JWT_SECRET)
- [ ] application-secret.yml gitignored
- [ ] application-secret.example.yml versionne
- [x] .gitignore pour fichiers sensibles

### Protection OWASP
- [x] SQL Injection : JPA parameterized queries
- [ ] XSS : InputSanitizer + CSP header
- [ ] Broken Access Control : RBAC + IDOR
- [x] Sensitive Data Exposure : pas de stacktrace client
- [ ] JWT Misuse : jti blacklist, issuer/audience verification
- [ ] CORS Misconfiguration : whitelist stricte en prod
- [ ] Brute Force : rate limiting + login attempts tracking

---

## Signaler une Vulnerabilite

Si vous decouvrez une vulnerabilite de securite, **ne creez pas d'issue publique**.

Contactez : security@nihongodev.com

Nous nous engageons a :
1. Accuser reception sous 48h
2. Fournir un correctif sous 7 jours pour les vulnerabilites critiques
3. Crediter le rapporteur (si souhaite)

---

## Dependances de Securite

| Composant | Version | Role |
|-----------|---------|------|
| Spring Security | 6.x | Framework auth/authz |
| JJWT | 0.12.x | Signature JWT |
| BCrypt | via Spring | Hashing passwords |
| Bucket4j | 8.10.1 (a ajouter) | Rate limiting |

---

## Profils d'Environnement

| Profil | CORS | Logging | JWT TTL | Rate Limit |
|--------|------|---------|---------|------------|
| dev | * (all origins) | DEBUG | 1h | Desactive |
| test | localhost | INFO | 1h | Desactive |
| prod | whitelist stricte | WARN | 15min | Active |

---

## Plan d'Implementation

Voir : `docs/superpowers/plans/2026-05-08-bloc10b-security-hardening.md`

Phases :
1. **Fondations** — JWT durci, BCrypt 12, headers, CORS, secrets
2. **Protection active** — Brute force, rate limit, IDOR, audit
3. **Verification** — Tests securite, ArchUnit rules, revue complete
