# NihongoDev Platform — Document Technique

## 1. Stack Technologique

| Composant | Technologie | Version | Justification |
|-----------|-------------|---------|---------------|
| Langage | Java | 21 | Virtual threads, pattern matching, records, switch expressions |
| Framework | Spring Boot | 3.3.5 | Ecosysteme mature, convention-over-config |
| Base de donnees | PostgreSQL | 16 | Robuste, JSONB, full-text search |
| Cache | Redis | 7 | Rate limiting, sessions, cache |
| Message broker | Apache Kafka | 7.6 (Confluent) | Event-driven, scalabilite, replay |
| Migration | Flyway | integre | Versionnement schema reproductible |
| Securite | Spring Security + JWT | 6.x | Stateless, mobile-ready |
| Documentation API | SpringDoc OpenAPI | 2.5.0 | Generation automatique Swagger |
| Mapping | MapStruct | 1.5.5 | Zero-reflection, compile-time |
| Tests | JUnit 5 + Mockito + Testcontainers | - | Tests unitaires + integration realistes |
| Architecture tests | ArchUnit | 1.3.0 | Enforcement des regles hexagonales |
| Conteneurisation | Docker | multi-stage | Build reproductible |
| IA/LLM | Claude API (Anthropic) | claude-sonnet-4-6 | Generation de CV/Pitch |

---

## 2. Architecture

### 2.1 Architecture Hexagonale (Ports & Adapters)

```
┌─────────────────────────────────────────────────────────────────┐
│                        INFRASTRUCTURE                            │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐       │
│  │ Web/REST │  │  Kafka   │  │   JPA    │  │  Claude  │       │
│  │Controller│  │Consumer/ │  │Repository│  │  LLM API │       │
│  │          │  │Publisher │  │          │  │          │       │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘       │
│       │              │              │              │             │
├───────┼──────────────┼──────────────┼──────────────┼─────────────┤
│       ▼              ▼              ▼              ▼             │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │                    PORTS (Interfaces)                     │    │
│  │  ┌─────────────────┐          ┌─────────────────────┐   │    │
│  │  │   Ports IN       │          │     Ports OUT        │   │    │
│  │  │ (Use Case ifaces)│          │ (Repository ifaces)  │   │    │
│  │  └─────────────────┘          └─────────────────────┘   │    │
│  └─────────────────────────────────────────────────────────┘    │
│                              │                                    │
├──────────────────────────────┼────────────────────────────────────┤
│                              ▼                                    │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │                    APPLICATION                            │    │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐              │    │
│  │  │ Use Cases│  │ Commands │  │   DTOs   │              │    │
│  │  └──────────┘  └──────────┘  └──────────┘              │    │
│  └─────────────────────────────────────────────────────────┘    │
│                              │                                    │
├──────────────────────────────┼────────────────────────────────────┤
│                              ▼                                    │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │                      DOMAIN                               │    │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐              │    │
│  │  │  Models  │  │  Events  │  │Exceptions│              │    │
│  │  └──────────┘  └──────────┘  └──────────┘              │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 Structure des Packages

```
src/main/java/com/nihongodev/platform/
├── domain/
│   ├── model/          # Entites metier, Value Objects, Enums
│   ├── event/          # Domain Events (records)
│   └── exception/      # Exceptions metier
├── application/
│   ├── command/        # Objets de commande (input use case)
│   ├── dto/            # Data Transfer Objects (output)
│   ├── port/
│   │   ├── in/         # Interfaces des use cases
│   │   └── out/        # Interfaces vers l'infrastructure
│   ├── usecase/        # Implementation des use cases
│   └── service/        # Services domaine (pipelines, strategies)
└── infrastructure/
    ├── config/         # Configuration Spring beans
    ├── kafka/          # Consumers, Publisher, Config
    ├── llm/            # Adapter Claude API
    ├── mail/           # Adapter email (SMTP)
    ├── persistence/
    │   ├── adapter/    # Implementations des ports OUT
    │   ├── entity/     # Entites JPA
    │   ├── mapper/     # Domain <-> Entity mappers
    │   └── repository/ # JPA Repository interfaces
    ├── security/       # JWT, Filters, Rate Limiting
    └── web/
        ├── controller/ # REST Controllers
        └── request/    # Request DTOs (validation)
