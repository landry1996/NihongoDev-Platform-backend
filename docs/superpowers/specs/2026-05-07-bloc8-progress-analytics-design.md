# BLOC 8 — Progress & Analytics Design Spec

## Objectif

Suivre la progression utilisateur en temps réel et fournir des analytics avancées (admin + user) via une architecture CQRS léger event-driven.

## Décisions architecturales

| Décision | Choix | Justification |
|----------|-------|---------------|
| Granularité | Multi-niveaux + statistiques pré-calculées | Support futur Shadow Day, Portfolio, Recommandations IA |
| Stratégie de mise à jour | Hybride (temps réel + batch) | Compteurs instantanés, analytics lourdes en différé (15min) |
| Pattern | CQRS léger | Write simple (increment + insert), Read optimisé (projections dénormalisées) |
| Idempotence | Contrainte unique (userId, referenceId, activityType) | Tolérance aux événements dupliqués Kafka |

---

## 1. Modèles de Domaine

### 1.1 UserProgress (Agrégat principal — Write side)

```java
UserProgress {
  id: UUID
  userId: UUID
  totalLessonsCompleted: int
  totalQuizzesCompleted: int
  totalInterviewsCompleted: int
  totalCorrectionsCompleted: int
  globalScore: double
  currentStreak: int
  longestStreak: int
  lastActivityAt: LocalDateTime
  level: ProgressLevel
  totalXp: long
  createdAt: LocalDateTime
  updatedAt: LocalDateTime
}
```

### 1.2 ModuleProgress (Progression par module)

```java
ModuleProgress {
  id: UUID
  userId: UUID
  moduleType: ModuleType
  completedItems: int
  totalItems: int              // nullable
  averageScore: double
  bestScore: double
  lastCompletedAt: LocalDateTime
  status: ModuleStatus
}
```

### 1.3 LearningActivity (Journal — faits immuables)

```java
LearningActivity {
  id: UUID
  userId: UUID
  activityType: ActivityType
  referenceId: UUID
  score: Double                // nullable
  xpEarned: int
  metadata: Map<String, Object>
  occurredAt: LocalDateTime
}
```

### 1.4 UserStatistics (Read side — projection dénormalisée)

```java
UserStatistics {
  id: UUID
  userId: UUID
  averageScore7Days: double
  averageScore30Days: double
  averageScoreAllTime: double
  learningVelocity: double
  consistencyRate: double
  weakAreas: List<WeakArea>
  recommendations: List<Recommendation>
  progressTrend: Trend
  lastCalculatedAt: LocalDateTime
}
```

### 1.5 Enums

```java
enum ProgressLevel { BEGINNER, INTERMEDIATE, ADVANCED, EXPERT }
enum ModuleType { LESSON, QUIZ, INTERVIEW, CORRECTION, SHADOW_DAY, CULTURAL, CODE_REVIEW, REAL_CONTENT }
enum ModuleStatus { NOT_STARTED, IN_PROGRESS, COMPLETED }
enum ActivityType { LESSON_COMPLETED, QUIZ_COMPLETED, INTERVIEW_COMPLETED, CORRECTION_COMPLETED }
enum Trend { IMPROVING, STABLE, DECLINING }
```

### 1.6 Value Objects

```java
WeakArea {
  moduleType: ModuleType
  topic: String
  averageScore: double
  priority: Priority           // HIGH, MEDIUM, LOW
}

Recommendation {
  type: RecommendationType     // REVIEW_LESSON, RETRY_QUIZ, PRACTICE_INTERVIEW, READ_CONTENT
  targetId: UUID
  reason: String
  priority: Priority
}
```

---

## 2. Ports & Use Cases

### 2.1 Ports IN (Write side — event-driven)

