# NihongoDev Platform — TODO

Last updated: 2026-05-07

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

## DONE — BLOC 5: Quiz Module (Enhanced — Multi-mode, Adaptive, Streak)

- [x] Quiz domain model (Quiz, Question, QuizAttempt, QuizResult)
- [x] QuizMode enum (CLASSIC, TIMED, SURVIVAL, SPRINT, REVIEW)
- [x] QuestionType enum (MULTIPLE_CHOICE, TRUE_FALSE, TEXT_INPUT, MATCHING)
- [x] DifficultyLevel enum (EASY, MEDIUM, HARD)
- [x] AttemptStatus enum (IN_PROGRESS, COMPLETED, ABANDONED, GAME_OVER)
- [x] QuizCompletedEvent (domain event)
- [x] Strategy Pattern: QuestionCorrector interface + 4 correctors (MultipleChoice, TrueFalse, TextInput, Matching)
- [x] QuestionCorrectorFactory (EnumMap-based static factory)
- [x] ScoreCalculator (time bonus + streak multiplier + difficulty multiplier)
- [x] Ports in: CreateQuizPort, GetQuizPort, StartQuizPort, SubmitAnswerPort, CompleteQuizPort, GetQuizHistoryPort
- [x] Ports out: QuizRepositoryPort, QuestionRepositoryPort, QuizAttemptRepositoryPort, QuizResultRepositoryPort
- [x] Commands: CreateQuizCommand, AddQuestionCommand, SubmitAnswerCommand
- [x] DTOs: QuizDto, QuestionDto, QuizAttemptDto, AnswerResultDto, QuizResultDto
- [x] CreateQuizUseCase (quiz + questions)
- [x] GetQuizUseCase (by id, lesson, published)
- [x] StartQuizUseCase (mode selection, max attempts check)
- [x] SubmitAnswerUseCase (correction + streak + score + game over)
- [x] CompleteQuizUseCase (result calculation + event publishing)
- [x] GetQuizHistoryUseCase (user history + result by attempt)
- [x] QuizEntity + QuestionEntity + QuizAttemptEntity + QuizResultEntity
- [x] JpaQuizRepository + JpaQuestionRepository + JpaQuizAttemptRepository + JpaQuizResultRepository
- [x] QuizPersistenceMapper + QuestionPersistenceMapper + QuizAttemptPersistenceMapper + QuizResultPersistenceMapper
- [x] QuizRepositoryAdapter + QuestionRepositoryAdapter + QuizAttemptRepositoryAdapter + QuizResultRepositoryAdapter
- [x] QuizController (9 endpoints: create quiz/question, get, start, answer, complete, history, results)
- [x] Request DTOs: CreateQuizRequest, AddQuestionRequest, SubmitAnswerRequest, StartQuizRequest
- [x] Security: GET /api/quizzes/published & /api/quizzes/{id} public, all others authenticated
- [x] V4 migration: quiz_attempts, quiz_results tables + indexes, quiz/questions enhancements
- [x] Kafka: quiz-events topic + QuizCompletedEvent
- [x] Unit tests: CreateQuizUseCaseTest (4 tests)
- [x] Unit tests: StartQuizUseCaseTest (6 tests)
- [x] Unit tests: SubmitAnswerUseCaseTest (8 tests)
- [x] Unit tests: CompleteQuizUseCaseTest (5 tests)
- [x] Unit tests: ScoreCalculatorTest (6 tests)
- [x] Unit tests: QuestionCorrectorFactoryTest (7 tests)
- [x] Unit tests: QuizAttemptTest — domain model (9 tests)
- [x] Compilation verified OK (45 tests pass)

## DONE — BLOC 6: Interview Simulation (Enhanced — Multi-dimensional scoring, Strategy evaluation)