```

---

## 3. Base de Donnees

### 3.1 Schema (13 migrations Flyway)

| Migration | Tables |
|-----------|--------|
| V1 | users, refresh_tokens |
| V2 | lessons |
| V3 | vocabulary, vocabulary_mastery, vocabulary_relations |
| V4 | quizzes, questions, quiz_attempts, quiz_results |
| V5 | interview_questions, interview_sessions, interview_answers |
| V6 | correction_sessions, correction_annotations, correction_rules, weakness_patterns |
| V7 | user_progress, module_progress, learning_activities, user_statistics |
| V8 | cv_profiles (JSONB), generated_pitches |
| V9 | cultural_scenarios, scenario_attempts, cultural_progress |
| V10 | code_exercises, code_exercise_attempts, code_japanese_progress, commit_message_rules |
| V11 | real_contents, content_annotations, content_reading_sessions, user_content_preferences |
| V12 | badges, user_badges, public_profiles |
| V13 | notifications |

### 3.2 Strategies de Stockage

- **JSONB** : `cv_profiles.tech_stack`, `cv_profiles.experiences`, `user_statistics.weak_areas`
- **Index partiels** : `WHERE is_read = FALSE` sur notifications
- **Full-text search** : `to_tsvector` sur vocabulary

---

## 4. Event-Driven Architecture (Kafka)

### 4.1 Topics

| Topic | Partitions | Producteur | Consommateurs |
|-------|-----------|------------|---------------|
| user-events | 3 | AuthController | NotificationEventConsumer |
| lesson-events | 3 | CompleteLessonUseCase | ProgressEventConsumer |
| quiz-events | 3 | CompleteQuizUseCase | ProgressEventConsumer, NotificationEventConsumer |
| interview-events | 3 | CompleteInterviewUseCase | ProgressEventConsumer, NotificationEventConsumer |
| correction-events | 3 | CorrectTextUseCase | ProgressEventConsumer |
| vocabulary-events | 3 | GenerateVocabularyQuizUseCase | - |
| progress-events | 3 | UpdateProgress*UseCase | NotificationEventConsumer |
| badge-events | 3 | AwardBadgeUseCase | NotificationEventConsumer |
| cv-generator-events | 3 | GenerateLlmCvUseCase | NotificationEventConsumer |
| notification-events | 3 | SendNotificationUseCase | - |
| cultural-events | 3 | CulturalController | ProgressEventConsumer |
| dead-letter-events | 1 | ErrorHandler (auto) | - |

### 4.2 Error Handling

- **Retry** : Backoff exponentiel 1s → 2s → 4s (max 7s)
- **Dead Letter Topic** : Messages non-retryables envoyes au DLT
- **Non-retryable** : `IllegalArgumentException`, `NullPointerException`
- **Consumer groups** : `progress-consumer-group`, `notification-consumer-group`

### 4.3 Serialization

- Producer : `JsonSerializer` avec type headers
- Consumer : `JsonDeserializer` avec trusted packages (`com.nihongodev.platform.domain.event`)

---

## 5. Securite

### 5.1 Authentification

```
Client → RateLimitFilter → JwtAuthenticationFilter → Controller
                                    │
                                    ▼
                            JwtService.validateToken()
                                    │
                                    ▼
                        AuthenticatedUser(id, email, role)