```java
UpdateProgressOnLessonCompletedPort {
  void execute(LessonCompletedEvent event)
}

UpdateProgressOnQuizCompletedPort {
  void execute(QuizCompletedEvent event)
}

UpdateProgressOnInterviewCompletedPort {
  void execute(InterviewCompletedEvent event)
}

UpdateProgressOnCorrectionCompletedPort {
  void execute(TextCorrectedEvent event)
}
```

### 2.2 Ports IN (Read side — User)

```java
GetUserProgressPort {
  UserProgressDto execute(UUID userId)
}

GetModuleProgressPort {
  List<ModuleProgressDto> execute(UUID userId)
  ModuleProgressDto execute(UUID userId, ModuleType moduleType)
}

GetUserActivityHistoryPort {
  Page<LearningActivityDto> execute(UUID userId, Pageable pageable)
}

GetUserStatisticsPort {
  UserStatisticsDto execute(UUID userId)
}
```

### 2.3 Ports IN (Read side — Admin)

```java
GetPlatformAnalyticsPort {
  PlatformAnalyticsDto execute(AnalyticsQuery query)
}

GetActiveUsersAnalyticsPort {
  ActiveUsersDto execute(Period period)
}

GetModuleAnalyticsPort {
  ModuleAnalyticsDto execute(ModuleType moduleType, Period period)
}
```

### 2.4 Ports OUT

```java
ProgressRepositoryPort {
  UserProgress save(UserProgress progress)
  Optional<UserProgress> findByUserId(UUID userId)
  void updateStreak(UUID userId, int newStreak, int longestStreak)
}

ModuleProgressRepositoryPort {
  ModuleProgress save(ModuleProgress moduleProgress)
  Optional<ModuleProgress> findByUserIdAndModuleType(UUID userId, ModuleType type)
  List<ModuleProgress> findAllByUserId(UUID userId)
}

LearningActivityRepositoryPort {
  LearningActivity save(LearningActivity activity)
  Page<LearningActivity> findByUserId(UUID userId, Pageable pageable)
  List<LearningActivity> findByUserIdAndOccurredAfter(UUID userId, LocalDateTime after)
  long countByUserIdAndActivityType(UUID userId, ActivityType type)
  boolean existsByUserIdAndReferenceIdAndActivityType(UUID userId, UUID referenceId, ActivityType type)
}

StatisticsRepositoryPort {
  UserStatistics save(UserStatistics statistics)
  Optional<UserStatistics> findByUserId(UUID userId)
  List<UUID> findUserIdsWithActivitySince(LocalDateTime since)
}

AnalyticsRepositoryPort {
  long countActiveUsers(LocalDateTime since)
  double averageGlobalScore()
  Map<ModuleType, Double> averageScoreByModule()
  Map<LocalDate, Long> activityCountByDay(LocalDateTime from, LocalDateTime to)
  List<TopUserDto> topUsers(int limit)
}
```

### 2.5 Use Cases

| Use Case | Type | Déclencheur |
|----------|------|-------------|
| UpdateProgressOnLessonCompletedUseCase | Write | Kafka consumer |
| UpdateProgressOnQuizCompletedUseCase | Write | Kafka consumer |
| UpdateProgressOnInterviewCompletedUseCase | Write | Kafka consumer |
| UpdateProgressOnCorrectionCompletedUseCase | Write | Kafka consumer |
| GetUserProgressUseCase | Read | REST API |
| GetModuleProgressUseCase | Read | REST API |
| GetUserActivityHistoryUseCase | Read | REST API |
| GetUserStatisticsUseCase | Read | REST API |
| GetPlatformAnalyticsUseCase | Read (admin) | REST API |
| GetActiveUsersAnalyticsUseCase | Read (admin) | REST API |
| GetModuleAnalyticsUseCase | Read (admin) | REST API |
| RecalculateStatisticsUseCase | Batch | @Scheduled 15min |
| CalculateStreaksUseCase | Batch | @Scheduled daily 2h |

### 2.6 Événement publié

