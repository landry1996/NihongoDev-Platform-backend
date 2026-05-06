# NihongoDev Platform — TODO

Last updated: 2026-05-06

---

## DONE

- [x] Project structure (hexagonal architecture)
- [x] pom.xml with all dependencies
- [x] Application YAML configs (dev, test, secret)
- [x] Docker Compose (PostgreSQL, Redis, Kafka)
- [x] Dockerfile
- [x] Flyway migration V1 (initial schema)
- [x] GlobalExceptionHandler + ApiErrorResponse
- [x] Domain exceptions (Business, NotFound, Unauthorized)
- [x] Domain models (User, Role, JapaneseLevel, RefreshToken)
- [x] Domain events (UserRegisteredEvent)
- [x] Security config (CORS, stateless, JWT filter)
- [x] Kafka config (topics) + KafkaEventPublisherAdapter
- [x] OpenAPI/Swagger config
- [x] Health controller
- [x] .gitignore
- [x] README.md
- [x] **BLOC 2: Auth & User Module**
- [x] UserEntity + RefreshTokenEntity (JPA)
- [x] JpaUserRepository + JpaRefreshTokenRepository
- [x] UserPersistenceMapper + RefreshTokenPersistenceMapper
- [x] UserRepositoryAdapter + RefreshTokenRepositoryAdapter
- [x] RegisterCommand / LoginCommand / ChangePasswordCommand / UpdateProfileCommand
- [x] AuthResponse / UserDto
- [x] Ports in: RegisterUserPort, LoginPort, RefreshTokenPort, LogoutPort, GetUserProfilePort, UpdateUserProfilePort, ChangePasswordPort
- [x] Ports out: UserRepositoryPort, RefreshTokenRepositoryPort, PasswordEncoderPort, JwtPort, EventPublisherPort
- [x] RegisterUserUseCase
- [x] LoginUseCase
- [x] RefreshTokenUseCase
- [x] LogoutUseCase
- [x] GetUserProfileUseCase
- [x] UpdateUserProfileUseCase
- [x] ChangePasswordUseCase
- [x] JwtService (JJWT implementation)
- [x] JwtAuthenticationFilter
- [x] PasswordEncoderAdapter
- [x] AuthController (register, login, refresh, logout)
- [x] UserController (profile, update, change password)
- [x] Request DTOs with validation (RegisterRequest, LoginRequest, etc.)
- [x] Unit tests: RegisterUserUseCaseTest (3 tests)
- [x] Unit tests: LoginUseCaseTest (4 tests)
- [x] Unit tests: ChangePasswordUseCaseTest (3 tests)
- [x] Compilation verified OK

---

## IN PROGRESS

- [ ] BLOC 3: Learning & Vocabulary Module

---

## TODO — BLOC 3: Learning & Vocabulary Module

- [ ] Lesson domain model
- [ ] LessonEntity + repository
- [ ] AuthUseCase (port in)
- [ ] UserRepositoryPort (port out)
- [ ] RegisterUseCase implementation
- [ ] LoginUseCase implementation
- [ ] JwtService (generate, validate, extract)
- [ ] JwtAuthenticationFilter
- [ ] AuthController (register, login, refresh, logout)
- [ ] RefreshTokenEntity + Repository
- [ ] UserDetailsService implementation

## TODO — BLOC 3: Learning & Vocabulary Module

- [ ] Lesson domain model
- [ ] LessonEntity + repository
- [ ] LessonUseCase (CRUD + complete)
- [ ] LessonController
- [ ] Vocabulary domain model
- [ ] VocabularyEntity + repository
- [ ] VocabularyUseCase (CRUD + search)
- [ ] VocabularyController
- [ ] Seed data (initial IT vocabulary)

## TODO — BLOC 4: Quiz Module

- [ ] Quiz domain model
- [ ] QuizEntity + QuestionEntity
- [ ] QuizUseCase (create, submit, score)
- [ ] QuizController

## TODO — BLOC 5: Interview Simulation Module

- [ ] InterviewSession domain model
- [ ] Answer domain model
- [ ] InterviewUseCase (start, answer, complete)
- [ ] InterviewController
- [ ] AI Correction integration (port out)

## TODO — BLOC 6: Progress & Analytics

- [ ] Progress domain model
- [ ] ProgressUseCase (update, get)
- [ ] ProgressController
- [ ] Kafka consumers for progress updates

## TODO — BLOC 7: Shadow Day (Innovation)

- [ ] ShadowDaySession domain model
- [ ] Scenario generator
- [ ] NPC simulator (LLM integration)
- [ ] Cultural validator
- [ ] ShadowDayController

## TODO — BLOC 8: Cultural Intelligence (Innovation)

- [ ] CulturalScenario domain model
- [ ] Cultural score system
- [ ] KeigoValidator
- [ ] CulturalController

## TODO — BLOC 9: Code in Japanese (Innovation)

- [ ] CodeReviewExercise domain model
- [ ] PRWriting exercises
- [ ] CommitMessage validator
- [ ] CodeJapaneseController

## TODO — BLOC 10: Real Content Engine (Innovation)

- [ ] Content ingestion pipeline
- [ ] Annotation engine
- [ ] Personalized content selector
- [ ] RealContentController

## TODO — BLOC 11: Portfolio & Recruiter (Innovation)

- [ ] PublicProfile domain model
- [ ] Badge system
- [ ] Portfolio sharing
- [ ] RecruiterController

## TODO — BLOC 12: Notification & CV Generator

- [ ] Kafka event consumers
- [ ] Email notification service
- [ ] CV generation (LLM-based)

---

## DECISIONS TECHNIQUES

| Decision | Choix | Raison |
|----------|-------|--------|
| Java version | 21 | Virtual threads, pattern matching, records |
| Architecture | Hexagonale | Séparation domaine/infra, testabilité |
| Events | Kafka | Scalabilité, découplage, replay |
| Auth | JWT + Refresh | Stateless, mobile-ready |
| DB | PostgreSQL | Robuste, JSONB pour flexibilité |
| Mapping | MapStruct | Zero-reflection, compile-time safety |
| Migration | Flyway | Versionnée, reproductible |
| Tests | Testcontainers | Tests réalistes sans mocks DB |

---

## RISQUES

| Risque | Impact | Mitigation |
|--------|--------|------------|
| Scope trop large (15 modules) | Retard MVP | Prioriser blocs 1-5 pour MVP |
| LLM cost for corrections | Budget | Rate limiting + caching |
| Kafka complexity for MVP | Over-engineering | Commencer avec events in-memory, migrer |
| Content ingestion legal | Copyright | Contenu open (CC-BY), RSS publics |

---

## BUGS

(none yet)

---

## NEXT BLOC

**BLOC 2: Auth & User Module** — JWT authentication, registration, login, refresh token, user CRUD.