- [x] InterviewSession domain model (phases, multi-dimensional scores, pass/fail)
- [x] InterviewQuestion domain model (bank with expectedKeywords, modelAnswer, scoringCriteria)
- [x] InterviewAnswer domain model (4 score dimensions + overall)
- [x] InterviewFeedback domain model (strengths, improvements, grammarNotes, vocabularySuggestions)
- [x] InterviewType enum (HR_JAPANESE, TECH_JAVA, TECH_SPRING, TECH_AWS, BEHAVIORAL, SELF_INTRODUCTION, BUSINESS_JAPANESE)
- [x] InterviewDifficulty enum (BEGINNER, INTERMEDIATE, ADVANCED)
- [x] InterviewPhase enum (INTRODUCTION, MAIN_QUESTIONS, FOLLOW_UP, CLOSING)
- [x] SessionStatus enum (SCHEDULED, IN_PROGRESS, COMPLETED, ABANDONED)
- [x] Domain events: InterviewStartedEvent, InterviewAnswerEvaluatedEvent, InterviewCompletedEvent
- [x] Strategy Pattern: AnswerEvaluator interface + 3 evaluators (KeywordBased, ReferenceAnswer, Structure)
- [x] AnswerEvaluatorFactory (EnumMap-based, maps interview type to evaluator)
- [x] KeywordBasedEvaluator: keyword detection + language/cultural scoring (keigo detection)
- [x] ReferenceAnswerEvaluator: Jaccard similarity + vocabulary suggestions
- [x] StructureEvaluator: STAR method detection + intro/conclusion analysis
- [x] Ports in: StartInterviewPort, GetNextQuestionPort, SubmitInterviewAnswerPort, CompleteInterviewPort, GetInterviewHistoryPort
- [x] Ports out: InterviewSessionRepositoryPort, InterviewQuestionRepositoryPort, InterviewAnswerRepositoryPort
- [x] Commands: StartInterviewCommand, SubmitInterviewAnswerCommand
- [x] DTOs: InterviewSessionDto, InterviewQuestionDto, InterviewFeedbackDto, InterviewAnswerResultDto
- [x] StartInterviewUseCase (question selection, shuffle, event publishing)
- [x] GetNextQuestionUseCase (session validation, question retrieval)
- [x] SubmitInterviewAnswerUseCase (evaluation + scoring + session advancement + event)
- [x] CompleteInterviewUseCase (final score + pass/fail + event)
- [x] GetInterviewHistoryUseCase (user history + session details)
- [x] InterviewQuestionEntity + InterviewSessionEntity + InterviewAnswerEntity
- [x] JpaInterviewQuestionRepository + JpaInterviewSessionRepository + JpaInterviewAnswerRepository
- [x] InterviewQuestionPersistenceMapper + InterviewSessionPersistenceMapper + InterviewAnswerPersistenceMapper
- [x] InterviewQuestionRepositoryAdapter + InterviewSessionRepositoryAdapter + InterviewAnswerRepositoryAdapter
- [x] InterviewController (6 endpoints: start, next-question, answer, complete, history, session details)
- [x] Request DTOs: StartInterviewRequest, SubmitInterviewAnswerRequest
- [x] V5 migration: interview_questions, interview_sessions, interview_answers tables + seed data (14 questions)
- [x] Kafka: interview-events topic (already configured)
- [x] Unit tests: StartInterviewUseCaseTest (4 tests)
- [x] Unit tests: SubmitInterviewAnswerUseCaseTest (6 tests)
- [x] Unit tests: CompleteInterviewUseCaseTest (5 tests)
- [x] Unit tests: AnswerEvaluatorFactoryTest (8 tests)
- [x] Unit tests: InterviewSessionTest — domain model (10 tests)
- [x] Compilation verified OK (136 total tests pass)

## DONE — BLOC 7: Correction Intelligente (Enhanced — Pipeline, Multi-dimensional, Context-Aware)