```java
ProgressUpdatedEvent {
  userId: UUID
  totalXp: long
  level: ProgressLevel
  globalScore: double
  activityType: ActivityType
  occurredAt: LocalDateTime
}
```

---

## 3. Infrastructure

### 3.1 Kafka Consumer

```java
ProgressEventConsumer {
  @KafkaListener(topics = "lesson-events")
  handleLessonCompleted(LessonCompletedEvent event)

  @KafkaListener(topics = "quiz-events")
  handleQuizCompleted(QuizCompletedEvent event)

  @KafkaListener(topics = "interview-events")
  handleInterviewCompleted(InterviewCompletedEvent event)

  @KafkaListener(topics = "correction-events")
  handleCorrectionCompleted(TextCorrectedEvent event)
}
```

Idempotent : vérifie `existsByUserIdAndReferenceIdAndActivityType` avant insertion.

### 3.2 REST Controllers

**ProgressController** (`/api/progress`)

| Méthode | Endpoint | Réponse |
|---------|----------|---------|
| GET | /api/progress/me | UserProgressDto |
| GET | /api/progress/me/modules | List<ModuleProgressDto> |
| GET | /api/progress/me/modules/{type} | ModuleProgressDto |
| GET | /api/progress/me/activities | Page<LearningActivityDto> |
| GET | /api/progress/me/statistics | UserStatisticsDto |
| GET | /api/progress/me/weak-areas | List<WeakAreaDto> |
| GET | /api/progress/me/recommendations | List<RecommendationDto> |

**AnalyticsController** (`/api/admin/analytics`)

| Méthode | Endpoint | Réponse |
|---------|----------|---------|
| GET | /api/admin/analytics/overview | PlatformAnalyticsDto |
| GET | /api/admin/analytics/active-users | ActiveUsersDto |
| GET | /api/admin/analytics/modules/{type} | ModuleAnalyticsDto |
| GET | /api/admin/analytics/top-users | List<TopUserDto> |
| GET | /api/admin/analytics/activity-trend | ActivityTrendDto |

### 3.3 JPA Entities

```java
UserProgressEntity {
  @Table("user_progress")
  id UUID PK, userId UUID UNIQUE, totalLessonsCompleted int,
  totalQuizzesCompleted int, totalInterviewsCompleted int,
  totalCorrectionsCompleted int, globalScore double,
  currentStreak int, longestStreak int, lastActivityAt timestamp,
  level varchar, totalXp bigint, createdAt timestamp, updatedAt timestamp
}

ModuleProgressEntity {
  @Table("module_progress")
  @UniqueConstraint(userId, moduleType)
  id UUID PK, userId UUID, moduleType varchar, completedItems int,
  totalItems int nullable, averageScore double, bestScore double,
  lastCompletedAt timestamp, status varchar
}

LearningActivityEntity {
  @Table("learning_activities")
  @UniqueConstraint(userId, referenceId, activityType)
  @Index(userId, occurredAt DESC)
  id UUID PK, userId UUID, activityType varchar, referenceId UUID,
  score double nullable, xpEarned int, metadata jsonb,
  occurredAt timestamp
}

UserStatisticsEntity {
  @Table("user_statistics")
  id UUID PK, userId UUID UNIQUE, averageScore7Days double,
  averageScore30Days double, averageScoreAllTime double,
  learningVelocity double, consistencyRate double,
  weakAreas jsonb, recommendations jsonb, progressTrend varchar,
  lastCalculatedAt timestamp
}
```

### 3.4 Migration Flyway V7

