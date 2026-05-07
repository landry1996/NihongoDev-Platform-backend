# BLOC 10b — Securite Avancee Production

> **Type** : Hardening / Refactoring (pas de nouvelles features business)
> **Priorite** : Haute — pre-requis avant deploiement production
> **Estime** : 1 journee d'implementation

---

## Objectif

Renforcer l'application contre les attaques OWASP Top 10 et preparer un deploiement production securise.

---

## 1. Validation & Sanitization des Inputs

### 1.1 Bean Validation sur tous les DTOs

Verifier et completer les annotations `@Valid`, `@NotBlank`, `@Size`, `@Email`, `@Pattern` sur :

| DTO | Validations attendues |
|-----|----------------------|
| RegisterRequest | @Email, @Size(min=8, max=100) password, @NotBlank firstName/lastName |
| LoginRequest | @Email, @NotBlank password |
| ChangePasswordRequest | @Size(min=8) newPassword, @NotBlank currentPassword |
| UpdateProfileRequest | @Size(max=200) fields, @Pattern pour avatarUrl |
| CreateLessonRequest | @NotBlank title, @Size(max=5000) content |
| CorrectTextRequest | @Size(max=5000) — deja fait, verifier les autres |
| SubmitInterviewAnswerRequest | @Size(max=3000) answerText |
| CreateCvProfileCommand | @Size(max=500) par champ, @Max(50) yearsExperience |
| StartInterviewRequest | @NotNull type/difficulty |
| GenerateQuizRequest | @Min(4) @Max(50) wordCount |

### 1.2 Sanitization des inputs textuels

```java
@Component
public class InputSanitizer {
    public String sanitize(String input, int maxLength);
    public String sanitizeForLog(String input);
}
```

Appliquer sur :
- Tous les champs texte libres (lessonContent, answerText, correctionText, pitchContent)
- Tous les champs affiches cote client (noms, titres)

### 1.3 Limite taille payload

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 1MB
      max-request-size: 2MB
server:
  tomcat:
    max-http-form-post-size: 2MB
    max-swallow-size: 2MB
```

---

## 2. GlobalExceptionHandler Securise

### 2.1 Regles

- **Jamais** de stacktrace cote client (ni en dev, ni en prod)
- Reponses d'erreur uniformes : `{ "status", "error", "message", "timestamp", "path" }`
- Messages generiques pour 401/403/500
- Messages specifiques uniquement pour 400 (validation) et 404
- Logging complet cote serveur (avec correlation ID)

### 2.2 Structure

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> handleValidation(...);

    @ExceptionHandler(BusinessException.class)
    ResponseEntity<ApiError> handleBusiness(...);

    @ExceptionHandler(AuthenticationException.class)
    ResponseEntity<ApiError> handleAuth(...);  // "Invalid credentials"

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ApiError> handleForbidden(...);  // "Access denied"

    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<ApiError> handleNotFound(...);

    @ExceptionHandler(RateLimitExceededException.class)
    ResponseEntity<ApiError> handleRateLimit(...);

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiError> handleUnexpected(...);  // "Internal server error"
}
```

---

## 3. Authentification & JWT

### 3.1 BCrypt

- Deja implemente via `PasswordEncoderAdapter`
- Verifier : strength >= 12 rounds (`BCryptPasswordEncoder(12)`)

### 3.2 JWT Robuste

| Parametre | Valeur production | Verifier |
|-----------|-------------------|----------|
| Algorithm | HS512 | Pas HS256 |
| Access token TTL | 15min (900000ms) | Pas 1h |
| Refresh token TTL | 7 jours | OK |
| Secret key length | >= 64 chars | Env variable |
| Issuer claim | nihongodev-platform | Ajouter |
| Audience claim | nihongodev-api | Ajouter |
| jti (JWT ID) | UUID unique | Pour revocation |

### 3.3 Refresh Token Securise

- Stocke en DB (deja fait)
- Ajouter : rotation a chaque usage (old token invalide)
- Ajouter : detection de reutilisation (token vole -> invalider toute la famille)
- Ajouter : limit 5 refresh tokens par user (purger les anciens)
- Ajouter : IP binding optionnel (warning si IP change)

### 3.4 Protection Brute Force Login

```java
@Component
public class LoginAttemptService {
    // Cache: email -> { attempts, lastAttempt, blockedUntil }
    // Seuil: 5 tentatives -> block 15min
    // Seuil: 10 tentatives -> block 1h
    // Seuil: 20 tentatives -> block 24h (+ alert admin)
    // Reset apres login reussi
}
```

### 3.5 Rate Limiting sur Auth

```java
@Component
public class RateLimitFilter extends OncePerRequestFilter {
    // Bucket4j
    // /api/auth/register : 3 req/min par IP
    // /api/auth/login : 10 req/min par IP
    // /api/auth/refresh : 20 req/min par IP
    // Autres endpoints : 100 req/min par user
}
```