- [x] Domain models: TextType, Severity, AnnotationCategory enums
- [x] Annotation domain model (positioned annotations with severity, category, rule tracking)
- [x] CorrectionScore domain model (6 dimensions: Grammar, Vocabulary, Politeness, Clarity, Naturalness, Professional)
- [x] CorrectionContext domain model (context-aware: EMAIL_TO_CLIENT, STANDUP_REPORT, CODE_REVIEW, INTERVIEW, SLACK, COMMIT)
- [x] CorrectionRule domain model (extensible rule engine with pattern, contexts, level)
- [x] CorrectionSession domain model (full session with score, annotations, severity counts)
- [x] WeaknessPattern domain model (recurring error tracking, isRecurring threshold)
- [x] TextCorrectedEvent (domain event)
- [x] CorrectionStep interface (Chain of Responsibility)
- [x] GrammarStep (particle errors, conjugation, redundancy detection)
- [x] VocabularyStep (casual→professional suggestions, IT vocabulary detection)
- [x] PolitenessStep (keigo detection, desu/masu, casual endings, context-aware strictness)
- [x] ClarityStep (sentence length, structure analysis)
- [x] NaturalnessStep (unnatural patterns, connector usage, Japanese detection)
- [x] ProfessionalStep (opener/closer detection, IT terms, emoji in formal)
- [x] CorrectionEngine interface (Strategy Pattern for future AI integration)
- [x] PatternCorrectionEngine (regex-based rule engine, MVP)
- [x] CorrectionMerger (deduplication, overlap detection, severity-priority sorting)
- [x] CorrectionPipeline (orchestrates steps + engine + merger)
- [x] Ports in: CorrectTextPort, GetCorrectionHistoryPort, GetWeaknessReportPort
- [x] Ports out: CorrectionSessionRepositoryPort, CorrectionRuleRepositoryPort, WeaknessPatternRepositoryPort
- [x] CorrectTextUseCase (full pipeline execution + weakness tracking + Kafka event)
- [x] GetCorrectionHistoryUseCase (user history + session by ID with auth check)
- [x] GetWeaknessReportUseCase (aggregated weakness report with averages)
- [x] CorrectionSessionEntity + CorrectionAnnotationEntity + CorrectionRuleEntity + WeaknessPatternEntity
- [x] JpaCorrectionSessionRepository + JpaCorrectionRuleRepository + JpaWeaknessPatternRepository
- [x] CorrectionSessionPersistenceMapper + CorrectionRulePersistenceMapper + WeaknessPatternPersistenceMapper
- [x] CorrectionSessionRepositoryAdapter + CorrectionRuleRepositoryAdapter + WeaknessPatternRepositoryAdapter
- [x] CorrectionController (4 endpoints: correct, history, session, weakness-report)
- [x] CorrectTextRequest with @Size(max=5000) validation
- [x] V6 migration: correction_sessions, correction_annotations, correction_rules, weakness_patterns + 8 seed rules
- [x] Kafka: correction-events topic
- [x] Unit tests: CorrectionPipelineTest (5 tests)
- [x] Unit tests: GrammarStepTest (6 tests)
- [x] Unit tests: PolitenessStepTest (5 tests)
- [x] Unit tests: PatternCorrectionEngineTest (4 tests)
- [x] Unit tests: CorrectionMergerTest (4 tests)
- [x] Unit tests: CorrectTextUseCaseTest (5 tests)
- [x] Unit tests: CorrectionSessionTest (4 tests)
- [x] Unit tests: WeaknessPatternTest (3 tests)
- [x] Compilation verified OK (172 total tests pass)

## DONE — BLOC 8: Progress & Analytics (Enhanced — CQRS-light, Event-driven, Batch Statistics)