```sql
CREATE TABLE user_progress (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL UNIQUE REFERENCES users(id),
  total_lessons_completed INT NOT NULL DEFAULT 0,
  total_quizzes_completed INT NOT NULL DEFAULT 0,
  total_interviews_completed INT NOT NULL DEFAULT 0,
  total_corrections_completed INT NOT NULL DEFAULT 0,
  global_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
  current_streak INT NOT NULL DEFAULT 0,
  longest_streak INT NOT NULL DEFAULT 0,
  last_activity_at TIMESTAMP,
  level VARCHAR(20) NOT NULL DEFAULT 'BEGINNER',
  total_xp BIGINT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE module_progress (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL REFERENCES users(id),
  module_type VARCHAR(30) NOT NULL,
  completed_items INT NOT NULL DEFAULT 0,
  total_items INT,
  average_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
  best_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
  last_completed_at TIMESTAMP,
  status VARCHAR(20) NOT NULL DEFAULT 'NOT_STARTED',
  UNIQUE(user_id, module_type)
);

CREATE TABLE learning_activities (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL REFERENCES users(id),
  activity_type VARCHAR(30) NOT NULL,
  reference_id UUID NOT NULL,
  score DOUBLE PRECISION,
  xp_earned INT NOT NULL DEFAULT 0,
  metadata JSONB,
  occurred_at TIMESTAMP NOT NULL,
  UNIQUE(user_id, reference_id, activity_type)
);

CREATE TABLE user_statistics (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL UNIQUE REFERENCES users(id),
  average_score_7_days DOUBLE PRECISION NOT NULL DEFAULT 0.0,
  average_score_30_days DOUBLE PRECISION NOT NULL DEFAULT 0.0,
  average_score_all_time DOUBLE PRECISION NOT NULL DEFAULT 0.0,
  learning_velocity DOUBLE PRECISION NOT NULL DEFAULT 0.0,
  consistency_rate DOUBLE PRECISION NOT NULL DEFAULT 0.0,
  weak_areas JSONB DEFAULT '[]',
  recommendations JSONB DEFAULT '[]',
  progress_trend VARCHAR(20) NOT NULL DEFAULT 'STABLE',
  last_calculated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_learning_activities_user_date ON learning_activities(user_id, occurred_at DESC);
CREATE INDEX idx_learning_activities_reference ON learning_activities(user_id, reference_id, activity_type);
CREATE INDEX idx_module_progress_user ON module_progress(user_id);
CREATE INDEX idx_user_progress_level ON user_progress(level);
CREATE INDEX idx_user_progress_xp ON user_progress(total_xp DESC);
```

### 3.5 Jobs Schedulés

```java
@Scheduled(fixedRate = 15, timeUnit = MINUTES)
StatisticsRecalculationJob {
  // 1. Trouver users avec activité depuis lastCalculatedAt
  // 2. Pour chaque user: recalculer scores glissants, tendances, points faibles, recommandations
  // 3. Sauvegarder UserStatistics
}

@Scheduled(cron = "0 0 2 * * *")
StreakCalculationJob {
  // 1. Trouver users avec currentStreak > 0 ET lastActivityAt < aujourd'hui 00:00
  // 2. Reset currentStreak à 0
}
```

---

## 4. Logique Métier de Calcul

### 4.1 XP par activité

| Activité | Formule | XP max |
|----------|---------|--------|
| LESSON_COMPLETED | 50 (fixe) | 50 |
| QUIZ_COMPLETED | 30 + (score * 0.7) | ~100 |
| INTERVIEW_COMPLETED | 80 + (score * 1.2) | ~200 |
| CORRECTION_COMPLETED | 40 + (score * 0.5) | ~90 |

### 4.2 Niveaux

| Level | XP requis |
|-------|-----------|
| BEGINNER | 0 — 999 |
| INTERMEDIATE | 1 000 — 4 999 |
| ADVANCED | 5 000 — 14 999 |
| EXPERT | 15 000+ |

### 4.3 Global Score (temps réel)

Moyenne pondérée des dernières activités scorées :
- QUIZ: poids 1.0
- INTERVIEW: poids 1.5
- CORRECTION: poids 1.2
- LESSON: poids 0.5

### 4.4 Points faibles (batch)

