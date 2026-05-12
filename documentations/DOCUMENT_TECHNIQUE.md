# NihongoDev Platform — Document Technique

## 1. Repositories

| Repository | URL | Description |
|------------|-----|-------------|
| Backend | https://github.com/landry1996/NihongoDev-Platform-backend | Java 21 + Spring Boot 3.3.5, Architecture Hexagonale |
| Frontend | https://github.com/landry1996/NihongoDev-Platform-frontend | Angular 21, Standalone Components, Signals |

---

## 2. Stack Technologique

### Backend

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

### Frontend

| Composant | Technologie | Version | Justification |
|-----------|-------------|---------|---------------|
| Framework | Angular | 21 | Standalone components, signals, inject pattern |
| Langage | TypeScript | 5.x | Typage fort aligne avec DTOs backend |
| Styling | SCSS + Tailwind CSS | 4.x | Utilitaires + composants custom |
| Build | @angular/build (ESBuild) | - | Build rapide, HMR |
| HTTP | HttpClient + Interceptors | Angular built-in | Injection Bearer automatique |
| State | Signals | Angular built-in | Reactivite sans boilerplate |
| Routing | Angular Router (lazy) | - | Code-splitting, performance |

---

## 3. Architecture

### 3.1 Architecture Hexagonale (Ports & Adapters)

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

### 3.2 Structure des Packages

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

## 4. Base de Donnees

### 4.1 Schema (13 migrations Flyway)

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

### 4.2 Strategies de Stockage

- **JSONB** : `cv_profiles.tech_stack`, `cv_profiles.experiences`, `user_statistics.weak_areas`
- **Index partiels** : `WHERE is_read = FALSE` sur notifications
- **Full-text search** : `to_tsvector` sur vocabulary

---

## 5. Event-Driven Architecture (Kafka)

### 5.1 Topics

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

### 5.2 Error Handling

- **Retry** : Backoff exponentiel 1s → 2s → 4s (max 7s)
- **Dead Letter Topic** : Messages non-retryables envoyes au DLT
- **Non-retryable** : `IllegalArgumentException`, `NullPointerException`
- **Consumer groups** : `progress-consumer-group`, `notification-consumer-group`

### 5.3 Serialization

- Producer : `JsonSerializer` avec type headers
- Consumer : `JsonDeserializer` avec trusted packages (`com.nihongodev.platform.domain.event`)

---

## 6. Securite

### 6.1 Authentification

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

### 6.2 Protections

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

### 6.3 Roles et Autorisations

- Endpoints publics : `GET /api/lessons/**`, `GET /api/vocabulary/**`, `GET /api/quizzes/published`
- Endpoints admin : `POST /api/lessons`, `GET /api/analytics/**`
- Endpoints authentifies : tout le reste (`.anyRequest().authenticated()`)

---

## 7. Integration LLM (Claude API)

### 7.1 Architecture

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

### 7.2 Configuration

```yaml
app:
  llm:
    api-key: ${LLM_API_KEY}
    model: claude-sonnet-4-6-20250514
    base-url: https://api.anthropic.com
```

### 7.3 Prompt Engineering

Le system prompt s'adapte dynamiquement :
- **PitchType** → langue (japonais avec keigo ou anglais)
- **TargetCompanyType** → ton (startup=moderne, traditionnelle=keigo formel, enterprise=structure)

---

## 8. Email Service

### 8.1 Architecture

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

### 8.2 Configuration

```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
```

---

## 9. API REST

### 9.1 Endpoints (17 controllers, ~80+ endpoints)

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

### 9.2 Conventions

- Reponses : `ApiErrorResponse` uniforme en cas d'erreur
- Validation : Bean Validation (jakarta.validation) sur tous les Request DTOs
- Pagination : `Pageable` Spring Data pour les listes
- Status codes : 201 (created), 204 (no content), 400 (validation), 401, 403, 404

---

## 10. Tests

### 10.1 Strategie

| Type | Outils | Scope |
|------|--------|-------|
| Unitaires | JUnit 5 + Mockito | Use cases, domain models, services |
| Integration | @EmbeddedKafka | Kafka producer/consumer |
| Architecture | ArchUnit | Regles hexagonales |
| API (a venir) | MockMvc | Controllers |

### 10.2 Couverture

- **444 tests** au total
- 0 failures
- Chaque use case a au minimum 2-3 tests (succes, erreur, edge case)
- Les domain models ont des tests de logique metier

---

## 11. Deploiement

### 11.1 Docker Compose (Developpement)

```bash
docker-compose up -d  # PostgreSQL + Redis + Kafka + Zookeeper
```

### 11.2 Dockerfile (Production)

