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

- [ ] BLOC 5: Quiz Module

---

## DONE — BLOC 3: Learning (Lessons)

- [x] Lesson domain model (Lesson, LessonType, LessonLevel)
- [x] LessonCompletedEvent (domain event)
- [x] LessonEntity + JpaLessonRepository
- [x] LessonPersistenceMapper
- [x] LessonRepositoryAdapter (implements LessonRepositoryPort)
- [x] Ports in: CreateLessonPort, UpdateLessonPort, GetLessonPort, CompleteLessonPort, DeleteLessonPort
- [x] Port out: LessonRepositoryPort
- [x] Commands: CreateLessonCommand, UpdateLessonCommand
- [x] DTO: LessonDto
- [x] CreateLessonUseCase
- [x] UpdateLessonUseCase
- [x] GetLessonUseCase (by id, type, level, type+level, published)
- [x] CompleteLessonUseCase (publishes LessonCompletedEvent to Kafka)
- [x] DeleteLessonUseCase
- [x] LessonController (CRUD + filters + complete)
- [x] Request DTOs: CreateLessonRequest, UpdateLessonRequest
- [x] Security: GET /api/lessons public, POST/PUT/DELETE authenticated
- [x] Unit tests: CreateLessonUseCaseTest (3 tests)
- [x] Unit tests: UpdateLessonUseCaseTest (3 tests)
- [x] Unit tests: GetLessonUseCaseTest (5 tests)
- [x] Unit tests: CompleteLessonUseCaseTest (2 tests)
- [x] Unit tests: DeleteLessonUseCaseTest (2 tests)
- [x] Compilation verified OK (15 tests pass)

## DONE — BLOC 4: Vocabulary IT Japanese (Enhanced)

- [x] Vocabulary domain model (Vocabulary, VocabularyCategory, VocabularyLevel)
- [x] VocabularyMastery domain model (SRS — Spaced Repetition System)
- [x] VocabularyRelation domain model (Word Graph)
- [x] MasteryLevel enum (NEW, LEARNING, REVIEWING, MASTERED)
- [x] RelationType enum (SYNONYM, ANTONYM, DERIVATIVE, RELATED, COMPOUND)
- [x] VocabularyQuizGeneratedEvent (domain event)
- [x] VocabularyEntity + VocabularyMasteryEntity + VocabularyRelationEntity
- [x] JpaVocabularyRepository (with JpaSpecificationExecutor + full-text search)
- [x] JpaVocabularyMasteryRepository + JpaVocabularyRelationRepository
- [x] VocabularySpecification (Specification Pattern for advanced search)
- [x] VocabularyPersistenceMapper + VocabularyMasteryPersistenceMapper + VocabularyRelationPersistenceMapper
- [x] VocabularyRepositoryAdapter + VocabularyMasteryRepositoryAdapter + VocabularyRelationRepositoryAdapter
- [x] Ports in: CreateVocabularyPort (single + batch), UpdateVocabularyPort, DeleteVocabularyPort,
      SearchVocabularyPort, ReviewVocabularyPort, GenerateVocabularyQuizPort
- [x] Ports out: VocabularyRepositoryPort, VocabularyMasteryRepositoryPort, VocabularyRelationRepositoryPort
- [x] Commands: CreateVocabularyCommand, UpdateVocabularyCommand, SearchVocabularyCommand, GenerateVocabularyQuizCommand
- [x] DTOs: VocabularyDto, VocabularyMasteryDto, VocabularyQuizDto, VocabularyQuizItemDto
- [x] CreateVocabularyUseCase (single + batch import)
- [x] UpdateVocabularyUseCase
- [x] DeleteVocabularyUseCase
- [x] SearchVocabularyUseCase (full-text + filters)
- [x] ReviewVocabularyUseCase (SM-2 algorithm)
- [x] GenerateVocabularyQuizUseCase (FR_TO_JP, JP_TO_FR, EN_TO_JP, CONTEXT quiz types)
- [x] VocabularyController (CRUD + batch + search + review + quiz)
- [x] Request DTOs: CreateVocabularyRequest, UpdateVocabularyRequest, GenerateQuizRequest, ReviewRequest
- [x] V3 migration: vocabulary_mastery, vocabulary_relations tables + indexes
- [x] Kafka: vocabulary-events topic + VocabularyQuizGeneratedEvent
- [x] Unit tests: CreateVocabularyUseCaseTest (3 tests)
- [x] Unit tests: UpdateVocabularyUseCaseTest (3 tests)
- [x] Unit tests: DeleteVocabularyUseCaseTest (2 tests)
- [x] Unit tests: SearchVocabularyUseCaseTest (4 tests)
- [x] Unit tests: ReviewVocabularyUseCaseTest (4 tests)
- [x] Unit tests: GenerateVocabularyQuizUseCaseTest (3 tests)
- [x] Unit tests: VocabularyMasteryTest — SRS algorithm (6 tests)
- [x] Compilation verified OK (25 tests pass)

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