```

- **JWT** : HS512, issuer/audience claims, jti (unique ID), 15min TTL
- **Refresh Token** : Rotation a chaque usage, 7 jours TTL
- **BCrypt** : Strength 12

### 5.2 Protections

| Protection | Implementation |
|------------|---------------|
| Brute force | LoginAttemptService (5/10/20 tentatives → blocage progressif) |
| Rate limiting | Bucket4j (3-100 req/min par endpoint) |
| IDOR | ResourceOwnershipChecker + @PreAuthorize |
| XSS/Injection | InputSanitizer (strip HTML, normalize unicode) |
| Headers | HSTS, CSP, X-Frame-Options, nosniff, referrer-policy |
| CORS | Strict par profil (dev vs prod) |
| Payload | Max 2MB request body |
| Audit | SecurityAuditAspect (logs des actions sensibles) |
| Correlation | X-Request-ID + MDC pour tracabilite |

### 5.3 Roles et Autorisations

- Endpoints publics : `GET /api/lessons/**`, `GET /api/vocabulary/**`, `GET /api/quizzes/published`
- Endpoints admin : `POST /api/lessons`, `GET /api/analytics/**`
- Endpoints authentifies : tout le reste (`.anyRequest().authenticated()`)

---

## 6. Integration LLM (Claude API)

### 6.1 Architecture

```
GenerateLlmCvUseCase
    │
    ├── Load CvProfile from DB
    ├── Build system prompt (tone + language + format)
    ├── Build user prompt (profile data + instructions)
    │
    ▼
ClaudeLlmAdapter (RestClient)
    │
    ├── POST /v1/messages
    ├── Headers: x-api-key, anthropic-version
    ├── Body: model, max_tokens, system, messages
    │
    ▼
Response → Extract content[0].text → Save GeneratedPitch
```

### 6.2 Configuration

```yaml
app:
  llm:
    api-key: ${LLM_API_KEY}
    model: claude-sonnet-4-6-20250514
    base-url: https://api.anthropic.com
```

### 6.3 Prompt Engineering

Le system prompt s'adapte dynamiquement :
- **PitchType** → langue (japonais avec keigo ou anglais)
- **TargetCompanyType** → ton (startup=moderne, traditionnelle=keigo formel, enterprise=structure)

---

## 7. Email Service

### 7.1 Architecture

```
SendNotificationUseCase
    │
    ├── Save Notification in DB
    ├── Check channel (EMAIL or BOTH?)
    │
    ▼
EmailSenderAdapter (@Async)
    │
    ├── Build HTML template
    ├── MimeMessage via JavaMailSender
    ├── SMTP (configurable: Gmail, SES, etc.)
    │
    ▼
Mark notification.emailSent = true
```

### 7.2 Configuration

```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
```

---

## 8. API REST

### 8.1 Endpoints (17 controllers, ~80+ endpoints)

| Controller | Base Path | Endpoints |
|------------|-----------|-----------|
| AuthController | /api/auth | POST register, login, refresh, logout |
| UserController | /api/users | GET profile, PUT update, POST change-password |
| LessonController | /api/lessons | CRUD + filters + complete |
| VocabularyController | /api/vocabulary | CRUD + batch + search + review + quiz |
| QuizController | /api/quizzes | CRUD + start + answer + complete + history |
| InterviewController | /api/interviews | start, next-question, answer, complete, history |
| CorrectionController | /api/corrections | POST correct, GET history, session, weakness-report |
| ProgressController | /api/progress | GET progress, modules, activities, statistics |
| AnalyticsController | /api/analytics | GET overview, top-users (ADMIN) |
| CvGeneratorController | /api/cv | profile CRUD, generate, generate-llm, pitches, export |
| CulturalController | /api/cultural | scenarios, submit, progress |
| CodeJapaneseController | /api/code-japanese | exercises, submit, progress, validate-commit |
| RealContentController | /api/content | ingest, annotate, publish, feed, read, sessions |
| PortfolioController | /api/portfolio | profile CRUD, badges, showcase |
| RecruiterController | /api/recruiter | search profiles |
| NotificationController | /api/notifications | GET all, unread, count, PATCH read |
| HealthController | /api/health | GET health check |

### 8.2 Conventions

- Reponses : `ApiErrorResponse` uniforme en cas d'erreur
- Validation : Bean Validation (jakarta.validation) sur tous les Request DTOs
- Pagination : `Pageable` Spring Data pour les listes
- Status codes : 201 (created), 204 (no content), 400 (validation), 401, 403, 404

---

## 9. Tests

### 9.1 Strategie

| Type | Outils | Scope |
|------|--------|-------|
| Unitaires | JUnit 5 + Mockito | Use cases, domain models, services |
| Integration | @EmbeddedKafka | Kafka producer/consumer |
| Architecture | ArchUnit | Regles hexagonales |
| API (a venir) | MockMvc | Controllers |

### 9.2 Couverture

- **444 tests** au total
- 0 failures
- Chaque use case a au minimum 2-3 tests (succes, erreur, edge case)
- Les domain models ont des tests de logique metier

---

## 10. Deploiement

### 10.1 Docker Compose (Developpement)

```bash
docker-compose up -d  # PostgreSQL + Redis + Kafka + Zookeeper
```

### 10.2 Dockerfile (Production)

```dockerfile
# Multi-stage build
FROM eclipse-temurin:21-jdk-alpine AS build
COPY . .
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:21-jre-alpine
COPY --from=build target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 10.3 Variables d'Environnement Requises

| Variable | Description |
|----------|-------------|
| JWT_SECRET | Cle secrete HS512 (min 64 chars) |
| KAFKA_BOOTSTRAP_SERVERS | Adresse Kafka (default: localhost:9092) |
| MAIL_USERNAME | Compte SMTP |
| MAIL_PASSWORD | Mot de passe SMTP |
| LLM_API_KEY | Cle API Anthropic |
| CORS_ALLOWED_ORIGINS | Origins autorisees |

---

## 11. Monitoring et Observabilite

| Aspect | Implementation |
|--------|---------------|
| Health check | `/actuator/health` |
| Structured logging | SLF4J + Logback, MDC avec correlationId |
| Audit | SecurityAuditAspect (actions sensibles) |
| Metriques Kafka | Logs producer/consumer avec offsets |
| Error tracking | Dead Letter Topic pour messages en echec |

---

## 12. Design Patterns Utilises

| Pattern | Utilisation |
|---------|-------------|
| Hexagonal Architecture | Separation domaine/infra |
| Strategy | AnswerEvaluator, CorrectionEngine, QuestionCorrector |
| Chain of Responsibility | CorrectionPipeline (6 steps) |
| Factory | QuestionCorrectorFactory, AnswerEvaluatorFactory |
| Builder/Assembler | PitchAssembler (12 sections composables) |
| Repository | Ports OUT abstraction sur JPA |
| Domain Event | Records immutables publies via Kafka |
| CQRS-light | Separation commandes/queries dans les ports IN |