- [x] Domain enums: ProgressLevel, ModuleType, ModuleStatus, ActivityType, Trend, RecommendationType, Priority
- [x] Value objects: WeakArea (auto-prioritization), Recommendation (from weak areas)
- [x] UserProgress aggregate (XP/Level system, weighted global score, streak tracking)
- [x] ModuleProgress domain model (per-module avg/best score, completion %)
- [x] LearningActivity domain model (immutable activity log with metadata)
- [x] UserStatistics domain model (7d/30d/allTime averages, velocity, consistency, trend detection)
- [x] ProgressUpdatedEvent (domain event)
- [x] Ports IN: UpdateProgressOnLesson/Quiz/Interview/CorrectionCompleted, GetUserProgress, GetModuleProgress, GetUserActivityHistory, GetUserStatistics, GetPlatformAnalytics, RecalculateStatistics
- [x] Ports OUT: ProgressRepositoryPort, ModuleProgressRepositoryPort, LearningActivityRepositoryPort, StatisticsRepositoryPort, AnalyticsQueryPort
- [x] DTOs: UserProgressDto, ModuleProgressDto, LearningActivityDto, UserStatisticsDto, WeakAreaDto, RecommendationDto, PlatformAnalyticsDto, TopUserDto
- [x] UpdateProgressOnQuizCompletedUseCase (idempotent, XP calculation, streak update, event publish)
- [x] UpdateProgressOnLessonCompletedUseCase (base XP, no score weight)
- [x] UpdateProgressOnInterviewCompletedUseCase (LocalDateTime.now() fallback for missing occurredAt)
- [x] UpdateProgressOnCorrectionCompletedUseCase (correction score-weighted XP)
- [x] GetUserProgressUseCase, GetModuleProgressUseCase, GetUserActivityHistoryUseCase (paginated)
- [x] GetUserStatisticsUseCase (weak areas + recommendations)
- [x] GetPlatformAnalyticsUseCase (admin dashboard: active users, avg scores, leaderboard)
- [x] RecalculateStatisticsUseCase (batch job logic: averages, velocity, consistency, trend, weak areas)
- [x] V7 Flyway migration: user_progress, module_progress, learning_activities, user_statistics tables
- [x] JPA entities: UserProgressEntity, ModuleProgressEntity, LearningActivityEntity, UserStatisticsEntity
- [x] JPA repositories with custom queries (distinct days active, user IDs with activity since)
- [x] Persistence mappers: UserProgress, ModuleProgress, LearningActivity, UserStatistics (JSON serialization for weak areas/recommendations)
- [x] Repository adapters: ProgressRepositoryAdapter, ModuleProgressRepositoryAdapter, LearningActivityRepositoryAdapter, StatisticsRepositoryAdapter, AnalyticsQueryAdapter
- [x] ProgressEventConsumer (Kafka consumer for lesson-events, quiz-events, interview-events, correction-events)
- [x] StatisticsRecalculationJob (@Scheduled every 15 minutes)
- [x] StreakCalculationJob (@Scheduled daily at 2am — reset broken streaks)
- [x] ProgressController (7 endpoints: progress, modules, module by type, activities, statistics, weak-areas, recommendations)
- [x] AnalyticsController (2 endpoints: overview, top-users)
- [x] @EnableScheduling on application class
- [x] Kafka consumer config: JsonDeserializer + trusted packages
- [x] KafkaTopicsProperties: added correctionEvents field
- [x] KafkaEventPublisherAdapter: added correction-events to topic registry
- [x] Unit tests: UserProgressTest (10 tests — XP, level-up, streak logic)
- [x] Unit tests: ModuleProgressTest (6 tests — status transitions, average, best score)
- [x] Unit tests: UpdateProgressOnQuizCompletedUseCaseTest (5 tests — create, increment, idempotent, event, module)
- [x] Unit tests: UpdateProgressOnLessonCompletedUseCaseTest (3 tests — XP, idempotent, event)
- [x] Unit tests: RecalculateStatisticsUseCaseTest (3 tests — recalculate, trend, weak areas)
- [x] Unit tests: GetUserProgressUseCaseTest (2 tests — existing, new user)
- [x] Unit tests: ProgressEventConsumerTest (5 tests — delegation, error handling)
- [x] Compilation verified OK (36 new tests, all pass)

## DONE — BLOC 9: CV Generator (Pitch & Self-Introduction)