```dockerfile
# Multi-stage build
FROM eclipse-temurin:21-jdk-alpine AS build
COPY . .
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:21-jre-alpine
COPY --from=build target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 11.3 Frontend (Angular)

```bash
cd NihongoDev-Platform-frontend
npm install
npx ng serve  # Developpement (http://localhost:4200)
npx ng build  # Production (dist/)
```

### 11.4 Variables d'Environnement Requises

**Backend :**

| Variable | Description |
|----------|-------------|
| JWT_SECRET | Cle secrete HS512 (min 64 chars) |
| KAFKA_BOOTSTRAP_SERVERS | Adresse Kafka (default: localhost:9092) |
| MAIL_USERNAME | Compte SMTP |
| MAIL_PASSWORD | Mot de passe SMTP |
| LLM_API_KEY | Cle API Anthropic |
| CORS_ALLOWED_ORIGINS | Origins autorisees |

**Frontend :**

| Variable | Description |
|----------|-------------|
| API_BASE_URL | URL du backend (default: http://localhost:8080/api) |

---

## 12. Monitoring et Observabilite

| Aspect | Implementation |
|--------|---------------|
| Health check | `/actuator/health` |
| Structured logging | SLF4J + Logback, MDC avec correlationId |
| Audit | SecurityAuditAspect (actions sensibles) |
| Metriques Kafka | Logs producer/consumer avec offsets |
| Error tracking | Dead Letter Topic pour messages en echec |

---

## 13. Frontend (Angular)

### 13.1 Stack Technologique

| Composant | Technologie | Version | Justification |
|-----------|-------------|---------|---------------|
| Framework | Angular | 21 | Signals, standalone components, latest features |
| Langage | TypeScript | 5.x | Typage fort, interfaces alignees avec DTOs backend |
| Styling | SCSS + Tailwind CSS | 4.x | Utilitaires + styles custom pour composants |
| HTTP | HttpClient + Interceptors | Angular built-in | Bearer token automatique |
| Routing | Angular Router | Lazy-loading | Performance, code-splitting |
| State | Signals | Angular built-in | Reactivite sans RxJS pour l'UI |
| Build | @angular/build | Application builder | ESBuild, fast HMR |

### 13.2 Architecture Frontend

```
src/app/
├── core/
│   ├── guards/           # Auth guard (redirection si non-connecte)
│   ├── interceptors/     # Bearer token injection automatique
│   ├── models/           # Interfaces TypeScript (miroir des DTOs Java)
│   └── services/         # Services HTTP par domaine
│       ├── auth.service.ts        # JWT, login, register, logout
│       ├── lesson.service.ts      # CRUD lecons
│       ├── vocabulary.service.ts  # CRUD + SRS review
│       ├── quiz.service.ts        # Start, answer, complete
│       ├── interview.service.ts   # Sessions d'entretien
│       ├── correction.service.ts  # Soumission + historique
│       ├── progress.service.ts    # Statistiques + recommandations
│       ├── cv.service.ts          # Profil + generation (regles + LLM)
│       └── notification.service.ts # In-app notifications
├── layout/
│   ├── layout.component.ts    # Shell (sidebar + content)
│   └── sidebar.component.ts   # Navigation + notifications
└── features/
    ├── auth/          # Login page
    ├── dashboard/     # Tableau de bord principal
    ├── lessons/       # Liste + detail
    ├── vocabulary/    # 3 onglets (tous/revision/quiz)
    ├── quiz/          # Liste + player interactif
    ├── interview/     # Simulation d'entretien
    ├── correction/    # Correction IA avec jauges
    ├── progress/      # Progression et statistiques
    ├── cv/            # Generateur de CV/Pitch
    └── notifications/ # Centre de notifications
```

### 13.3 Modeles TypeScript (Alignement Backend)

Les interfaces TypeScript sont un miroir des DTOs Java :

| Interface Frontend | DTO Backend | Usage |
|-------------------|-------------|-------|
| `UserDto` | `UserDto` | Profil utilisateur |
| `LessonDto` | `LessonDto` | Affichage des lecons |
| `VocabularyDto` | `VocabularyDto` | Fiches de vocabulaire |
| `QuizDto`, `QuestionDto` | `QuizDto`, `QuestionDto` | Quiz et questions |
| `InterviewSessionDto` | `InterviewSessionDto` | Sessions d'entretien |
| `CorrectionResultDto` | `CorrectionResultDto` | Resultats de correction |
| `UserProgressDto` | `UserProgressDto` | Progression |
| `CvProfileDto`, `GeneratedPitchDto` | `CvProfileDto`, `GeneratedPitchDto` | CV Generator |
| `NotificationDto` | `NotificationDto` | Notifications |

### 13.4 Securite Frontend

| Mecanisme | Implementation |
|-----------|---------------|
| Token storage | localStorage (access + refresh) |
| Auto-injection | HTTP Interceptor ajoute `Authorization: Bearer <token>` |
| Guard | `authGuard` redirige vers `/login` si pas de token |
| Refresh | Rotation automatique du refresh token |
| Logout | Suppression des tokens + redirection |

### 13.5 Conventions et Patterns

- **Standalone components** : Pas de NgModules, chaque composant est autonome
- **inject()** : Injection de dependances fonctionnelle (pas de constructeur)
- **Signals** : Etat local reactif sans `BehaviorSubject`
- **Inline templates** : Template et styles dans le meme fichier `.ts`
- **Lazy routing** : `loadComponent: () => import(...)` pour chaque feature

---

## 14. Design Patterns Utilises

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