Dependance :
```xml
<dependency>
    <groupId>com.bucket4j</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>8.10.1</version>
</dependency>
```

---

## 4. Controle d'Acces (RBAC + IDOR)

### 4.1 RBAC par endpoint

| Endpoint | Roles autorises |
|----------|-----------------|
| POST /api/auth/** | PUBLIC |
| GET /api/lessons (published) | PUBLIC |
| GET /api/quizzes (published) | PUBLIC |
| POST /api/lessons | ADMIN, TEACHER |
| PUT/DELETE /api/lessons/** | ADMIN, TEACHER |
| GET /api/analytics/** | ADMIN |
| POST /api/admin/** | ADMIN |
| Tout le reste | LEARNER, TEACHER, ADMIN (authentifie) |

### 4.2 Protection IDOR (Insecure Direct Object Reference)

```java
@Component
public class ResourceOwnershipChecker {
    public void verifyOwnership(UUID resourceOwnerId, UUID authenticatedUserId);
    // Throw AccessDeniedException si mismatch (sauf ADMIN)
}
```

Appliquer sur :
- GET/PUT /api/users/{userId}/profile
- GET /api/progress/{userId}
- GET /api/corrections/history (filtre par user)
- GET /api/interviews/history (filtre par user)
- GET /api/cv-profile (filtre par user)
- PUT /api/quiz-attempts/{attemptId} (verifier owner)

### 4.3 @PreAuthorize sur les controllers

```java
@PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
@GetMapping("/users/{userId}/progress")
public ResponseEntity<?> getProgress(@PathVariable UUID userId) { ... }
```

---

## 5. Headers de Securite & CORS

### 5.1 Security Headers

```
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 0
Strict-Transport-Security: max-age=31536000; includeSubDomains
Content-Security-Policy: default-src 'self'
Referrer-Policy: strict-origin-when-cross-origin
Permissions-Policy: camera=(), microphone=(), geolocation=()
Cache-Control: no-store (sur endpoints auth)
```

### 5.2 CORS Strict

```yaml
# application-prod.yml
app:
  cors:
    allowed-origins:
      - https://nihongodev.com
      - https://app.nihongodev.com
    allowed-methods: GET, POST, PUT, DELETE, OPTIONS
    allowed-headers: Authorization, Content-Type, X-Request-ID
    exposed-headers: X-Request-ID
    max-age: 3600
    allow-credentials: true
```

**Regle absolue :**
- JAMAIS `allowedOrigins("*")` en production
- JAMAIS `allowCredentials(true)` avec `allowedOrigins("*")`

---

## 6. Audit & Logging Securise

### 6.1 Audit Logs

```java
@Aspect
@Component
public class SecurityAuditAspect {
    // Log: LOGIN_SUCCESS, LOGIN_FAILURE, LOGOUT
    // Log: ACCESS_DENIED, RESOURCE_NOT_OWNED
    // Log: RATE_LIMITED, BRUTE_FORCE_BLOCKED
    // Log: PASSWORD_CHANGED, TOKEN_REFRESHED
    // Format: [AUDIT] action={}, userId={}, ip={}, userAgent={}, timestamp={}
}
```

### 6.2 Masquage des Secrets dans Logs

```java
@Component
public class LogSanitizer {
    // Patterns a masquer :
    // - email: p***@domain.com
    // - password: ********
    // - JWT: eyJ...***
    // - secrets/keys: ***
}
```

### 6.3 Correlation ID

```java
@Component
public class CorrelationIdFilter extends OncePerRequestFilter {
    // Header: X-Request-ID
    // Si absent: generer UUID
    // Mettre dans MDC pour tous les logs de la requete
    // Retourner dans response header
}
```

---

## 7. Configuration par Profils

### 7.1 Structure des fichiers

```
src/main/resources/
  application.yml                  # Config commune
  application-dev.yml              # Dev local (CORS *, logs DEBUG)
  application-prod.yml             # Production (strict)
  application-test.yml             # Tests
  application-secret.yml           # Secrets (GITIGNORED)
  application-secret.example.yml   # Template pour les devs
```

### 7.2 application-secret.example.yml (VERSIONNE)

```yaml
# Copier ce fichier en application-secret.yml et remplir les valeurs
app:
  jwt:
    secret: CHANGE_ME_64_CHARS_MINIMUM_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
  database:
    password: CHANGE_ME
  kafka:
    password: CHANGE_ME
```

### 7.3 .gitignore

Verifier ces lignes :
```
application-secret.yml
application-secret.properties
*.env
.env
```

---

## 8. Protection contre les Menaces OWASP

### 8.1 SQL Injection
- JPA/Hibernate parameterized queries (deja securise)
- Verifier : aucun `@Query` avec concatenation de string
- ArchUnit rule de verification

### 8.2 XSS (Cross-Site Scripting)
- API REST JSON-only -> risque limite
- Sanitizer sur inputs textuels (voir 1.2)
- CSP header strict (voir 5.1)

### 8.3 Broken Access Control
- RBAC (voir 4.1)
- IDOR protection (voir 4.2)
- Tests ArchUnit : controllers annotes @PreAuthorize

### 8.4 Sensitive Data Exposure
- Jamais de password/token dans les logs
- Jamais de stacktrace cote client
- HTTPS only en prod (HSTS)
- Secrets en env variables, pas en config

### 8.5 JWT Misuse
- Verifier signature a chaque requete (deja fait)
- Verifier expiration (deja fait)
- Ajouter : blacklist des tokens revoques (logout -> invalidate jti)
- Ajouter : verifier issuer + audience claims

### 8.6 CORS Misconfiguration
- Dev : allowedOrigins("*") autorise (OK pour local)
- Prod : whitelist stricte (voir 5.2)
- ArchUnit rule de verification

---

## 9. Tests de Securite

### 9.1 Tests unitaires
- InputSanitizerTest — XSS payloads, SQL injection strings, unicode
- LoginAttemptServiceTest — block after 5 attempts, reset after success
- RateLimitFilterTest — reject after threshold
- ResourceOwnershipCheckerTest — allow owner, deny other, allow admin
- JwtServiceTest — expired token, invalid signature, missing claims

### 9.2 Tests d'integration
- Acces sans token -> 401
- Token expire -> 401
- Acces ressource d'un autre user -> 403
- ADMIN accede a tout -> 200
- Rate limit atteint -> 429
- Payload trop grand -> 413
- Input malveillant sanitise -> 200 (pas de XSS stocke)

### 9.3 ArchUnit rules
- Controllers annotes @PreAuthorize
- Pas de allowedOrigins("*") en code prod

---

## 10. Checklist d'Implementation

### Phase 1 — Fondations (priorite haute)
- [ ] InputSanitizer + tests
- [ ] GlobalExceptionHandler durci (pas de stacktrace)
- [ ] BCrypt strength 12
- [ ] JWT : HS512, issuer, audience, jti
- [ ] Refresh token rotation
- [ ] Security headers
- [ ] CORS strict par profil
- [ ] application-secret.example.yml + .gitignore
- [ ] Correlation ID filter

### Phase 2 — Protection active
- [ ] LoginAttemptService (brute force)
- [ ] RateLimitFilter (Bucket4j)
- [ ] ResourceOwnershipChecker (IDOR)
- [ ] @PreAuthorize sur tous les controllers
- [ ] Audit logging aspect
- [ ] Log sanitization

### Phase 3 — Verification
- [ ] Completer Bean Validation sur tous les DTOs
- [ ] Payload size limits
- [ ] ArchUnit security rules
- [ ] Tests unitaires securite
- [ ] Tests d'integration securite
- [ ] Revue .gitignore
- [ ] SECURITY.md cree
- [ ] TODO.md mis a jour

---

## Dependances a Ajouter

```xml
<dependency>
    <groupId>com.bucket4j</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>8.10.1</version>
</dependency>
<dependency>
    <groupId>net.logstash.logback</groupId>
    <artifactId>logstash-logback-encoder</artifactId>
    <version>7.4</version>
</dependency>
```

---

## Fichiers a Creer/Modifier

| Fichier | Action |
|---------|--------|
| `InputSanitizer.java` | CREER |
| `LoginAttemptService.java` | CREER |
| `RateLimitFilter.java` | CREER |
| `RateLimitExceededException.java` | CREER |
| `ResourceOwnershipChecker.java` | CREER |
| `SecurityAuditAspect.java` | CREER |
| `CorrelationIdFilter.java` | CREER |
| `LogSanitizer.java` | CREER |
| `SecurityHeadersConfig.java` | CREER |
| `GlobalExceptionHandler.java` | MODIFIER |
| `SecurityConfig.java` | MODIFIER |
| `JwtService.java` | MODIFIER |
| `PasswordEncoderAdapter.java` | MODIFIER |
| `RefreshTokenUseCase.java` | MODIFIER |
| `application.yml` | MODIFIER |
| `application-prod.yml` | CREER |
| `application-secret.example.yml` | CREER |
| `logback-spring.xml` | CREER |
| `.gitignore` | MODIFIER |
| `SECURITY.md` | CREER |
| `TODO.md` | MODIFIER |
| `pom.xml` | MODIFIER |

---

## Contraintes

- Spring Boot 3.3.5, Spring Security 6.x
- Java 21 (records, pattern matching, sealed classes si utile)
- Pas de nouvelles features business
- Retro-compatible avec les tests existants (241 pass)
- Hexagonal architecture respectee