- [x] Domain enums: PitchType, TargetCompanyType, ExportFormat
- [x] WorkExperience value object (company, role, durationMonths, highlights)
- [x] CvProfile aggregate (one per user, JSONB lists, factory method)
- [x] GeneratedPitch entity (full Markdown content, version history)
- [x] PitchGeneratedEvent (domain event)
- [x] Composable Pipeline Pattern: PitchSection interface + PitchAssembler
- [x] 12 PitchSection implementations (EN/JP intro, experience, tech stack, motivation, certifications, closing, interview opening, project highlights)
- [x] Java 21 switch expressions for TargetCompanyType tone adaptation in each section
- [x] Ports in: CreateCvProfilePort, UpdateCvProfilePort, GetCvProfilePort, GeneratePitchPort, GetPitchHistoryPort, ExportPitchPort
- [x] Ports out: CvProfileRepositoryPort, GeneratedPitchRepositoryPort
- [x] Commands: CreateCvProfileCommand (nested WorkExperienceData), UpdateCvProfileCommand, GeneratePitchCommand
- [x] DTOs: CvProfileDto, WorkExperienceDto, GeneratedPitchDto
- [x] CreateCvProfileUseCase (duplicate check)
- [x] UpdateCvProfileUseCase (partial update, null = no change)
- [x] GetCvProfileUseCase
- [x] GeneratePitchUseCase (load profile → assemble → save → publish event)
- [x] GetPitchHistoryUseCase (history + latest by type)
- [x] ExportPitchUseCase (MARKDOWN as-is, PLAIN_TEXT strips markdown, ownership check)
- [x] V8 Flyway migration: cv_profiles (JSONB), generated_pitches tables + indexes
- [x] CvProfileEntity (@JdbcTypeCode JSONB) + GeneratedPitchEntity
- [x] JpaCvProfileRepository + JpaGeneratedPitchRepository
- [x] CvProfilePersistenceMapper (ObjectMapper for JSONB) + GeneratedPitchPersistenceMapper
- [x] CvProfileRepositoryAdapter + GeneratedPitchRepositoryAdapter
- [x] PitchAssemblerConfig (@Configuration, wires 12 sections into 4 PitchType registries)
- [x] CvGeneratorController (7 endpoints: POST/PUT/GET profile, POST generate, GET history/latest/export)
- [x] Kafka: cv-generator-events topic (partitions: 3, replicas: 1)
- [x] Unit tests: CvProfileTest (3), PitchAssemblerTest (3), IntroSectionENTest (4), IntroSectionJPTest (4), ExperienceSectionENTest (3), TechStackSectionTest (2), CreateCvProfileUseCaseTest (2), GeneratePitchUseCaseTest (3), ExportPitchUseCaseTest (3)
- [x] Compilation verified OK (235 total tests pass)

## DONE — BLOC 10: Event-Driven Architecture Kafka (Refactoring & Hardening)

- [x] DomainEvent interface (eventId, eventType, userId, occurredAt)
- [x] Refactored 10 existing events to implement DomainEvent (Java 21 records)
- [x] NotificationRequestedEvent (inert, declared for future use)
- [x] KafkaEventPublisherAdapter enhanced (eventId keying, async callback, structured logging)
- [x] KafkaConsumerConfig (DefaultErrorHandler + DeadLetterPublishingRecoverer)
- [x] Exponential backoff retry: 1s, 2s, 4s (maxElapsedTime 7000ms)
- [x] Non-retryable exceptions (IllegalArgumentException, NullPointerException) -> DLT direct
- [x] ProgressEventConsumer simplified (no try/catch, validation -> IllegalArgumentException)
- [x] Dead Letter Topic: dead-letter-events (partitions: 1, replicas: 1)
- [x] spring.json.add.type.headers: true (producer)
- [x] KafkaTopicsProperties + KafkaConfig: added deadLetterEvents, cvGeneratorEvents, correctionEvents beans
- [x] Updated all use cases for new event factory signatures
- [x] spring-kafka-test dependency added
- [x] Unit tests: KafkaEventPublisherAdapterTest (4 tests)
- [x] Unit tests: ProgressEventConsumerTest (6 tests)
- [x] Unit tests: KafkaConsumerConfigTest (2 tests)
- [x] Integration test: KafkaIntegrationTest (@EmbeddedKafka)
- [x] 241 total tests pass, 0 failures

## TODO — BLOC 11: Cultural Intelligence (Innovation)

- [ ] CulturalScenario domain model
- [ ] Cultural score system
- [ ] KeigoValidator
- [ ] CulturalController

## TODO — BLOC 12: Code in Japanese (Innovation)

- [ ] CodeReviewExercise domain model
- [ ] PRWriting exercises
- [ ] CommitMessage validator
- [ ] CodeJapaneseController

## TODO — BLOC 13: Real Content Engine (Innovation)

- [ ] Content ingestion pipeline
- [ ] Annotation engine
- [ ] Personalized content selector
- [ ] RealContentController

## TODO — BLOC 14: Portfolio & Recruiter (Innovation)

- [ ] PublicProfile domain model
- [ ] Badge system
- [ ] Portfolio sharing
- [ ] RecruiterController

## TODO — BLOC 15: Notification & CV Generator

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

**BLOC 11: Cultural Intelligence (Innovation)** — Cultural scenarios, keigo validation, and cultural scoring for Japanese workplace situations.