Pour chaque ModuleType avec > 3 activités :
- averageScore < 60% → priority HIGH
- averageScore < 75% → priority MEDIUM

### 4.5 Recommandations (batch)

| Condition | Recommandation |
|-----------|---------------|
| Point faible identifié | REVIEW_LESSON ou RETRY_QUIZ sur le topic |
| Module inactif > 7 jours | Rappel de pratique |
| Score en déclin (7d < 30d - 5pts) | Revoir les fondamentaux |
| Streak en danger | Encouragement à pratiquer |

### 4.6 Tendance (batch)

```
averageScore7Days > averageScore30Days + 5 → IMPROVING
averageScore7Days < averageScore30Days - 5 → DECLINING
sinon → STABLE
```

---

## 5. DTOs

### 5.1 User-facing

```java
record UserProgressDto(UUID userId, int totalLessonsCompleted, int totalQuizzesCompleted,
  int totalInterviewsCompleted, int totalCorrectionsCompleted, double globalScore,
  int currentStreak, int longestStreak, ProgressLevel level, long totalXp,
  LocalDateTime lastActivityAt) {}

record ModuleProgressDto(ModuleType moduleType, int completedItems, Integer totalItems,
  double completionPercentage, double averageScore, double bestScore,
  ModuleStatus status, LocalDateTime lastCompletedAt) {}

record LearningActivityDto(ActivityType activityType, UUID referenceId, Double score,
  int xpEarned, Map<String, Object> metadata, LocalDateTime occurredAt) {}

record UserStatisticsDto(double averageScore7Days, double averageScore30Days,
  double averageScoreAllTime, double learningVelocity, double consistencyRate,
  Trend progressTrend, List<WeakAreaDto> weakAreas,
  List<RecommendationDto> recommendations) {}

record WeakAreaDto(ModuleType moduleType, String topic, double averageScore,
  Priority priority, String suggestedAction) {}

record RecommendationDto(RecommendationType type, UUID targetId, String reason,
  Priority priority) {}
```

### 5.2 Admin-facing

```java
record PlatformAnalyticsDto(long totalUsers, long activeUsersLast7Days,
  long activeUsersLast30Days, double averageGlobalScore, long totalActivities,
  Map<ModuleType, Double> averageScoreByModule) {}

record ActiveUsersDto(String period, long count,
  Map<LocalDate, Long> dailyBreakdown) {}

record ModuleAnalyticsDto(ModuleType moduleType, long totalCompletions,
  double averageScore, double completionRate,
  List<TopUserDto> topPerformers) {}

record TopUserDto(UUID userId, String displayName, long totalXp,
  double globalScore, ProgressLevel level) {}

record ActivityTrendDto(LocalDate from, LocalDate to,
  Map<LocalDate, Long> dailyCounts, Trend trend) {}

record AnalyticsQuery(LocalDate from, LocalDate to, ModuleType moduleType) {}
```

---

## 6. Événements

### 6.1 Consommés

| Événement | Topic Kafka | Source |
|-----------|-------------|--------|
| LessonCompletedEvent | lesson-events | BLOC 3 Learning |
| QuizCompletedEvent | quiz-events | BLOC 4 Quiz |
| InterviewCompletedEvent | interview-events | BLOC 6 Interview |
| TextCorrectedEvent | correction-events | BLOC 7 Correction |

### 6.2 Publié

| Événement | Topic Kafka | Consommateurs futurs |
|-----------|-------------|---------------------|
| ProgressUpdatedEvent | progress-events | Portfolio (BLOC Innovation 5), Notifications, Badges |

---

## 7. Tests

### 7.1 Domain Model Tests

```
UserProgressTest
  - should calculate level from XP correctly (each threshold)
  - should increment streak when activity on consecutive day
  - should reset streak when gap > 1 day
  - should update global score with weighted average
  - should track longest streak

ModuleProgressTest
  - should calculate completion percentage
  - should update average score incrementally
  - should transition NOT_STARTED → IN_PROGRESS on first completion
  - should update best score when new score is higher
```

### 7.2 Use Case Tests

```
UpdateProgressOnQuizCompletedUseCaseTest
  - should create UserProgress if first activity
  - should increment quiz counter
  - should calculate and add XP (30 + score * 0.7)
  - should update module progress average score
  - should insert learning activity
  - should publish ProgressUpdatedEvent
  - should be idempotent (skip duplicate referenceId)
  - should update streak correctly

UpdateProgressOnLessonCompletedUseCaseTest
  - should increment lesson counter
  - should award 50 XP for lesson completion
  - should update streak if first activity today
  - should level up when XP threshold crossed

UpdateProgressOnInterviewCompletedUseCaseTest
  - should increment interview counter
  - should calculate XP (80 + score * 1.2)
  - should update INTERVIEW module progress

RecalculateStatisticsUseCaseTest
  - should calculate 7-day average from recent activities
  - should calculate 30-day average
  - should identify weak areas below 60% as HIGH priority
  - should identify weak areas below 75% as MEDIUM priority
  - should generate recommendations for weak modules
  - should detect IMPROVING trend (7d > 30d + 5)
  - should detect DECLINING trend (7d < 30d - 5)
  - should calculate learning velocity (activities/day)
  - should calculate consistency rate (% days active)
```

### 7.3 Consumer Tests

```
ProgressEventConsumerTest
  - should deserialize and delegate LessonCompletedEvent
  - should deserialize and delegate QuizCompletedEvent
  - should deserialize and delegate InterviewCompletedEvent
  - should deserialize and delegate TextCorrectedEvent
  - should handle malformed events gracefully (no crash)
  - should be idempotent on duplicate events
```

### 7.4 Integration Tests

```
ProgressIntegrationTest
  - should persist progress and retrieve via GET /api/progress/me
  - should consume Kafka event and update progress end-to-end
  - should return paginated activity history
  - should calculate statistics after batch job execution
```

---

## 8. Améliorations par rapport au bloc original

| Aspect | Version originale | Version améliorée |
|--------|------------------|-------------------|
| Architecture | Non spécifiée | CQRS léger (write/read séparés) |
| Mise à jour | Non spécifiée | Hybride temps réel + batch 15min |
| Idempotence | Non mentionnée | Contrainte unique sur (userId, referenceId, activityType) |
| XP System | Non présent | Formule par activité + niveaux |
| Streaks | Non présent | Calcul quotidien + longest streak |
| Tendances | Non présent | IMPROVING/STABLE/DECLINING via scores glissants |
| Recommandations | Mentionnées sans détail | Algorithme précis basé sur points faibles + inactivité |
| Admin analytics | "statistiques admin" vague | 5 endpoints dédiés avec métriques précises |
| ModuleType | Progress + LearningActivity | Extensible avec SHADOW_DAY, CULTURAL, CODE_REVIEW, REAL_CONTENT |
| Scalabilité | Non considérée | Projections dénormalisées, index optimisés, jobs batch |
| Tolérance pannes | Non mentionnée | Consumer idempotent, events rejouables |

---

## 9. Dépendances avec les autres blocs

| Bloc | Relation |
|------|----------|
| BLOC 3 (Learning) | Consomme LessonCompletedEvent |
| BLOC 4 (Quiz) | Consomme QuizCompletedEvent |
| BLOC 6 (Interview) | Consomme InterviewCompletedEvent |
| BLOC 7 (Correction) | Consomme TextCorrectedEvent |
| Innovation 1 (Shadow Day) | Futur: consommera ShadowDayCompletedEvent |
| Innovation 3 (Cultural) | Futur: consommera CulturalScoreUpdatedEvent |
| Innovation 5 (Portfolio) | Futur: consommera ProgressUpdatedEvent pour badges |
