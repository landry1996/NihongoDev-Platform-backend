# BLOC 8 — Progress & Analytics Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement user progress tracking and analytics with real-time counters (via Kafka consumers) and batch-calculated statistics (via scheduled jobs), following CQRS-light pattern.

**Architecture:** Hexagonal (ports/adapters) with CQRS-light — write side consumes domain events from Kafka to update counters and log activities; read side serves pre-calculated statistics via REST. Scheduled jobs recalculate complex analytics every 15 minutes.

**Tech Stack:** Java 21, Spring Boot 3.3.5, PostgreSQL (Flyway), Apache Kafka (consumer), Spring Scheduling, JUnit 5 + Mockito + AssertJ

---

## File Structure

### Domain Layer (`src/main/java/com/nihongodev/platform/domain/`)

| File | Responsibility |
|------|---------------|
| `model/UserProgress.java` | Aggregate — global progress counters, XP, level, streaks |
| `model/ModuleProgress.java` | Per-module progress (score, completions, status) |
| `model/LearningActivity.java` | Immutable activity log entry |
| `model/UserStatistics.java` | Read-side projection with calculated analytics |
| `model/ProgressLevel.java` | Enum: BEGINNER, INTERMEDIATE, ADVANCED, EXPERT |
| `model/ModuleType.java` | Enum: LESSON, QUIZ, INTERVIEW, CORRECTION, + future types |
| `model/ModuleStatus.java` | Enum: NOT_STARTED, IN_PROGRESS, COMPLETED |
| `model/ActivityType.java` | Enum: LESSON_COMPLETED, QUIZ_COMPLETED, etc. |
| `model/Trend.java` | Enum: IMPROVING, STABLE, DECLINING |
| `model/WeakArea.java` | Value object for identified weak areas |
| `model/Recommendation.java` | Value object for learning recommendations |
| `model/RecommendationType.java` | Enum: REVIEW_LESSON, RETRY_QUIZ, etc. |
| `model/Priority.java` | Enum: HIGH, MEDIUM, LOW |
| `event/ProgressUpdatedEvent.java` | Domain event published after progress update |

### Application Layer (`src/main/java/com/nihongodev/platform/application/`)

| File | Responsibility |
|------|---------------|
| `port/in/UpdateProgressOnLessonCompletedPort.java` | Port IN for lesson completion |
| `port/in/UpdateProgressOnQuizCompletedPort.java` | Port IN for quiz completion |
| `port/in/UpdateProgressOnInterviewCompletedPort.java` | Port IN for interview completion |
| `port/in/UpdateProgressOnCorrectionCompletedPort.java` | Port IN for correction completion |
| `port/in/GetUserProgressPort.java` | Port IN for reading user progress |
| `port/in/GetModuleProgressPort.java` | Port IN for reading module progress |
| `port/in/GetUserActivityHistoryPort.java` | Port IN for reading activity history |
| `port/in/GetUserStatisticsPort.java` | Port IN for reading user statistics |
| `port/in/GetPlatformAnalyticsPort.java` | Port IN for admin analytics |
| `port/in/RecalculateStatisticsPort.java` | Port IN for batch recalculation |
| `port/out/ProgressRepositoryPort.java` | Port OUT for UserProgress persistence |
| `port/out/ModuleProgressRepositoryPort.java` | Port OUT for ModuleProgress persistence |
| `port/out/LearningActivityRepositoryPort.java` | Port OUT for LearningActivity persistence |
| `port/out/StatisticsRepositoryPort.java` | Port OUT for UserStatistics persistence |
| `port/out/AnalyticsQueryPort.java` | Port OUT for admin aggregate queries |
| `usecase/UpdateProgressOnLessonCompletedUseCase.java` | Handles lesson event → updates progress |
| `usecase/UpdateProgressOnQuizCompletedUseCase.java` | Handles quiz event → updates progress |
| `usecase/UpdateProgressOnInterviewCompletedUseCase.java` | Handles interview event → updates progress |
| `usecase/UpdateProgressOnCorrectionCompletedUseCase.java` | Handles correction event → updates progress |
| `usecase/GetUserProgressUseCase.java` | Returns user's global progress |
| `usecase/GetModuleProgressUseCase.java` | Returns module-level progress |
| `usecase/GetUserActivityHistoryUseCase.java` | Returns paginated activity history |
| `usecase/GetUserStatisticsUseCase.java` | Returns calculated statistics |
| `usecase/GetPlatformAnalyticsUseCase.java` | Returns admin dashboard data |
| `usecase/RecalculateStatisticsUseCase.java` | Batch job logic for stats recalculation |
| `dto/UserProgressDto.java` | DTO for user progress response |
| `dto/ModuleProgressDto.java` | DTO for module progress response |
| `dto/LearningActivityDto.java` | DTO for activity entry |
| `dto/UserStatisticsDto.java` | DTO for user statistics |
| `dto/WeakAreaDto.java` | DTO for weak area |
| `dto/RecommendationDto.java` | DTO for recommendation |
| `dto/PlatformAnalyticsDto.java` | DTO for admin overview |
| `dto/TopUserDto.java` | DTO for leaderboard entry |

### Infrastructure Layer (`src/main/java/com/nihongodev/platform/infrastructure/`)

| File | Responsibility |
|------|---------------|
| `persistence/entity/UserProgressEntity.java` | JPA entity for user_progress table |
| `persistence/entity/ModuleProgressEntity.java` | JPA entity for module_progress table |
| `persistence/entity/LearningActivityEntity.java` | JPA entity for learning_activities table |
| `persistence/entity/UserStatisticsEntity.java` | JPA entity for user_statistics table |
| `persistence/repository/JpaUserProgressRepository.java` | Spring Data JPA interface |
| `persistence/repository/JpaModuleProgressRepository.java` | Spring Data JPA interface |
| `persistence/repository/JpaLearningActivityRepository.java` | Spring Data JPA interface |
| `persistence/repository/JpaUserStatisticsRepository.java` | Spring Data JPA interface |
| `persistence/mapper/UserProgressPersistenceMapper.java` | Entity ↔ Domain mapper |
| `persistence/mapper/ModuleProgressPersistenceMapper.java` | Entity ↔ Domain mapper |
| `persistence/mapper/LearningActivityPersistenceMapper.java` | Entity ↔ Domain mapper |
| `persistence/mapper/UserStatisticsPersistenceMapper.java` | Entity ↔ Domain mapper |
| `persistence/adapter/ProgressRepositoryAdapter.java` | Implements ProgressRepositoryPort |
| `persistence/adapter/ModuleProgressRepositoryAdapter.java` | Implements ModuleProgressRepositoryPort |
| `persistence/adapter/LearningActivityRepositoryAdapter.java` | Implements LearningActivityRepositoryPort |
| `persistence/adapter/StatisticsRepositoryAdapter.java` | Implements StatisticsRepositoryPort |
| `persistence/adapter/AnalyticsQueryAdapter.java` | Implements AnalyticsQueryPort |
| `kafka/ProgressEventConsumer.java` | Kafka consumer for domain events |
| `web/controller/ProgressController.java` | REST endpoints for user progress |
| `web/controller/AnalyticsController.java` | REST endpoints for admin analytics |
| `scheduling/StatisticsRecalculationJob.java` | @Scheduled job for stats |
| `scheduling/StreakCalculationJob.java` | @Scheduled job for streaks |

### Database Migration

| File | Responsibility |
|------|---------------|
| `src/main/resources/db/migration/V7__progress_analytics.sql` | Schema for progress tables |

### Tests (`src/test/java/com/nihongodev/platform/`)

| File | Responsibility |
|------|---------------|
| `domain/model/UserProgressTest.java` | Domain model behavior tests |
| `domain/model/ModuleProgressTest.java` | Module progress behavior tests |
| `application/usecase/UpdateProgressOnQuizCompletedUseCaseTest.java` | Write use case tests |
| `application/usecase/UpdateProgressOnLessonCompletedUseCaseTest.java` | Write use case tests |
| `application/usecase/RecalculateStatisticsUseCaseTest.java` | Batch job logic tests |
| `application/usecase/GetUserProgressUseCaseTest.java` | Read use case tests |
| `infrastructure/kafka/ProgressEventConsumerTest.java` | Kafka consumer tests |

---

## Task 1: Domain Enums

**Files:**
- Create: `src/main/java/com/nihongodev/platform/domain/model/ProgressLevel.java`
- Create: `src/main/java/com/nihongodev/platform/domain/model/ModuleType.java`
- Create: `src/main/java/com/nihongodev/platform/domain/model/ModuleStatus.java`
- Create: `src/main/java/com/nihongodev/platform/domain/model/ActivityType.java`
- Create: `src/main/java/com/nihongodev/platform/domain/model/Trend.java`
- Create: `src/main/java/com/nihongodev/platform/domain/model/RecommendationType.java`
- Create: `src/main/java/com/nihongodev/platform/domain/model/Priority.java`

- [ ] **Step 1: Create ProgressLevel enum**

```java
package com.nihongodev.platform.domain.model;

public enum ProgressLevel {
    BEGINNER(0, 999),
    INTERMEDIATE(1000, 4999),
    ADVANCED(5000, 14999),
    EXPERT(15000, Long.MAX_VALUE);

    private final long minXp;
    private final long maxXp;

    ProgressLevel(long minXp, long maxXp) {
        this.minXp = minXp;
        this.maxXp = maxXp;
    }

    public long getMinXp() { return minXp; }
    public long getMaxXp() { return maxXp; }

    public static ProgressLevel fromXp(long xp) {
        for (ProgressLevel level : values()) {
            if (xp >= level.minXp && xp <= level.maxXp) return level;
        }
        return EXPERT;
    }
}
```

- [ ] **Step 2: Create ModuleType enum**

```java
package com.nihongodev.platform.domain.model;

public enum ModuleType {
    LESSON,
    QUIZ,
    INTERVIEW,
    CORRECTION,
    SHADOW_DAY,
    CULTURAL,
    CODE_REVIEW,
    REAL_CONTENT
}
```

- [ ] **Step 3: Create ModuleStatus enum**

```java
package com.nihongodev.platform.domain.model;

public enum ModuleStatus {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED
}
```

- [ ] **Step 4: Create ActivityType enum**

```java
package com.nihongodev.platform.domain.model;

public enum ActivityType {
    LESSON_COMPLETED(50, 0.5),
    QUIZ_COMPLETED(30, 1.0),
    INTERVIEW_COMPLETED(80, 1.5),
    CORRECTION_COMPLETED(40, 1.2);

    private final int baseXp;
    private final double scoreWeight;

    ActivityType(int baseXp, double scoreWeight) {
        this.baseXp = baseXp;
        this.scoreWeight = scoreWeight;
    }

    public int getBaseXp() { return baseXp; }
    public double getScoreWeight() { return scoreWeight; }

    public int calculateXp(double score) {
        if (this == LESSON_COMPLETED) return baseXp;
        return (int) (baseXp + (score * (scoreWeight)));
    }

    public static ActivityType fromModuleType(ModuleType moduleType) {
        return switch (moduleType) {
            case LESSON -> LESSON_COMPLETED;
            case QUIZ -> QUIZ_COMPLETED;
            case INTERVIEW -> INTERVIEW_COMPLETED;
            case CORRECTION -> CORRECTION_COMPLETED;
            default -> throw new IllegalArgumentException("No activity type for module: " + moduleType);
        };
    }
}
```

- [ ] **Step 5: Create Trend enum**

```java
package com.nihongodev.platform.domain.model;

public enum Trend {
    IMPROVING,
    STABLE,
    DECLINING;

    public static Trend calculate(double score7Days, double score30Days) {
        if (score7Days > score30Days + 5) return IMPROVING;
        if (score7Days < score30Days - 5) return DECLINING;
        return STABLE;
    }
}
```

- [ ] **Step 6: Create RecommendationType enum**

```java
package com.nihongodev.platform.domain.model;

public enum RecommendationType {
    REVIEW_LESSON,
    RETRY_QUIZ,
    PRACTICE_INTERVIEW,
    PRACTICE_CORRECTION,
    READ_CONTENT
}
```

- [ ] **Step 7: Create Priority enum**

```java
package com.nihongodev.platform.domain.model;

public enum Priority {
    HIGH,
    MEDIUM,
    LOW
}
```

- [ ] **Step 8: Commit**

```bash
git add src/main/java/com/nihongodev/platform/domain/model/ProgressLevel.java \
        src/main/java/com/nihongodev/platform/domain/model/ModuleType.java \
        src/main/java/com/nihongodev/platform/domain/model/ModuleStatus.java \
        src/main/java/com/nihongodev/platform/domain/model/ActivityType.java \
        src/main/java/com/nihongodev/platform/domain/model/Trend.java \
        src/main/java/com/nihongodev/platform/domain/model/RecommendationType.java \
        src/main/java/com/nihongodev/platform/domain/model/Priority.java
git commit -m "feat(progress): add domain enums for Progress & Analytics module"
```

---

## Task 2: Domain Models — Value Objects

**Files:**
- Create: `src/main/java/com/nihongodev/platform/domain/model/WeakArea.java`
- Create: `src/main/java/com/nihongodev/platform/domain/model/Recommendation.java`

- [ ] **Step 1: Create WeakArea value object**

```java
package com.nihongodev.platform.domain.model;

import java.util.UUID;

public class WeakArea {
    private ModuleType moduleType;
    private String topic;
    private double averageScore;
    private Priority priority;

    public WeakArea() {}

    public WeakArea(ModuleType moduleType, String topic, double averageScore, Priority priority) {
        this.moduleType = moduleType;
        this.topic = topic;
        this.averageScore = averageScore;
        this.priority = priority;
    }

    public static WeakArea identify(ModuleType moduleType, String topic, double averageScore) {
        Priority priority;
        if (averageScore < 60) {
            priority = Priority.HIGH;
        } else if (averageScore < 75) {
            priority = Priority.MEDIUM;
        } else {
            priority = Priority.LOW;
        }
        return new WeakArea(moduleType, topic, averageScore, priority);
    }

    public ModuleType getModuleType() { return moduleType; }
    public void setModuleType(ModuleType moduleType) { this.moduleType = moduleType; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public double getAverageScore() { return averageScore; }
    public void setAverageScore(double averageScore) { this.averageScore = averageScore; }
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
}
```

- [ ] **Step 2: Create Recommendation value object**

```java
package com.nihongodev.platform.domain.model;

import java.util.UUID;

public class Recommendation {
    private RecommendationType type;
    private UUID targetId;
    private String reason;
    private Priority priority;

    public Recommendation() {}

    public Recommendation(RecommendationType type, UUID targetId, String reason, Priority priority) {
        this.type = type;
        this.targetId = targetId;
        this.reason = reason;
        this.priority = priority;
    }

    public static Recommendation fromWeakArea(WeakArea weakArea) {
        RecommendationType type = switch (weakArea.getModuleType()) {
            case LESSON -> RecommendationType.REVIEW_LESSON;
            case QUIZ -> RecommendationType.RETRY_QUIZ;
            case INTERVIEW -> RecommendationType.PRACTICE_INTERVIEW;
            case CORRECTION -> RecommendationType.PRACTICE_CORRECTION;
            default -> RecommendationType.READ_CONTENT;
        };
        return new Recommendation(type, null, "Low score in " + weakArea.getTopic(), weakArea.getPriority());
    }

    public RecommendationType getType() { return type; }
    public void setType(RecommendationType type) { this.type = type; }
    public UUID getTargetId() { return targetId; }
    public void setTargetId(UUID targetId) { this.targetId = targetId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
}
```

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/nihongodev/platform/domain/model/WeakArea.java \
        src/main/java/com/nihongodev/platform/domain/model/Recommendation.java
git commit -m "feat(progress): add WeakArea and Recommendation value objects"
```

---

## Task 3: Domain Models — UserProgress Aggregate (with TDD)

**Files:**
- Create: `src/main/java/com/nihongodev/platform/domain/model/UserProgress.java`
- Create: `src/test/java/com/nihongodev/platform/domain/model/UserProgressTest.java`

- [ ] **Step 1: Write the failing test**

```java
package com.nihongodev.platform.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserProgress")
class UserProgressTest {

    private UserProgress progress;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        progress = UserProgress.initialize(userId);
    }

    @Test
    @DisplayName("should initialize with BEGINNER level and zero counters")
    void shouldInitialize() {
        assertThat(progress.getUserId()).isEqualTo(userId);
        assertThat(progress.getLevel()).isEqualTo(ProgressLevel.BEGINNER);
        assertThat(progress.getTotalXp()).isZero();
        assertThat(progress.getTotalLessonsCompleted()).isZero();
        assertThat(progress.getTotalQuizzesCompleted()).isZero();
        assertThat(progress.getTotalInterviewsCompleted()).isZero();
        assertThat(progress.getTotalCorrectionsCompleted()).isZero();
        assertThat(progress.getCurrentStreak()).isZero();
        assertThat(progress.getLongestStreak()).isZero();
        assertThat(progress.getGlobalScore()).isZero();
    }

    @Test
    @DisplayName("should increment lesson counter and add base XP")
    void shouldRecordLessonCompleted() {
        progress.recordActivity(ActivityType.LESSON_COMPLETED, 0);

        assertThat(progress.getTotalLessonsCompleted()).isEqualTo(1);
        assertThat(progress.getTotalXp()).isEqualTo(50);
        assertThat(progress.getLevel()).isEqualTo(ProgressLevel.BEGINNER);
    }

    @Test
    @DisplayName("should increment quiz counter and calculate XP from score")
    void shouldRecordQuizCompleted() {
        progress.recordActivity(ActivityType.QUIZ_COMPLETED, 85.0);

        assertThat(progress.getTotalQuizzesCompleted()).isEqualTo(1);
        assertThat(progress.getTotalXp()).isEqualTo(115); // 30 + (85 * 1.0)
    }

    @Test
    @DisplayName("should increment interview counter and calculate XP")
    void shouldRecordInterviewCompleted() {
        progress.recordActivity(ActivityType.INTERVIEW_COMPLETED, 70.0);

        assertThat(progress.getTotalInterviewsCompleted()).isEqualTo(1);
        assertThat(progress.getTotalXp()).isEqualTo(164); // 80 + (70 * 1.2)
    }

    @Test
    @DisplayName("should increment correction counter and calculate XP")
    void shouldRecordCorrectionCompleted() {
        progress.recordActivity(ActivityType.CORRECTION_COMPLETED, 80.0);

        assertThat(progress.getTotalCorrectionsCompleted()).isEqualTo(1);
        assertThat(progress.getTotalXp()).isEqualTo(120); // 40 + (80 * 1.2)
    }

    @Test
    @DisplayName("should level up to INTERMEDIATE at 1000 XP")
    void shouldLevelUpToIntermediate() {
        for (int i = 0; i < 20; i++) {
            progress.recordActivity(ActivityType.LESSON_COMPLETED, 0); // 20 * 50 = 1000 XP
        }

        assertThat(progress.getTotalXp()).isEqualTo(1000);
        assertThat(progress.getLevel()).isEqualTo(ProgressLevel.INTERMEDIATE);
    }

    @Test
    @DisplayName("should level up to ADVANCED at 5000 XP")
    void shouldLevelUpToAdvanced() {
        for (int i = 0; i < 100; i++) {
            progress.recordActivity(ActivityType.LESSON_COMPLETED, 0); // 100 * 50 = 5000 XP
        }

        assertThat(progress.getTotalXp()).isEqualTo(5000);
        assertThat(progress.getLevel()).isEqualTo(ProgressLevel.ADVANCED);
    }

    @Test
    @DisplayName("should update global score with weighted average")
    void shouldUpdateGlobalScore() {
        progress.recordActivity(ActivityType.QUIZ_COMPLETED, 80.0);
        progress.updateGlobalScore(ActivityType.QUIZ_COMPLETED, 80.0);

        assertThat(progress.getGlobalScore()).isGreaterThan(0);
    }

    @Test
    @DisplayName("should increment streak on first activity of the day")
    void shouldIncrementStreak() {
        progress.updateStreak(LocalDateTime.now());

        assertThat(progress.getCurrentStreak()).isEqualTo(1);
        assertThat(progress.getLongestStreak()).isEqualTo(1);
    }

    @Test
    @DisplayName("should not increment streak for second activity same day")
    void shouldNotIncrementStreakSameDay() {
        LocalDateTime now = LocalDateTime.now();
        progress.updateStreak(now);
        progress.updateStreak(now);

        assertThat(progress.getCurrentStreak()).isEqualTo(1);
    }

    @Test
    @DisplayName("should track longest streak")
    void shouldTrackLongestStreak() {
        LocalDateTime day1 = LocalDateTime.of(2026, 5, 1, 10, 0);
        LocalDateTime day2 = LocalDateTime.of(2026, 5, 2, 10, 0);
        LocalDateTime day3 = LocalDateTime.of(2026, 5, 3, 10, 0);

        progress.updateStreak(day1);
        progress.updateStreak(day2);
        progress.updateStreak(day3);

        assertThat(progress.getCurrentStreak()).isEqualTo(3);
        assertThat(progress.getLongestStreak()).isEqualTo(3);
    }

    @Test
    @DisplayName("should reset current streak after gap but keep longest")
    void shouldResetStreakAfterGap() {
        LocalDateTime day1 = LocalDateTime.of(2026, 5, 1, 10, 0);
        LocalDateTime day2 = LocalDateTime.of(2026, 5, 2, 10, 0);
        LocalDateTime day4 = LocalDateTime.of(2026, 5, 4, 10, 0);

        progress.updateStreak(day1);
        progress.updateStreak(day2);
        progress.setLastActivityAt(day2);
        progress.updateStreak(day4);

        assertThat(progress.getCurrentStreak()).isEqualTo(1);
        assertThat(progress.getLongestStreak()).isEqualTo(2);
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn test -pl . -Dtest=UserProgressTest -f pom.xml`
Expected: FAIL — `UserProgress` class does not exist

- [ ] **Step 3: Implement UserProgress domain model**

```java
package com.nihongodev.platform.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class UserProgress {

    private UUID id;
    private UUID userId;
    private int totalLessonsCompleted;
    private int totalQuizzesCompleted;
    private int totalInterviewsCompleted;
    private int totalCorrectionsCompleted;
    private double globalScore;
    private int currentStreak;
    private int longestStreak;
    private LocalDateTime lastActivityAt;
    private ProgressLevel level;
    private long totalXp;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private int scoredActivitiesCount;
    private double weightedScoreSum;
    private double weightSum;

    public static UserProgress initialize(UUID userId) {
        UserProgress progress = new UserProgress();
        progress.id = UUID.randomUUID();
        progress.userId = userId;
        progress.level = ProgressLevel.BEGINNER;
        progress.totalXp = 0;
        progress.totalLessonsCompleted = 0;
        progress.totalQuizzesCompleted = 0;
        progress.totalInterviewsCompleted = 0;
        progress.totalCorrectionsCompleted = 0;
        progress.globalScore = 0;
        progress.currentStreak = 0;
        progress.longestStreak = 0;
        progress.scoredActivitiesCount = 0;
        progress.weightedScoreSum = 0;
        progress.weightSum = 0;
        progress.createdAt = LocalDateTime.now();
        progress.updatedAt = LocalDateTime.now();
        return progress;
    }

    public void recordActivity(ActivityType activityType, double score) {
        switch (activityType) {
            case LESSON_COMPLETED -> totalLessonsCompleted++;
            case QUIZ_COMPLETED -> totalQuizzesCompleted++;
            case INTERVIEW_COMPLETED -> totalInterviewsCompleted++;
            case CORRECTION_COMPLETED -> totalCorrectionsCompleted++;
        }
        int xpEarned = activityType.calculateXp(score);
        totalXp += xpEarned;
        level = ProgressLevel.fromXp(totalXp);
        updatedAt = LocalDateTime.now();
    }

    public void updateGlobalScore(ActivityType activityType, double score) {
        double weight = activityType.getScoreWeight();
        weightedScoreSum += score * weight;
        weightSum += weight;
        scoredActivitiesCount++;
        if (weightSum > 0) {
            globalScore = weightedScoreSum / weightSum;
        }
    }

    public void updateStreak(LocalDateTime activityTime) {
        LocalDate activityDate = activityTime.toLocalDate();

        if (lastActivityAt == null) {
            currentStreak = 1;
            longestStreak = 1;
            lastActivityAt = activityTime;
            return;
        }

        LocalDate lastDate = lastActivityAt.toLocalDate();
        if (activityDate.equals(lastDate)) {
            return;
        }

        if (activityDate.equals(lastDate.plusDays(1))) {
            currentStreak++;
        } else {
            currentStreak = 1;
        }

        if (currentStreak > longestStreak) {
            longestStreak = currentStreak;
        }
        lastActivityAt = activityTime;
    }

    public void resetStreak() {
        currentStreak = 0;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public int getTotalLessonsCompleted() { return totalLessonsCompleted; }
    public void setTotalLessonsCompleted(int totalLessonsCompleted) { this.totalLessonsCompleted = totalLessonsCompleted; }
    public int getTotalQuizzesCompleted() { return totalQuizzesCompleted; }
    public void setTotalQuizzesCompleted(int totalQuizzesCompleted) { this.totalQuizzesCompleted = totalQuizzesCompleted; }
    public int getTotalInterviewsCompleted() { return totalInterviewsCompleted; }
    public void setTotalInterviewsCompleted(int totalInterviewsCompleted) { this.totalInterviewsCompleted = totalInterviewsCompleted; }
    public int getTotalCorrectionsCompleted() { return totalCorrectionsCompleted; }
    public void setTotalCorrectionsCompleted(int totalCorrectionsCompleted) { this.totalCorrectionsCompleted = totalCorrectionsCompleted; }
    public double getGlobalScore() { return globalScore; }
    public void setGlobalScore(double globalScore) { this.globalScore = globalScore; }
    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }
    public int getLongestStreak() { return longestStreak; }
    public void setLongestStreak(int longestStreak) { this.longestStreak = longestStreak; }
    public LocalDateTime getLastActivityAt() { return lastActivityAt; }
    public void setLastActivityAt(LocalDateTime lastActivityAt) { this.lastActivityAt = lastActivityAt; }
    public ProgressLevel getLevel() { return level; }
    public void setLevel(ProgressLevel level) { this.level = level; }
    public long getTotalXp() { return totalXp; }
    public void setTotalXp(long totalXp) { this.totalXp = totalXp; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public int getScoredActivitiesCount() { return scoredActivitiesCount; }
    public void setScoredActivitiesCount(int scoredActivitiesCount) { this.scoredActivitiesCount = scoredActivitiesCount; }
    public double getWeightedScoreSum() { return weightedScoreSum; }
    public void setWeightedScoreSum(double weightedScoreSum) { this.weightedScoreSum = weightedScoreSum; }
    public double getWeightSum() { return weightSum; }
    public void setWeightSum(double weightSum) { this.weightSum = weightSum; }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `mvn test -pl . -Dtest=UserProgressTest -f pom.xml`
Expected: ALL PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/nihongodev/platform/domain/model/UserProgress.java \
        src/test/java/com/nihongodev/platform/domain/model/UserProgressTest.java
git commit -m "feat(progress): add UserProgress aggregate with XP, level, and streak logic"
```

---

## Task 4: Domain Models — ModuleProgress and LearningActivity (with TDD)

**Files:**
- Create: `src/main/java/com/nihongodev/platform/domain/model/ModuleProgress.java`
- Create: `src/main/java/com/nihongodev/platform/domain/model/LearningActivity.java`
- Create: `src/test/java/com/nihongodev/platform/domain/model/ModuleProgressTest.java`

- [ ] **Step 1: Write the failing test for ModuleProgress**

```java
package com.nihongodev.platform.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ModuleProgress")
class ModuleProgressTest {

    private ModuleProgress moduleProgress;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        moduleProgress = ModuleProgress.initialize(userId, ModuleType.QUIZ);
    }

    @Test
    @DisplayName("should initialize with NOT_STARTED status")
    void shouldInitialize() {
        assertThat(moduleProgress.getUserId()).isEqualTo(userId);
        assertThat(moduleProgress.getModuleType()).isEqualTo(ModuleType.QUIZ);
        assertThat(moduleProgress.getStatus()).isEqualTo(ModuleStatus.NOT_STARTED);
        assertThat(moduleProgress.getCompletedItems()).isZero();
        assertThat(moduleProgress.getAverageScore()).isZero();
        assertThat(moduleProgress.getBestScore()).isZero();
    }

    @Test
    @DisplayName("should transition to IN_PROGRESS on first completion")
    void shouldTransitionToInProgress() {
        moduleProgress.recordCompletion(80.0);

        assertThat(moduleProgress.getStatus()).isEqualTo(ModuleStatus.IN_PROGRESS);
        assertThat(moduleProgress.getCompletedItems()).isEqualTo(1);
    }

    @Test
    @DisplayName("should calculate average score incrementally")
    void shouldCalculateAverageScore() {
        moduleProgress.recordCompletion(80.0);
        moduleProgress.recordCompletion(60.0);

        assertThat(moduleProgress.getAverageScore()).isEqualTo(70.0);
        assertThat(moduleProgress.getCompletedItems()).isEqualTo(2);
    }

    @Test
    @DisplayName("should update best score when new score is higher")
    void shouldUpdateBestScore() {
        moduleProgress.recordCompletion(60.0);
        moduleProgress.recordCompletion(90.0);
        moduleProgress.recordCompletion(70.0);

        assertThat(moduleProgress.getBestScore()).isEqualTo(90.0);
    }

    @Test
    @DisplayName("should calculate completion percentage when totalItems set")
    void shouldCalculateCompletionPercentage() {
        moduleProgress.setTotalItems(10);
        moduleProgress.recordCompletion(80.0);
        moduleProgress.recordCompletion(70.0);

        assertThat(moduleProgress.getCompletionPercentage()).isEqualTo(20.0);
    }

    @Test
    @DisplayName("should return 0 completion percentage when totalItems is null")
    void shouldReturnZeroWhenNoTotalItems() {
        moduleProgress.recordCompletion(80.0);

        assertThat(moduleProgress.getCompletionPercentage()).isZero();
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn test -pl . -Dtest=ModuleProgressTest -f pom.xml`
Expected: FAIL — `ModuleProgress` class does not exist

- [ ] **Step 3: Implement ModuleProgress**

```java
package com.nihongodev.platform.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class ModuleProgress {

    private UUID id;
    private UUID userId;
    private ModuleType moduleType;
    private int completedItems;
    private Integer totalItems;
    private double averageScore;
    private double bestScore;
    private LocalDateTime lastCompletedAt;
    private ModuleStatus status;

    public static ModuleProgress initialize(UUID userId, ModuleType moduleType) {
        ModuleProgress mp = new ModuleProgress();
        mp.id = UUID.randomUUID();
        mp.userId = userId;
        mp.moduleType = moduleType;
        mp.completedItems = 0;
        mp.averageScore = 0;
        mp.bestScore = 0;
        mp.status = ModuleStatus.NOT_STARTED;
        return mp;
    }

    public void recordCompletion(double score) {
        if (status == ModuleStatus.NOT_STARTED) {
            status = ModuleStatus.IN_PROGRESS;
        }
        double totalScore = averageScore * completedItems;
        completedItems++;
        averageScore = (totalScore + score) / completedItems;
        if (score > bestScore) {
            bestScore = score;
        }
        lastCompletedAt = LocalDateTime.now();
    }

    public double getCompletionPercentage() {
        if (totalItems == null || totalItems == 0) return 0;
        return ((double) completedItems / totalItems) * 100;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public ModuleType getModuleType() { return moduleType; }
    public void setModuleType(ModuleType moduleType) { this.moduleType = moduleType; }
    public int getCompletedItems() { return completedItems; }
    public void setCompletedItems(int completedItems) { this.completedItems = completedItems; }
    public Integer getTotalItems() { return totalItems; }
    public void setTotalItems(Integer totalItems) { this.totalItems = totalItems; }
    public double getAverageScore() { return averageScore; }
    public void setAverageScore(double averageScore) { this.averageScore = averageScore; }
    public double getBestScore() { return bestScore; }
    public void setBestScore(double bestScore) { this.bestScore = bestScore; }
    public LocalDateTime getLastCompletedAt() { return lastCompletedAt; }
    public void setLastCompletedAt(LocalDateTime lastCompletedAt) { this.lastCompletedAt = lastCompletedAt; }
    public ModuleStatus getStatus() { return status; }
    public void setStatus(ModuleStatus status) { this.status = status; }
}
```

- [ ] **Step 4: Implement LearningActivity**

```java
package com.nihongodev.platform.domain.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LearningActivity {

    private UUID id;
    private UUID userId;
    private ActivityType activityType;
    private UUID referenceId;
    private Double score;
    private int xpEarned;
    private Map<String, Object> metadata;
    private LocalDateTime occurredAt;

    public static LearningActivity create(UUID userId, ActivityType activityType,
                                          UUID referenceId, Double score, int xpEarned) {
        LearningActivity activity = new LearningActivity();
        activity.id = UUID.randomUUID();
        activity.userId = userId;
        activity.activityType = activityType;
        activity.referenceId = referenceId;
        activity.score = score;
        activity.xpEarned = xpEarned;
        activity.metadata = new HashMap<>();
        activity.occurredAt = LocalDateTime.now();
        return activity;
    }

    public void addMetadata(String key, Object value) {
        if (metadata == null) metadata = new HashMap<>();
        metadata.put(key, value);
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public ActivityType getActivityType() { return activityType; }
    public void setActivityType(ActivityType activityType) { this.activityType = activityType; }
    public UUID getReferenceId() { return referenceId; }
    public void setReferenceId(UUID referenceId) { this.referenceId = referenceId; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    public int getXpEarned() { return xpEarned; }
    public void setXpEarned(int xpEarned) { this.xpEarned = xpEarned; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }
}
```

- [ ] **Step 5: Run tests to verify they pass**

Run: `mvn test -pl . -Dtest=ModuleProgressTest -f pom.xml`
Expected: ALL PASS

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/nihongodev/platform/domain/model/ModuleProgress.java \
        src/main/java/com/nihongodev/platform/domain/model/LearningActivity.java \
        src/test/java/com/nihongodev/platform/domain/model/ModuleProgressTest.java
git commit -m "feat(progress): add ModuleProgress and LearningActivity domain models"
```

---

## Task 5: Domain Model — UserStatistics and ProgressUpdatedEvent

**Files:**
- Create: `src/main/java/com/nihongodev/platform/domain/model/UserStatistics.java`
- Create: `src/main/java/com/nihongodev/platform/domain/event/ProgressUpdatedEvent.java`

- [ ] **Step 1: Create UserStatistics domain model**

```java
package com.nihongodev.platform.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserStatistics {

    private UUID id;
    private UUID userId;
    private double averageScore7Days;
    private double averageScore30Days;
    private double averageScoreAllTime;
    private double learningVelocity;
    private double consistencyRate;
    private List<WeakArea> weakAreas;
    private List<Recommendation> recommendations;
    private Trend progressTrend;
    private LocalDateTime lastCalculatedAt;

    public static UserStatistics initialize(UUID userId) {
        UserStatistics stats = new UserStatistics();
        stats.id = UUID.randomUUID();
        stats.userId = userId;
        stats.averageScore7Days = 0;
        stats.averageScore30Days = 0;
        stats.averageScoreAllTime = 0;
        stats.learningVelocity = 0;
        stats.consistencyRate = 0;
        stats.weakAreas = new ArrayList<>();
        stats.recommendations = new ArrayList<>();
        stats.progressTrend = Trend.STABLE;
        stats.lastCalculatedAt = LocalDateTime.now();
        return stats;
    }

    public void recalculate(List<LearningActivity> recentActivities, int daysActive7, int daysActive30) {
        calculateAverages(recentActivities);
        calculateVelocity(recentActivities);
        calculateConsistency(daysActive7, daysActive30);
        calculateTrend();
        identifyWeakAreas(recentActivities);
        generateRecommendations();
        lastCalculatedAt = LocalDateTime.now();
    }

    private void calculateAverages(List<LearningActivity> activities) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysAgo = now.minusDays(7);
        LocalDateTime thirtyDaysAgo = now.minusDays(30);

        List<LearningActivity> scored = activities.stream()
                .filter(a -> a.getScore() != null && a.getScore() > 0)
                .toList();

        averageScoreAllTime = scored.stream()
                .mapToDouble(LearningActivity::getScore)
                .average().orElse(0);

        averageScore30Days = scored.stream()
                .filter(a -> a.getOccurredAt().isAfter(thirtyDaysAgo))
                .mapToDouble(LearningActivity::getScore)
                .average().orElse(0);

        averageScore7Days = scored.stream()
                .filter(a -> a.getOccurredAt().isAfter(sevenDaysAgo))
                .mapToDouble(LearningActivity::getScore)
                .average().orElse(0);
    }

    private void calculateVelocity(List<LearningActivity> activities) {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        long recentCount = activities.stream()
                .filter(a -> a.getOccurredAt().isAfter(sevenDaysAgo))
                .count();
        learningVelocity = recentCount / 7.0;
    }

    private void calculateConsistency(int daysActive7, int daysActive30) {
        consistencyRate = daysActive30 > 0 ? (daysActive30 / 30.0) * 100 : 0;
    }

    private void calculateTrend() {
        progressTrend = Trend.calculate(averageScore7Days, averageScore30Days);
    }

    private void identifyWeakAreas(List<LearningActivity> activities) {
        weakAreas = new ArrayList<>();
        for (ModuleType moduleType : ModuleType.values()) {
            ActivityType activityType;
            try {
                activityType = ActivityType.fromModuleType(moduleType);
            } catch (IllegalArgumentException e) {
                continue;
            }

            List<LearningActivity> moduleActivities = activities.stream()
                    .filter(a -> a.getActivityType() == activityType && a.getScore() != null)
                    .toList();

            if (moduleActivities.size() >= 3) {
                double avg = moduleActivities.stream()
                        .mapToDouble(LearningActivity::getScore)
                        .average().orElse(0);
                if (avg < 75) {
                    weakAreas.add(WeakArea.identify(moduleType, moduleType.name().toLowerCase(), avg));
                }
            }
        }
    }

    private void generateRecommendations() {
        recommendations = new ArrayList<>();
        for (WeakArea weakArea : weakAreas) {
            recommendations.add(Recommendation.fromWeakArea(weakArea));
        }
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public double getAverageScore7Days() { return averageScore7Days; }
    public void setAverageScore7Days(double averageScore7Days) { this.averageScore7Days = averageScore7Days; }
    public double getAverageScore30Days() { return averageScore30Days; }
    public void setAverageScore30Days(double averageScore30Days) { this.averageScore30Days = averageScore30Days; }
    public double getAverageScoreAllTime() { return averageScoreAllTime; }
    public void setAverageScoreAllTime(double averageScoreAllTime) { this.averageScoreAllTime = averageScoreAllTime; }
    public double getLearningVelocity() { return learningVelocity; }
    public void setLearningVelocity(double learningVelocity) { this.learningVelocity = learningVelocity; }
    public double getConsistencyRate() { return consistencyRate; }
    public void setConsistencyRate(double consistencyRate) { this.consistencyRate = consistencyRate; }
    public List<WeakArea> getWeakAreas() { return weakAreas; }
    public void setWeakAreas(List<WeakArea> weakAreas) { this.weakAreas = weakAreas; }
    public List<Recommendation> getRecommendations() { return recommendations; }
    public void setRecommendations(List<Recommendation> recommendations) { this.recommendations = recommendations; }
    public Trend getProgressTrend() { return progressTrend; }
    public void setProgressTrend(Trend progressTrend) { this.progressTrend = progressTrend; }
    public LocalDateTime getLastCalculatedAt() { return lastCalculatedAt; }
    public void setLastCalculatedAt(LocalDateTime lastCalculatedAt) { this.lastCalculatedAt = lastCalculatedAt; }
}
```

- [ ] **Step 2: Create ProgressUpdatedEvent**

```java
package com.nihongodev.platform.domain.event;

import com.nihongodev.platform.domain.model.ActivityType;
import com.nihongodev.platform.domain.model.ProgressLevel;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProgressUpdatedEvent(
        UUID userId,
        long totalXp,
        ProgressLevel level,
        double globalScore,
        ActivityType activityType,
        LocalDateTime occurredAt
) {
    public static ProgressUpdatedEvent of(UUID userId, long totalXp, ProgressLevel level,
                                          double globalScore, ActivityType activityType) {
        return new ProgressUpdatedEvent(userId, totalXp, level, globalScore, activityType, LocalDateTime.now());
    }
}
```

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/nihongodev/platform/domain/model/UserStatistics.java \
        src/main/java/com/nihongodev/platform/domain/event/ProgressUpdatedEvent.java
git commit -m "feat(progress): add UserStatistics model and ProgressUpdatedEvent"
```

---

## Task 6: Application Layer — Ports

**Files:**
- Create: `src/main/java/com/nihongodev/platform/application/port/in/UpdateProgressOnLessonCompletedPort.java`
- Create: `src/main/java/com/nihongodev/platform/application/port/in/UpdateProgressOnQuizCompletedPort.java`
- Create: `src/main/java/com/nihongodev/platform/application/port/in/UpdateProgressOnInterviewCompletedPort.java`
- Create: `src/main/java/com/nihongodev/platform/application/port/in/UpdateProgressOnCorrectionCompletedPort.java`
- Create: `src/main/java/com/nihongodev/platform/application/port/in/GetUserProgressPort.java`
- Create: `src/main/java/com/nihongodev/platform/application/port/in/GetModuleProgressPort.java`
- Create: `src/main/java/com/nihongodev/platform/application/port/in/GetUserActivityHistoryPort.java`
- Create: `src/main/java/com/nihongodev/platform/application/port/in/GetUserStatisticsPort.java`
- Create: `src/main/java/com/nihongodev/platform/application/port/in/GetPlatformAnalyticsPort.java`
- Create: `src/main/java/com/nihongodev/platform/application/port/in/RecalculateStatisticsPort.java`
- Create: `src/main/java/com/nihongodev/platform/application/port/out/ProgressRepositoryPort.java`
- Create: `src/main/java/com/nihongodev/platform/application/port/out/ModuleProgressRepositoryPort.java`
- Create: `src/main/java/com/nihongodev/platform/application/port/out/LearningActivityRepositoryPort.java`
- Create: `src/main/java/com/nihongodev/platform/application/port/out/StatisticsRepositoryPort.java`
- Create: `src/main/java/com/nihongodev/platform/application/port/out/AnalyticsQueryPort.java`

- [ ] **Step 1: Create Write Ports IN**

```java
// UpdateProgressOnLessonCompletedPort.java
package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.domain.event.LessonCompletedEvent;

public interface UpdateProgressOnLessonCompletedPort {
    void execute(LessonCompletedEvent event);
}
```

```java
// UpdateProgressOnQuizCompletedPort.java
package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.domain.event.QuizCompletedEvent;

public interface UpdateProgressOnQuizCompletedPort {
    void execute(QuizCompletedEvent event);
}
```

```java
// UpdateProgressOnInterviewCompletedPort.java
package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.domain.event.InterviewCompletedEvent;

public interface UpdateProgressOnInterviewCompletedPort {
    void execute(InterviewCompletedEvent event);
}
```

```java
// UpdateProgressOnCorrectionCompletedPort.java
package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.domain.event.TextCorrectedEvent;

public interface UpdateProgressOnCorrectionCompletedPort {
    void execute(TextCorrectedEvent event);
}
```

- [ ] **Step 2: Create Read Ports IN**

```java
// GetUserProgressPort.java
package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.UserProgressDto;
import java.util.UUID;

public interface GetUserProgressPort {
    UserProgressDto execute(UUID userId);
}
```

```java
// GetModuleProgressPort.java
package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.ModuleProgressDto;
import com.nihongodev.platform.domain.model.ModuleType;
import java.util.List;
import java.util.UUID;

public interface GetModuleProgressPort {
    List<ModuleProgressDto> getAll(UUID userId);
    ModuleProgressDto getByType(UUID userId, ModuleType moduleType);
}
```

```java
// GetUserActivityHistoryPort.java
package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.LearningActivityDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface GetUserActivityHistoryPort {
    Page<LearningActivityDto> execute(UUID userId, Pageable pageable);
}
```

```java
// GetUserStatisticsPort.java
package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.UserStatisticsDto;
import java.util.UUID;

public interface GetUserStatisticsPort {
    UserStatisticsDto execute(UUID userId);
}
```

```java
// GetPlatformAnalyticsPort.java
package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.PlatformAnalyticsDto;
import com.nihongodev.platform.application.dto.TopUserDto;
import com.nihongodev.platform.domain.model.ModuleType;
import java.time.LocalDateTime;
import java.util.List;

public interface GetPlatformAnalyticsPort {
    PlatformAnalyticsDto getOverview();
    List<TopUserDto> getTopUsers(int limit);
}
```

```java
// RecalculateStatisticsPort.java
package com.nihongodev.platform.application.port.in;

public interface RecalculateStatisticsPort {
    void recalculateAll();
}
```

- [ ] **Step 3: Create Ports OUT**

```java
// ProgressRepositoryPort.java
package com.nihongodev.platform.application.port.out;

import com.nihongodev.platform.domain.model.UserProgress;
import java.util.Optional;
import java.util.UUID;

public interface ProgressRepositoryPort {
    UserProgress save(UserProgress progress);
    Optional<UserProgress> findByUserId(UUID userId);
}
```

```java
// ModuleProgressRepositoryPort.java
package com.nihongodev.platform.application.port.out;

import com.nihongodev.platform.domain.model.ModuleProgress;
import com.nihongodev.platform.domain.model.ModuleType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ModuleProgressRepositoryPort {
    ModuleProgress save(ModuleProgress moduleProgress);
    Optional<ModuleProgress> findByUserIdAndModuleType(UUID userId, ModuleType moduleType);
    List<ModuleProgress> findAllByUserId(UUID userId);
}
```

```java
// LearningActivityRepositoryPort.java
package com.nihongodev.platform.application.port.out;

import com.nihongodev.platform.domain.model.ActivityType;
import com.nihongodev.platform.domain.model.LearningActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface LearningActivityRepositoryPort {
    LearningActivity save(LearningActivity activity);
    Page<LearningActivity> findByUserId(UUID userId, Pageable pageable);
    List<LearningActivity> findByUserIdAndOccurredAfter(UUID userId, LocalDateTime after);
    boolean existsByUserIdAndReferenceIdAndActivityType(UUID userId, UUID referenceId, ActivityType activityType);
    long countDistinctDaysActiveAfter(UUID userId, LocalDateTime after);
}
```

```java
// StatisticsRepositoryPort.java
package com.nihongodev.platform.application.port.out;

import com.nihongodev.platform.domain.model.UserStatistics;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StatisticsRepositoryPort {
    UserStatistics save(UserStatistics statistics);
    Optional<UserStatistics> findByUserId(UUID userId);
    List<UUID> findUserIdsWithActivitySince(LocalDateTime since);
}
```

```java
// AnalyticsQueryPort.java
package com.nihongodev.platform.application.port.out;

import com.nihongodev.platform.application.dto.TopUserDto;
import com.nihongodev.platform.domain.model.ModuleType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface AnalyticsQueryPort {
    long countActiveUsers(LocalDateTime since);
    double averageGlobalScore();
    Map<ModuleType, Double> averageScoreByModule();
    long countTotalActivities();
    List<TopUserDto> topUsers(int limit);
}
```

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/nihongodev/platform/application/port/
git commit -m "feat(progress): add all ports IN and OUT for Progress & Analytics"
```

---

## Task 7: Application Layer — DTOs

**Files:**
- Create: `src/main/java/com/nihongodev/platform/application/dto/UserProgressDto.java`
- Create: `src/main/java/com/nihongodev/platform/application/dto/ModuleProgressDto.java`
- Create: `src/main/java/com/nihongodev/platform/application/dto/LearningActivityDto.java`
- Create: `src/main/java/com/nihongodev/platform/application/dto/UserStatisticsDto.java`
- Create: `src/main/java/com/nihongodev/platform/application/dto/WeakAreaDto.java`
- Create: `src/main/java/com/nihongodev/platform/application/dto/RecommendationDto.java`
- Create: `src/main/java/com/nihongodev/platform/application/dto/PlatformAnalyticsDto.java`
- Create: `src/main/java/com/nihongodev/platform/application/dto/TopUserDto.java`

- [ ] **Step 1: Create all DTOs**

```java
// UserProgressDto.java
package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.ProgressLevel;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserProgressDto(
        UUID userId,
        int totalLessonsCompleted,
        int totalQuizzesCompleted,
        int totalInterviewsCompleted,
        int totalCorrectionsCompleted,
        double globalScore,
        int currentStreak,
        int longestStreak,
        ProgressLevel level,
        long totalXp,
        LocalDateTime lastActivityAt
) {}
```

```java
// ModuleProgressDto.java
package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.ModuleStatus;
import com.nihongodev.platform.domain.model.ModuleType;
import java.time.LocalDateTime;

public record ModuleProgressDto(
        ModuleType moduleType,
        int completedItems,
        Integer totalItems,
        double completionPercentage,
        double averageScore,
        double bestScore,
        ModuleStatus status,
        LocalDateTime lastCompletedAt
) {}
```

```java
// LearningActivityDto.java
package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.ActivityType;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record LearningActivityDto(
        ActivityType activityType,
        UUID referenceId,
        Double score,
        int xpEarned,
        Map<String, Object> metadata,
        LocalDateTime occurredAt
) {}
```

```java
// UserStatisticsDto.java
package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.Trend;
import java.util.List;

public record UserStatisticsDto(
        double averageScore7Days,
        double averageScore30Days,
        double averageScoreAllTime,
        double learningVelocity,
        double consistencyRate,
        Trend progressTrend,
        List<WeakAreaDto> weakAreas,
        List<RecommendationDto> recommendations
) {}
```

```java
// WeakAreaDto.java
package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.ModuleType;
import com.nihongodev.platform.domain.model.Priority;

public record WeakAreaDto(
        ModuleType moduleType,
        String topic,
        double averageScore,
        Priority priority
) {}
```

```java
// RecommendationDto.java
package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.Priority;
import com.nihongodev.platform.domain.model.RecommendationType;
import java.util.UUID;

public record RecommendationDto(
        RecommendationType type,
        UUID targetId,
        String reason,
        Priority priority
) {}
```

```java
// PlatformAnalyticsDto.java
package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.ModuleType;
import java.util.Map;

public record PlatformAnalyticsDto(
        long totalUsers,
        long activeUsersLast7Days,
        long activeUsersLast30Days,
        double averageGlobalScore,
        long totalActivities,
        Map<ModuleType, Double> averageScoreByModule
) {}
```

```java
// TopUserDto.java
package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.ProgressLevel;
import java.util.UUID;

public record TopUserDto(
        UUID userId,
        String displayName,
        long totalXp,
        double globalScore,
        ProgressLevel level
) {}
```

- [ ] **Step 2: Commit**

```bash
git add src/main/java/com/nihongodev/platform/application/dto/UserProgressDto.java \
        src/main/java/com/nihongodev/platform/application/dto/ModuleProgressDto.java \
        src/main/java/com/nihongodev/platform/application/dto/LearningActivityDto.java \
        src/main/java/com/nihongodev/platform/application/dto/UserStatisticsDto.java \
        src/main/java/com/nihongodev/platform/application/dto/WeakAreaDto.java \
        src/main/java/com/nihongodev/platform/application/dto/RecommendationDto.java \
        src/main/java/com/nihongodev/platform/application/dto/PlatformAnalyticsDto.java \
        src/main/java/com/nihongodev/platform/application/dto/TopUserDto.java
git commit -m "feat(progress): add DTOs for Progress & Analytics responses"
```

---

## Task 8: Write Use Cases (with TDD)

**Files:**
- Create: `src/main/java/com/nihongodev/platform/application/usecase/UpdateProgressOnQuizCompletedUseCase.java`
- Create: `src/main/java/com/nihongodev/platform/application/usecase/UpdateProgressOnLessonCompletedUseCase.java`
- Create: `src/main/java/com/nihongodev/platform/application/usecase/UpdateProgressOnInterviewCompletedUseCase.java`
- Create: `src/main/java/com/nihongodev/platform/application/usecase/UpdateProgressOnCorrectionCompletedUseCase.java`
- Create: `src/test/java/com/nihongodev/platform/application/usecase/UpdateProgressOnQuizCompletedUseCaseTest.java`
- Create: `src/test/java/com/nihongodev/platform/application/usecase/UpdateProgressOnLessonCompletedUseCaseTest.java`

- [ ] **Step 1: Write failing test for UpdateProgressOnQuizCompletedUseCase**

```java
package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.port.out.*;
import com.nihongodev.platform.domain.event.ProgressUpdatedEvent;
import com.nihongodev.platform.domain.event.QuizCompletedEvent;
import com.nihongodev.platform.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateProgressOnQuizCompletedUseCase")
class UpdateProgressOnQuizCompletedUseCaseTest {

    @Mock private ProgressRepositoryPort progressRepository;
    @Mock private ModuleProgressRepositoryPort moduleProgressRepository;
    @Mock private LearningActivityRepositoryPort activityRepository;
    @Mock private EventPublisherPort eventPublisher;

    private UpdateProgressOnQuizCompletedUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new UpdateProgressOnQuizCompletedUseCase(
                progressRepository, moduleProgressRepository, activityRepository, eventPublisher);
    }

    @Test
    @DisplayName("should create UserProgress if first activity for user")
    void shouldCreateProgressIfFirst() {
        UUID userId = UUID.randomUUID();
        QuizCompletedEvent event = new QuizCompletedEvent(
                userId, UUID.randomUUID(), UUID.randomUUID(), "Quiz 1",
                85.0, true, 5, "CLASSIC", LocalDateTime.now());

        when(progressRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(moduleProgressRepository.findByUserIdAndModuleType(userId, ModuleType.QUIZ))
                .thenReturn(Optional.empty());
        when(activityRepository.existsByUserIdAndReferenceIdAndActivityType(
                userId, event.attemptId(), ActivityType.QUIZ_COMPLETED)).thenReturn(false);
        when(progressRepository.save(any(UserProgress.class))).thenAnswer(inv -> inv.getArgument(0));
        when(moduleProgressRepository.save(any(ModuleProgress.class))).thenAnswer(inv -> inv.getArgument(0));
        when(activityRepository.save(any(LearningActivity.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(event);

        ArgumentCaptor<UserProgress> captor = ArgumentCaptor.forClass(UserProgress.class);
        verify(progressRepository).save(captor.capture());
        UserProgress saved = captor.getValue();
        assertThat(saved.getTotalQuizzesCompleted()).isEqualTo(1);
        assertThat(saved.getTotalXp()).isEqualTo(115); // 30 + (85 * 1.0)
    }

    @Test
    @DisplayName("should increment quiz counter on existing progress")
    void shouldIncrementQuizCounter() {
        UUID userId = UUID.randomUUID();
        UserProgress existing = UserProgress.initialize(userId);
        existing.recordActivity(ActivityType.QUIZ_COMPLETED, 70.0);

        QuizCompletedEvent event = new QuizCompletedEvent(
                userId, UUID.randomUUID(), UUID.randomUUID(), "Quiz 2",
                90.0, true, 7, "CLASSIC", LocalDateTime.now());

        when(progressRepository.findByUserId(userId)).thenReturn(Optional.of(existing));
        when(moduleProgressRepository.findByUserIdAndModuleType(userId, ModuleType.QUIZ))
                .thenReturn(Optional.of(ModuleProgress.initialize(userId, ModuleType.QUIZ)));
        when(activityRepository.existsByUserIdAndReferenceIdAndActivityType(
                userId, event.attemptId(), ActivityType.QUIZ_COMPLETED)).thenReturn(false);
        when(progressRepository.save(any(UserProgress.class))).thenAnswer(inv -> inv.getArgument(0));
        when(moduleProgressRepository.save(any(ModuleProgress.class))).thenAnswer(inv -> inv.getArgument(0));
        when(activityRepository.save(any(LearningActivity.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(event);

        ArgumentCaptor<UserProgress> captor = ArgumentCaptor.forClass(UserProgress.class);
        verify(progressRepository).save(captor.capture());
        assertThat(captor.getValue().getTotalQuizzesCompleted()).isEqualTo(2);
    }

    @Test
    @DisplayName("should be idempotent and skip duplicate events")
    void shouldSkipDuplicateEvent() {
        UUID userId = UUID.randomUUID();
        UUID attemptId = UUID.randomUUID();
        QuizCompletedEvent event = new QuizCompletedEvent(
                userId, UUID.randomUUID(), attemptId, "Quiz 1",
                85.0, true, 5, "CLASSIC", LocalDateTime.now());

        when(activityRepository.existsByUserIdAndReferenceIdAndActivityType(
                userId, attemptId, ActivityType.QUIZ_COMPLETED)).thenReturn(true);

        useCase.execute(event);

        verify(progressRepository, never()).save(any());
        verify(eventPublisher, never()).publish(anyString(), any());
    }

    @Test
    @DisplayName("should publish ProgressUpdatedEvent")
    void shouldPublishEvent() {
        UUID userId = UUID.randomUUID();
        QuizCompletedEvent event = new QuizCompletedEvent(
                userId, UUID.randomUUID(), UUID.randomUUID(), "Quiz 1",
                80.0, true, 4, "CLASSIC", LocalDateTime.now());

        when(progressRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(moduleProgressRepository.findByUserIdAndModuleType(userId, ModuleType.QUIZ))
                .thenReturn(Optional.empty());
        when(activityRepository.existsByUserIdAndReferenceIdAndActivityType(
                any(), any(), any())).thenReturn(false);
        when(progressRepository.save(any(UserProgress.class))).thenAnswer(inv -> inv.getArgument(0));
        when(moduleProgressRepository.save(any(ModuleProgress.class))).thenAnswer(inv -> inv.getArgument(0));
        when(activityRepository.save(any(LearningActivity.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(event);

        verify(eventPublisher).publish(eq("progress-events"), any(ProgressUpdatedEvent.class));
    }

    @Test
    @DisplayName("should update module progress average score")
    void shouldUpdateModuleProgress() {
        UUID userId = UUID.randomUUID();
        QuizCompletedEvent event = new QuizCompletedEvent(
                userId, UUID.randomUUID(), UUID.randomUUID(), "Quiz 1",
                75.0, true, 3, "CLASSIC", LocalDateTime.now());

        when(progressRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(moduleProgressRepository.findByUserIdAndModuleType(userId, ModuleType.QUIZ))
                .thenReturn(Optional.empty());
        when(activityRepository.existsByUserIdAndReferenceIdAndActivityType(
                any(), any(), any())).thenReturn(false);
        when(progressRepository.save(any(UserProgress.class))).thenAnswer(inv -> inv.getArgument(0));
        when(moduleProgressRepository.save(any(ModuleProgress.class))).thenAnswer(inv -> inv.getArgument(0));
        when(activityRepository.save(any(LearningActivity.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(event);

        ArgumentCaptor<ModuleProgress> captor = ArgumentCaptor.forClass(ModuleProgress.class);
        verify(moduleProgressRepository).save(captor.capture());
        assertThat(captor.getValue().getCompletedItems()).isEqualTo(1);
        assertThat(captor.getValue().getAverageScore()).isEqualTo(75.0);
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn test -pl . -Dtest=UpdateProgressOnQuizCompletedUseCaseTest -f pom.xml`
Expected: FAIL — class does not exist

- [ ] **Step 3: Implement UpdateProgressOnQuizCompletedUseCase**

```java
package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.port.in.UpdateProgressOnQuizCompletedPort;
import com.nihongodev.platform.application.port.out.*;
import com.nihongodev.platform.domain.event.ProgressUpdatedEvent;
import com.nihongodev.platform.domain.event.QuizCompletedEvent;
import com.nihongodev.platform.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UpdateProgressOnQuizCompletedUseCase implements UpdateProgressOnQuizCompletedPort {

    private final ProgressRepositoryPort progressRepository;
    private final ModuleProgressRepositoryPort moduleProgressRepository;
    private final LearningActivityRepositoryPort activityRepository;
    private final EventPublisherPort eventPublisher;

    public UpdateProgressOnQuizCompletedUseCase(ProgressRepositoryPort progressRepository,
                                                ModuleProgressRepositoryPort moduleProgressRepository,
                                                LearningActivityRepositoryPort activityRepository,
                                                EventPublisherPort eventPublisher) {
        this.progressRepository = progressRepository;
        this.moduleProgressRepository = moduleProgressRepository;
        this.activityRepository = activityRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public void execute(QuizCompletedEvent event) {
        if (activityRepository.existsByUserIdAndReferenceIdAndActivityType(
                event.userId(), event.attemptId(), ActivityType.QUIZ_COMPLETED)) {
            return;
        }

        UserProgress progress = progressRepository.findByUserId(event.userId())
                .orElseGet(() -> UserProgress.initialize(event.userId()));

        progress.recordActivity(ActivityType.QUIZ_COMPLETED, event.percentage());
        progress.updateGlobalScore(ActivityType.QUIZ_COMPLETED, event.percentage());
        progress.updateStreak(event.occurredAt());
        progress.setLastActivityAt(event.occurredAt());

        progressRepository.save(progress);

        ModuleProgress moduleProgress = moduleProgressRepository
                .findByUserIdAndModuleType(event.userId(), ModuleType.QUIZ)
                .orElseGet(() -> ModuleProgress.initialize(event.userId(), ModuleType.QUIZ));

        moduleProgress.recordCompletion(event.percentage());
        moduleProgressRepository.save(moduleProgress);

        int xpEarned = ActivityType.QUIZ_COMPLETED.calculateXp(event.percentage());
        LearningActivity activity = LearningActivity.create(
                event.userId(), ActivityType.QUIZ_COMPLETED,
                event.attemptId(), event.percentage(), xpEarned);
        activity.addMetadata("quizTitle", event.quizTitle());
        activity.addMetadata("passed", event.passed());
        activity.addMetadata("maxStreak", event.maxStreak());
        activity.addMetadata("mode", event.mode());
        activityRepository.save(activity);

        eventPublisher.publish("progress-events", ProgressUpdatedEvent.of(
                event.userId(), progress.getTotalXp(), progress.getLevel(),
                progress.getGlobalScore(), ActivityType.QUIZ_COMPLETED));
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `mvn test -pl . -Dtest=UpdateProgressOnQuizCompletedUseCaseTest -f pom.xml`
Expected: ALL PASS

- [ ] **Step 5: Write failing test for UpdateProgressOnLessonCompletedUseCase**

```java
package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.port.out.*;
import com.nihongodev.platform.domain.event.LessonCompletedEvent;
import com.nihongodev.platform.domain.event.ProgressUpdatedEvent;
import com.nihongodev.platform.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateProgressOnLessonCompletedUseCase")
class UpdateProgressOnLessonCompletedUseCaseTest {

    @Mock private ProgressRepositoryPort progressRepository;
    @Mock private ModuleProgressRepositoryPort moduleProgressRepository;
    @Mock private LearningActivityRepositoryPort activityRepository;
    @Mock private EventPublisherPort eventPublisher;

    private UpdateProgressOnLessonCompletedUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new UpdateProgressOnLessonCompletedUseCase(
                progressRepository, moduleProgressRepository, activityRepository, eventPublisher);
    }

    @Test
    @DisplayName("should increment lesson counter and award 50 XP")
    void shouldIncrementLessonCounter() {
        UUID userId = UUID.randomUUID();
        LessonCompletedEvent event = new LessonCompletedEvent(
                userId, UUID.randomUUID(), "Lesson 1", "GRAMMAR", "N5", LocalDateTime.now());

        when(progressRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(moduleProgressRepository.findByUserIdAndModuleType(userId, ModuleType.LESSON))
                .thenReturn(Optional.empty());
        when(activityRepository.existsByUserIdAndReferenceIdAndActivityType(
                any(), any(), any())).thenReturn(false);
        when(progressRepository.save(any(UserProgress.class))).thenAnswer(inv -> inv.getArgument(0));
        when(moduleProgressRepository.save(any(ModuleProgress.class))).thenAnswer(inv -> inv.getArgument(0));
        when(activityRepository.save(any(LearningActivity.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(event);

        ArgumentCaptor<UserProgress> captor = ArgumentCaptor.forClass(UserProgress.class);
        verify(progressRepository).save(captor.capture());
        assertThat(captor.getValue().getTotalLessonsCompleted()).isEqualTo(1);
        assertThat(captor.getValue().getTotalXp()).isEqualTo(50);
    }

    @Test
    @DisplayName("should be idempotent and skip duplicate events")
    void shouldSkipDuplicate() {
        UUID userId = UUID.randomUUID();
        UUID lessonId = UUID.randomUUID();
        LessonCompletedEvent event = new LessonCompletedEvent(
                userId, lessonId, "Lesson 1", "GRAMMAR", "N5", LocalDateTime.now());

        when(activityRepository.existsByUserIdAndReferenceIdAndActivityType(
                userId, lessonId, ActivityType.LESSON_COMPLETED)).thenReturn(true);

        useCase.execute(event);

        verify(progressRepository, never()).save(any());
    }

    @Test
    @DisplayName("should publish ProgressUpdatedEvent after lesson completion")
    void shouldPublishEvent() {
        UUID userId = UUID.randomUUID();
        LessonCompletedEvent event = new LessonCompletedEvent(
                userId, UUID.randomUUID(), "Lesson 1", "GRAMMAR", "N5", LocalDateTime.now());

        when(progressRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(moduleProgressRepository.findByUserIdAndModuleType(userId, ModuleType.LESSON))
                .thenReturn(Optional.empty());
        when(activityRepository.existsByUserIdAndReferenceIdAndActivityType(
                any(), any(), any())).thenReturn(false);
        when(progressRepository.save(any(UserProgress.class))).thenAnswer(inv -> inv.getArgument(0));
        when(moduleProgressRepository.save(any(ModuleProgress.class))).thenAnswer(inv -> inv.getArgument(0));
        when(activityRepository.save(any(LearningActivity.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.execute(event);

        verify(eventPublisher).publish(eq("progress-events"), any(ProgressUpdatedEvent.class));
    }
}
```

- [ ] **Step 6: Implement UpdateProgressOnLessonCompletedUseCase**

```java
package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.port.in.UpdateProgressOnLessonCompletedPort;
import com.nihongodev.platform.application.port.out.*;
import com.nihongodev.platform.domain.event.LessonCompletedEvent;
import com.nihongodev.platform.domain.event.ProgressUpdatedEvent;
import com.nihongodev.platform.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateProgressOnLessonCompletedUseCase implements UpdateProgressOnLessonCompletedPort {

    private final ProgressRepositoryPort progressRepository;
    private final ModuleProgressRepositoryPort moduleProgressRepository;
    private final LearningActivityRepositoryPort activityRepository;
    private final EventPublisherPort eventPublisher;

    public UpdateProgressOnLessonCompletedUseCase(ProgressRepositoryPort progressRepository,
                                                  ModuleProgressRepositoryPort moduleProgressRepository,
                                                  LearningActivityRepositoryPort activityRepository,
                                                  EventPublisherPort eventPublisher) {
        this.progressRepository = progressRepository;
        this.moduleProgressRepository = moduleProgressRepository;
        this.activityRepository = activityRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public void execute(LessonCompletedEvent event) {
        if (activityRepository.existsByUserIdAndReferenceIdAndActivityType(
                event.userId(), event.lessonId(), ActivityType.LESSON_COMPLETED)) {
            return;
        }

        UserProgress progress = progressRepository.findByUserId(event.userId())
                .orElseGet(() -> UserProgress.initialize(event.userId()));

        progress.recordActivity(ActivityType.LESSON_COMPLETED, 0);
        progress.updateStreak(event.occurredAt());
        progress.setLastActivityAt(event.occurredAt());

        progressRepository.save(progress);

        ModuleProgress moduleProgress = moduleProgressRepository
                .findByUserIdAndModuleType(event.userId(), ModuleType.LESSON)
                .orElseGet(() -> ModuleProgress.initialize(event.userId(), ModuleType.LESSON));

        moduleProgress.recordCompletion(100.0);
        moduleProgressRepository.save(moduleProgress);

        int xpEarned = ActivityType.LESSON_COMPLETED.calculateXp(0);
        LearningActivity activity = LearningActivity.create(
                event.userId(), ActivityType.LESSON_COMPLETED,
                event.lessonId(), null, xpEarned);
        activity.addMetadata("lessonTitle", event.lessonTitle());
        activity.addMetadata("lessonType", event.lessonType());
        activity.addMetadata("lessonLevel", event.lessonLevel());
        activityRepository.save(activity);

        eventPublisher.publish("progress-events", ProgressUpdatedEvent.of(
                event.userId(), progress.getTotalXp(), progress.getLevel(),
                progress.getGlobalScore(), ActivityType.LESSON_COMPLETED));
    }
}
```

- [ ] **Step 7: Implement UpdateProgressOnInterviewCompletedUseCase**

```java
package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.port.in.UpdateProgressOnInterviewCompletedPort;
import com.nihongodev.platform.application.port.out.*;
import com.nihongodev.platform.domain.event.InterviewCompletedEvent;
import com.nihongodev.platform.domain.event.ProgressUpdatedEvent;
import com.nihongodev.platform.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UpdateProgressOnInterviewCompletedUseCase implements UpdateProgressOnInterviewCompletedPort {

    private final ProgressRepositoryPort progressRepository;
    private final ModuleProgressRepositoryPort moduleProgressRepository;
    private final LearningActivityRepositoryPort activityRepository;
    private final EventPublisherPort eventPublisher;

    public UpdateProgressOnInterviewCompletedUseCase(ProgressRepositoryPort progressRepository,
                                                     ModuleProgressRepositoryPort moduleProgressRepository,
                                                     LearningActivityRepositoryPort activityRepository,
                                                     EventPublisherPort eventPublisher) {
        this.progressRepository = progressRepository;
        this.moduleProgressRepository = moduleProgressRepository;
        this.activityRepository = activityRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public void execute(InterviewCompletedEvent event) {
        if (activityRepository.existsByUserIdAndReferenceIdAndActivityType(
                event.userId(), event.sessionId(), ActivityType.INTERVIEW_COMPLETED)) {
            return;
        }

        UserProgress progress = progressRepository.findByUserId(event.userId())
                .orElseGet(() -> UserProgress.initialize(event.userId()));

        progress.recordActivity(ActivityType.INTERVIEW_COMPLETED, event.overallScore());
        progress.updateGlobalScore(ActivityType.INTERVIEW_COMPLETED, event.overallScore());
        LocalDateTime now = LocalDateTime.now();
        progress.updateStreak(now);
        progress.setLastActivityAt(now);

        progressRepository.save(progress);

        ModuleProgress moduleProgress = moduleProgressRepository
                .findByUserIdAndModuleType(event.userId(), ModuleType.INTERVIEW)
                .orElseGet(() -> ModuleProgress.initialize(event.userId(), ModuleType.INTERVIEW));

        moduleProgress.recordCompletion(event.overallScore());
        moduleProgressRepository.save(moduleProgress);

        int xpEarned = ActivityType.INTERVIEW_COMPLETED.calculateXp(event.overallScore());
        LearningActivity activity = LearningActivity.create(
                event.userId(), ActivityType.INTERVIEW_COMPLETED,
                event.sessionId(), event.overallScore(), xpEarned);
        activity.addMetadata("interviewType", event.interviewType());
        activity.addMetadata("passed", event.passed());
        activity.addMetadata("durationSeconds", event.durationSeconds());
        activityRepository.save(activity);

        eventPublisher.publish("progress-events", ProgressUpdatedEvent.of(
                event.userId(), progress.getTotalXp(), progress.getLevel(),
                progress.getGlobalScore(), ActivityType.INTERVIEW_COMPLETED));
    }
}
```

- [ ] **Step 8: Implement UpdateProgressOnCorrectionCompletedUseCase**

```java
package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.port.in.UpdateProgressOnCorrectionCompletedPort;
import com.nihongodev.platform.application.port.out.*;
import com.nihongodev.platform.domain.event.ProgressUpdatedEvent;
import com.nihongodev.platform.domain.event.TextCorrectedEvent;
import com.nihongodev.platform.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateProgressOnCorrectionCompletedUseCase implements UpdateProgressOnCorrectionCompletedPort {

    private final ProgressRepositoryPort progressRepository;
    private final ModuleProgressRepositoryPort moduleProgressRepository;
    private final LearningActivityRepositoryPort activityRepository;
    private final EventPublisherPort eventPublisher;

    public UpdateProgressOnCorrectionCompletedUseCase(ProgressRepositoryPort progressRepository,
                                                      ModuleProgressRepositoryPort moduleProgressRepository,
                                                      LearningActivityRepositoryPort activityRepository,
                                                      EventPublisherPort eventPublisher) {
        this.progressRepository = progressRepository;
        this.moduleProgressRepository = moduleProgressRepository;
        this.activityRepository = activityRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public void execute(TextCorrectedEvent event) {
        if (activityRepository.existsByUserIdAndReferenceIdAndActivityType(
                event.userId(), event.sessionId(), ActivityType.CORRECTION_COMPLETED)) {
            return;
        }

        UserProgress progress = progressRepository.findByUserId(event.userId())
                .orElseGet(() -> UserProgress.initialize(event.userId()));

        progress.recordActivity(ActivityType.CORRECTION_COMPLETED, event.overallScore());
        progress.updateGlobalScore(ActivityType.CORRECTION_COMPLETED, event.overallScore());
        progress.updateStreak(event.correctedAt());
        progress.setLastActivityAt(event.correctedAt());

        progressRepository.save(progress);

        ModuleProgress moduleProgress = moduleProgressRepository
                .findByUserIdAndModuleType(event.userId(), ModuleType.CORRECTION)
                .orElseGet(() -> ModuleProgress.initialize(event.userId(), ModuleType.CORRECTION));

        moduleProgress.recordCompletion(event.overallScore());
        moduleProgressRepository.save(moduleProgress);

        int xpEarned = ActivityType.CORRECTION_COMPLETED.calculateXp(event.overallScore());
        LearningActivity activity = LearningActivity.create(
                event.userId(), ActivityType.CORRECTION_COMPLETED,
                event.sessionId(), event.overallScore(), xpEarned);
        activity.addMetadata("textType", event.textType());
        activity.addMetadata("totalAnnotations", event.totalAnnotations());
        activity.addMetadata("errorCount", event.errorCount());
        activityRepository.save(activity);

        eventPublisher.publish("progress-events", ProgressUpdatedEvent.of(
                event.userId(), progress.getTotalXp(), progress.getLevel(),
                progress.getGlobalScore(), ActivityType.CORRECTION_COMPLETED));
    }
}
```

- [ ] **Step 9: Run all tests**

Run: `mvn test -pl . -Dtest="UpdateProgressOn*UseCaseTest" -f pom.xml`
Expected: ALL PASS

- [ ] **Step 10: Commit**

```bash
git add src/main/java/com/nihongodev/platform/application/usecase/UpdateProgressOnQuizCompletedUseCase.java \
        src/main/java/com/nihongodev/platform/application/usecase/UpdateProgressOnLessonCompletedUseCase.java \
        src/main/java/com/nihongodev/platform/application/usecase/UpdateProgressOnInterviewCompletedUseCase.java \
        src/main/java/com/nihongodev/platform/application/usecase/UpdateProgressOnCorrectionCompletedUseCase.java \
        src/test/java/com/nihongodev/platform/application/usecase/UpdateProgressOnQuizCompletedUseCaseTest.java \
        src/test/java/com/nihongodev/platform/application/usecase/UpdateProgressOnLessonCompletedUseCaseTest.java
git commit -m "feat(progress): add write use cases for event-driven progress updates"
```

---

## Task 9: Read Use Cases and RecalculateStatistics (with TDD)

**Files:**
- Create: `src/main/java/com/nihongodev/platform/application/usecase/GetUserProgressUseCase.java`
- Create: `src/main/java/com/nihongodev/platform/application/usecase/GetModuleProgressUseCase.java`
- Create: `src/main/java/com/nihongodev/platform/application/usecase/GetUserActivityHistoryUseCase.java`
- Create: `src/main/java/com/nihongodev/platform/application/usecase/GetUserStatisticsUseCase.java`
- Create: `src/main/java/com/nihongodev/platform/application/usecase/GetPlatformAnalyticsUseCase.java`
- Create: `src/main/java/com/nihongodev/platform/application/usecase/RecalculateStatisticsUseCase.java`
- Create: `src/test/java/com/nihongodev/platform/application/usecase/RecalculateStatisticsUseCaseTest.java`
- Create: `src/test/java/com/nihongodev/platform/application/usecase/GetUserProgressUseCaseTest.java`

- [ ] **Step 1: Write failing test for RecalculateStatisticsUseCase**

```java
package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.port.out.LearningActivityRepositoryPort;
import com.nihongodev.platform.application.port.out.StatisticsRepositoryPort;
import com.nihongodev.platform.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RecalculateStatisticsUseCase")
class RecalculateStatisticsUseCaseTest {

    @Mock private StatisticsRepositoryPort statisticsRepository;
    @Mock private LearningActivityRepositoryPort activityRepository;

    private RecalculateStatisticsUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new RecalculateStatisticsUseCase(statisticsRepository, activityRepository);
    }

    @Test
    @DisplayName("should recalculate statistics for active users")
    void shouldRecalculateForActiveUsers() {
        UUID userId = UUID.randomUUID();
        when(statisticsRepository.findUserIdsWithActivitySince(any())).thenReturn(List.of(userId));
        when(statisticsRepository.findByUserId(userId)).thenReturn(Optional.empty());

        LearningActivity activity1 = LearningActivity.create(userId, ActivityType.QUIZ_COMPLETED, UUID.randomUUID(), 80.0, 110);
        activity1.setOccurredAt(LocalDateTime.now().minusDays(2));
        LearningActivity activity2 = LearningActivity.create(userId, ActivityType.QUIZ_COMPLETED, UUID.randomUUID(), 60.0, 90);
        activity2.setOccurredAt(LocalDateTime.now().minusDays(1));

        when(activityRepository.findByUserIdAndOccurredAfter(eq(userId), any()))
                .thenReturn(List.of(activity1, activity2));
        when(activityRepository.countDistinctDaysActiveAfter(eq(userId), any()))
                .thenReturn(2L);
        when(statisticsRepository.save(any(UserStatistics.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.recalculateAll();

        ArgumentCaptor<UserStatistics> captor = ArgumentCaptor.forClass(UserStatistics.class);
        verify(statisticsRepository).save(captor.capture());
        UserStatistics saved = captor.getValue();
        assertThat(saved.getAverageScore7Days()).isEqualTo(70.0);
        assertThat(saved.getUserId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("should detect IMPROVING trend")
    void shouldDetectImprovingTrend() {
        UUID userId = UUID.randomUUID();
        when(statisticsRepository.findUserIdsWithActivitySince(any())).thenReturn(List.of(userId));
        when(statisticsRepository.findByUserId(userId)).thenReturn(Optional.empty());

        LearningActivity recent = LearningActivity.create(userId, ActivityType.QUIZ_COMPLETED, UUID.randomUUID(), 90.0, 120);
        recent.setOccurredAt(LocalDateTime.now().minusDays(1));
        LearningActivity older = LearningActivity.create(userId, ActivityType.QUIZ_COMPLETED, UUID.randomUUID(), 50.0, 65);
        older.setOccurredAt(LocalDateTime.now().minusDays(20));

        when(activityRepository.findByUserIdAndOccurredAfter(eq(userId), any()))
                .thenReturn(List.of(recent, older));
        when(activityRepository.countDistinctDaysActiveAfter(eq(userId), any()))
                .thenReturn(2L);
        when(statisticsRepository.save(any(UserStatistics.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.recalculateAll();

        ArgumentCaptor<UserStatistics> captor = ArgumentCaptor.forClass(UserStatistics.class);
        verify(statisticsRepository).save(captor.capture());
        assertThat(captor.getValue().getProgressTrend()).isEqualTo(Trend.IMPROVING);
    }

    @Test
    @DisplayName("should identify weak areas below 75%")
    void shouldIdentifyWeakAreas() {
        UUID userId = UUID.randomUUID();
        when(statisticsRepository.findUserIdsWithActivitySince(any())).thenReturn(List.of(userId));
        when(statisticsRepository.findByUserId(userId)).thenReturn(Optional.empty());

        List<LearningActivity> activities = List.of(
                createActivity(userId, ActivityType.QUIZ_COMPLETED, 50.0, 1),
                createActivity(userId, ActivityType.QUIZ_COMPLETED, 55.0, 2),
                createActivity(userId, ActivityType.QUIZ_COMPLETED, 45.0, 3),
                createActivity(userId, ActivityType.QUIZ_COMPLETED, 40.0, 4)
        );

        when(activityRepository.findByUserIdAndOccurredAfter(eq(userId), any())).thenReturn(activities);
        when(activityRepository.countDistinctDaysActiveAfter(eq(userId), any())).thenReturn(4L);
        when(statisticsRepository.save(any(UserStatistics.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.recalculateAll();

        ArgumentCaptor<UserStatistics> captor = ArgumentCaptor.forClass(UserStatistics.class);
        verify(statisticsRepository).save(captor.capture());
        assertThat(captor.getValue().getWeakAreas()).isNotEmpty();
        assertThat(captor.getValue().getWeakAreas().get(0).getPriority()).isEqualTo(Priority.HIGH);
    }

    private LearningActivity createActivity(UUID userId, ActivityType type, double score, int daysAgo) {
        LearningActivity a = LearningActivity.create(userId, type, UUID.randomUUID(), score, 50);
        a.setOccurredAt(LocalDateTime.now().minusDays(daysAgo));
        return a;
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `mvn test -pl . -Dtest=RecalculateStatisticsUseCaseTest -f pom.xml`
Expected: FAIL — class does not exist

- [ ] **Step 3: Implement RecalculateStatisticsUseCase**

```java
package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.port.in.RecalculateStatisticsPort;
import com.nihongodev.platform.application.port.out.LearningActivityRepositoryPort;
import com.nihongodev.platform.application.port.out.StatisticsRepositoryPort;
import com.nihongodev.platform.domain.model.LearningActivity;
import com.nihongodev.platform.domain.model.UserStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RecalculateStatisticsUseCase implements RecalculateStatisticsPort {

    private static final Logger log = LoggerFactory.getLogger(RecalculateStatisticsUseCase.class);

    private final StatisticsRepositoryPort statisticsRepository;
    private final LearningActivityRepositoryPort activityRepository;

    public RecalculateStatisticsUseCase(StatisticsRepositoryPort statisticsRepository,
                                        LearningActivityRepositoryPort activityRepository) {
        this.statisticsRepository = statisticsRepository;
        this.activityRepository = activityRepository;
    }

    @Override
    @Transactional
    public void recalculateAll() {
        LocalDateTime since = LocalDateTime.now().minusMinutes(20);
        List<UUID> activeUserIds = statisticsRepository.findUserIdsWithActivitySince(since);

        log.info("Recalculating statistics for {} active users", activeUserIds.size());

        for (UUID userId : activeUserIds) {
            recalculateForUser(userId);
        }
    }

    private void recalculateForUser(UUID userId) {
        UserStatistics stats = statisticsRepository.findByUserId(userId)
                .orElseGet(() -> UserStatistics.initialize(userId));

        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<LearningActivity> activities = activityRepository
                .findByUserIdAndOccurredAfter(userId, thirtyDaysAgo);

        long daysActive7 = activityRepository.countDistinctDaysActiveAfter(
                userId, LocalDateTime.now().minusDays(7));
        long daysActive30 = activityRepository.countDistinctDaysActiveAfter(
                userId, thirtyDaysAgo);

        stats.recalculate(activities, (int) daysActive7, (int) daysActive30);
        statisticsRepository.save(stats);
    }
}
```

- [ ] **Step 4: Implement GetUserProgressUseCase**

```java
package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.UserProgressDto;
import com.nihongodev.platform.application.port.in.GetUserProgressPort;
import com.nihongodev.platform.application.port.out.ProgressRepositoryPort;
import com.nihongodev.platform.domain.model.ProgressLevel;
import com.nihongodev.platform.domain.model.UserProgress;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetUserProgressUseCase implements GetUserProgressPort {

    private final ProgressRepositoryPort progressRepository;

    public GetUserProgressUseCase(ProgressRepositoryPort progressRepository) {
        this.progressRepository = progressRepository;
    }

    @Override
    public UserProgressDto execute(UUID userId) {
        UserProgress progress = progressRepository.findByUserId(userId)
                .orElseGet(() -> UserProgress.initialize(userId));

        return new UserProgressDto(
                progress.getUserId(),
                progress.getTotalLessonsCompleted(),
                progress.getTotalQuizzesCompleted(),
                progress.getTotalInterviewsCompleted(),
                progress.getTotalCorrectionsCompleted(),
                progress.getGlobalScore(),
                progress.getCurrentStreak(),
                progress.getLongestStreak(),
                progress.getLevel(),
                progress.getTotalXp(),
                progress.getLastActivityAt()
        );
    }
}
```

- [ ] **Step 5: Implement GetModuleProgressUseCase**

```java
package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.ModuleProgressDto;
import com.nihongodev.platform.application.port.in.GetModuleProgressPort;
import com.nihongodev.platform.application.port.out.ModuleProgressRepositoryPort;
import com.nihongodev.platform.domain.model.ModuleProgress;
import com.nihongodev.platform.domain.model.ModuleType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GetModuleProgressUseCase implements GetModuleProgressPort {

    private final ModuleProgressRepositoryPort moduleProgressRepository;

    public GetModuleProgressUseCase(ModuleProgressRepositoryPort moduleProgressRepository) {
        this.moduleProgressRepository = moduleProgressRepository;
    }

    @Override
    public List<ModuleProgressDto> getAll(UUID userId) {
        return moduleProgressRepository.findAllByUserId(userId).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public ModuleProgressDto getByType(UUID userId, ModuleType moduleType) {
        ModuleProgress mp = moduleProgressRepository.findByUserIdAndModuleType(userId, moduleType)
                .orElseGet(() -> ModuleProgress.initialize(userId, moduleType));
        return toDto(mp);
    }

    private ModuleProgressDto toDto(ModuleProgress mp) {
        return new ModuleProgressDto(
                mp.getModuleType(),
                mp.getCompletedItems(),
                mp.getTotalItems(),
                mp.getCompletionPercentage(),
                mp.getAverageScore(),
                mp.getBestScore(),
                mp.getStatus(),
                mp.getLastCompletedAt()
        );
    }
}
```

- [ ] **Step 6: Implement GetUserActivityHistoryUseCase**

```java
package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.LearningActivityDto;
import com.nihongodev.platform.application.port.in.GetUserActivityHistoryPort;
import com.nihongodev.platform.application.port.out.LearningActivityRepositoryPort;
import com.nihongodev.platform.domain.model.LearningActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetUserActivityHistoryUseCase implements GetUserActivityHistoryPort {

    private final LearningActivityRepositoryPort activityRepository;

    public GetUserActivityHistoryUseCase(LearningActivityRepositoryPort activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Override
    public Page<LearningActivityDto> execute(UUID userId, Pageable pageable) {
        return activityRepository.findByUserId(userId, pageable)
                .map(this::toDto);
    }

    private LearningActivityDto toDto(LearningActivity a) {
        return new LearningActivityDto(
                a.getActivityType(),
                a.getReferenceId(),
                a.getScore(),
                a.getXpEarned(),
                a.getMetadata(),
                a.getOccurredAt()
        );
    }
}
```

- [ ] **Step 7: Implement GetUserStatisticsUseCase**

```java
package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.*;
import com.nihongodev.platform.application.port.in.GetUserStatisticsPort;
import com.nihongodev.platform.application.port.out.StatisticsRepositoryPort;
import com.nihongodev.platform.domain.model.UserStatistics;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GetUserStatisticsUseCase implements GetUserStatisticsPort {

    private final StatisticsRepositoryPort statisticsRepository;

    public GetUserStatisticsUseCase(StatisticsRepositoryPort statisticsRepository) {
        this.statisticsRepository = statisticsRepository;
    }

    @Override
    public UserStatisticsDto execute(UUID userId) {
        UserStatistics stats = statisticsRepository.findByUserId(userId)
                .orElseGet(() -> UserStatistics.initialize(userId));

        List<WeakAreaDto> weakAreas = stats.getWeakAreas().stream()
                .map(w -> new WeakAreaDto(w.getModuleType(), w.getTopic(), w.getAverageScore(), w.getPriority()))
                .toList();

        List<RecommendationDto> recommendations = stats.getRecommendations().stream()
                .map(r -> new RecommendationDto(r.getType(), r.getTargetId(), r.getReason(), r.getPriority()))
                .toList();

        return new UserStatisticsDto(
                stats.getAverageScore7Days(),
                stats.getAverageScore30Days(),
                stats.getAverageScoreAllTime(),
                stats.getLearningVelocity(),
                stats.getConsistencyRate(),
                stats.getProgressTrend(),
                weakAreas,
                recommendations
        );
    }
}
```

- [ ] **Step 8: Implement GetPlatformAnalyticsUseCase**

```java
package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.PlatformAnalyticsDto;
import com.nihongodev.platform.application.dto.TopUserDto;
import com.nihongodev.platform.application.port.in.GetPlatformAnalyticsPort;
import com.nihongodev.platform.application.port.out.AnalyticsQueryPort;
import com.nihongodev.platform.domain.model.ModuleType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class GetPlatformAnalyticsUseCase implements GetPlatformAnalyticsPort {

    private final AnalyticsQueryPort analyticsQueryPort;

    public GetPlatformAnalyticsUseCase(AnalyticsQueryPort analyticsQueryPort) {
        this.analyticsQueryPort = analyticsQueryPort;
    }

    @Override
    public PlatformAnalyticsDto getOverview() {
        long activeUsers7d = analyticsQueryPort.countActiveUsers(LocalDateTime.now().minusDays(7));
        long activeUsers30d = analyticsQueryPort.countActiveUsers(LocalDateTime.now().minusDays(30));
        double avgScore = analyticsQueryPort.averageGlobalScore();
        long totalActivities = analyticsQueryPort.countTotalActivities();
        Map<ModuleType, Double> scoreByModule = analyticsQueryPort.averageScoreByModule();

        long totalUsers = activeUsers30d;

        return new PlatformAnalyticsDto(
                totalUsers, activeUsers7d, activeUsers30d,
                avgScore, totalActivities, scoreByModule
        );
    }

    @Override
    public List<TopUserDto> getTopUsers(int limit) {
        return analyticsQueryPort.topUsers(limit);
    }
}
```

- [ ] **Step 9: Write test for GetUserProgressUseCase**

```java
package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.UserProgressDto;
import com.nihongodev.platform.application.port.out.ProgressRepositoryPort;
import com.nihongodev.platform.domain.model.ProgressLevel;
import com.nihongodev.platform.domain.model.UserProgress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetUserProgressUseCase")
class GetUserProgressUseCaseTest {

    @Mock private ProgressRepositoryPort progressRepository;

    private GetUserProgressUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetUserProgressUseCase(progressRepository);
    }

    @Test
    @DisplayName("should return user progress when exists")
    void shouldReturnProgress() {
        UUID userId = UUID.randomUUID();
        UserProgress progress = UserProgress.initialize(userId);
        progress.setTotalXp(2500);
        progress.setLevel(ProgressLevel.INTERMEDIATE);
        progress.setTotalQuizzesCompleted(10);

        when(progressRepository.findByUserId(userId)).thenReturn(Optional.of(progress));

        UserProgressDto dto = useCase.execute(userId);

        assertThat(dto.userId()).isEqualTo(userId);
        assertThat(dto.totalXp()).isEqualTo(2500);
        assertThat(dto.level()).isEqualTo(ProgressLevel.INTERMEDIATE);
        assertThat(dto.totalQuizzesCompleted()).isEqualTo(10);
    }

    @Test
    @DisplayName("should return initialized progress for new user")
    void shouldReturnInitializedForNewUser() {
        UUID userId = UUID.randomUUID();
        when(progressRepository.findByUserId(userId)).thenReturn(Optional.empty());

        UserProgressDto dto = useCase.execute(userId);

        assertThat(dto.userId()).isEqualTo(userId);
        assertThat(dto.totalXp()).isZero();
        assertThat(dto.level()).isEqualTo(ProgressLevel.BEGINNER);
    }
}
```

- [ ] **Step 10: Run all tests**

Run: `mvn test -pl . -Dtest="RecalculateStatisticsUseCaseTest,GetUserProgressUseCaseTest" -f pom.xml`
Expected: ALL PASS

- [ ] **Step 11: Commit**

```bash
git add src/main/java/com/nihongodev/platform/application/usecase/GetUserProgressUseCase.java \
        src/main/java/com/nihongodev/platform/application/usecase/GetModuleProgressUseCase.java \
        src/main/java/com/nihongodev/platform/application/usecase/GetUserActivityHistoryUseCase.java \
        src/main/java/com/nihongodev/platform/application/usecase/GetUserStatisticsUseCase.java \
        src/main/java/com/nihongodev/platform/application/usecase/GetPlatformAnalyticsUseCase.java \
        src/main/java/com/nihongodev/platform/application/usecase/RecalculateStatisticsUseCase.java \
        src/test/java/com/nihongodev/platform/application/usecase/RecalculateStatisticsUseCaseTest.java \
        src/test/java/com/nihongodev/platform/application/usecase/GetUserProgressUseCaseTest.java
git commit -m "feat(progress): add read use cases and batch statistics recalculation"
```

---

## Task 10: Flyway Migration V7

**Files:**
- Create: `src/main/resources/db/migration/V7__progress_analytics.sql`

- [ ] **Step 1: Create migration file**

```sql
-- V7: Progress & Analytics Module
-- Tables: user_progress, module_progress, learning_activities, user_statistics

-- User Progress (global progress aggregate)
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
    scored_activities_count INT NOT NULL DEFAULT 0,
    weighted_score_sum DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    weight_sum DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_user_progress_level ON user_progress(level);
CREATE INDEX idx_user_progress_xp ON user_progress(total_xp DESC);

-- Module Progress (per-module tracking)
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

CREATE INDEX idx_module_progress_user ON module_progress(user_id);

-- Learning Activities (immutable activity log)
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

CREATE INDEX idx_learning_activities_user_date ON learning_activities(user_id, occurred_at DESC);
CREATE INDEX idx_learning_activities_reference ON learning_activities(user_id, reference_id, activity_type);
CREATE INDEX idx_learning_activities_type ON learning_activities(activity_type);

-- User Statistics (read-side projection, batch-updated)
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

CREATE INDEX idx_user_statistics_user ON user_statistics(user_id);
```

- [ ] **Step 2: Commit**

```bash
git add src/main/resources/db/migration/V7__progress_analytics.sql
git commit -m "feat(progress): add Flyway V7 migration for progress & analytics tables"
```

---

## Task 11: JPA Entities

**Files:**
- Create: `src/main/java/com/nihongodev/platform/infrastructure/persistence/entity/UserProgressEntity.java`
- Create: `src/main/java/com/nihongodev/platform/infrastructure/persistence/entity/ModuleProgressEntity.java`
- Create: `src/main/java/com/nihongodev/platform/infrastructure/persistence/entity/LearningActivityEntity.java`
- Create: `src/main/java/com/nihongodev/platform/infrastructure/persistence/entity/UserStatisticsEntity.java`

- [ ] **Step 1: Create UserProgressEntity**

```java
package com.nihongodev.platform.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_progress")
public class UserProgressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "total_lessons_completed", nullable = false)
    private int totalLessonsCompleted;

    @Column(name = "total_quizzes_completed", nullable = false)
    private int totalQuizzesCompleted;

    @Column(name = "total_interviews_completed", nullable = false)
    private int totalInterviewsCompleted;

    @Column(name = "total_corrections_completed", nullable = false)
    private int totalCorrectionsCompleted;

    @Column(name = "global_score", nullable = false)
    private double globalScore;

    @Column(name = "current_streak", nullable = false)
    private int currentStreak;

    @Column(name = "longest_streak", nullable = false)
    private int longestStreak;

    @Column(name = "last_activity_at")
    private LocalDateTime lastActivityAt;

    @Column(name = "level", nullable = false, length = 20)
    private String level;

    @Column(name = "total_xp", nullable = false)
    private long totalXp;

    @Column(name = "scored_activities_count", nullable = false)
    private int scoredActivitiesCount;

    @Column(name = "weighted_score_sum", nullable = false)
    private double weightedScoreSum;

    @Column(name = "weight_sum", nullable = false)
    private double weightSum;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public int getTotalLessonsCompleted() { return totalLessonsCompleted; }
    public void setTotalLessonsCompleted(int v) { this.totalLessonsCompleted = v; }
    public int getTotalQuizzesCompleted() { return totalQuizzesCompleted; }
    public void setTotalQuizzesCompleted(int v) { this.totalQuizzesCompleted = v; }
    public int getTotalInterviewsCompleted() { return totalInterviewsCompleted; }
    public void setTotalInterviewsCompleted(int v) { this.totalInterviewsCompleted = v; }
    public int getTotalCorrectionsCompleted() { return totalCorrectionsCompleted; }
    public void setTotalCorrectionsCompleted(int v) { this.totalCorrectionsCompleted = v; }
    public double getGlobalScore() { return globalScore; }
    public void setGlobalScore(double v) { this.globalScore = v; }
    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int v) { this.currentStreak = v; }
    public int getLongestStreak() { return longestStreak; }
    public void setLongestStreak(int v) { this.longestStreak = v; }
    public LocalDateTime getLastActivityAt() { return lastActivityAt; }
    public void setLastActivityAt(LocalDateTime v) { this.lastActivityAt = v; }
    public String getLevel() { return level; }
    public void setLevel(String v) { this.level = v; }
    public long getTotalXp() { return totalXp; }
    public void setTotalXp(long v) { this.totalXp = v; }
    public int getScoredActivitiesCount() { return scoredActivitiesCount; }
    public void setScoredActivitiesCount(int v) { this.scoredActivitiesCount = v; }
    public double getWeightedScoreSum() { return weightedScoreSum; }
    public void setWeightedScoreSum(double v) { this.weightedScoreSum = v; }
    public double getWeightSum() { return weightSum; }
    public void setWeightSum(double v) { this.weightSum = v; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime v) { this.updatedAt = v; }
}
```

- [ ] **Step 2: Create ModuleProgressEntity**

```java
package com.nihongodev.platform.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "module_progress", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "module_type"}))
public class ModuleProgressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "module_type", nullable = false, length = 30)
    private String moduleType;

    @Column(name = "completed_items", nullable = false)
    private int completedItems;

    @Column(name = "total_items")
    private Integer totalItems;

    @Column(name = "average_score", nullable = false)
    private double averageScore;

    @Column(name = "best_score", nullable = false)
    private double bestScore;

    @Column(name = "last_completed_at")
    private LocalDateTime lastCompletedAt;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getModuleType() { return moduleType; }
    public void setModuleType(String moduleType) { this.moduleType = moduleType; }
    public int getCompletedItems() { return completedItems; }
    public void setCompletedItems(int completedItems) { this.completedItems = completedItems; }
    public Integer getTotalItems() { return totalItems; }
    public void setTotalItems(Integer totalItems) { this.totalItems = totalItems; }
    public double getAverageScore() { return averageScore; }
    public void setAverageScore(double averageScore) { this.averageScore = averageScore; }
    public double getBestScore() { return bestScore; }
    public void setBestScore(double bestScore) { this.bestScore = bestScore; }
    public LocalDateTime getLastCompletedAt() { return lastCompletedAt; }
    public void setLastCompletedAt(LocalDateTime lastCompletedAt) { this.lastCompletedAt = lastCompletedAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
```

- [ ] **Step 3: Create LearningActivityEntity**

```java
package com.nihongodev.platform.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "learning_activities", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "reference_id", "activity_type"}))
public class LearningActivityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "activity_type", nullable = false, length = 30)
    private String activityType;

    @Column(name = "reference_id", nullable = false)
    private UUID referenceId;

    @Column(name = "score")
    private Double score;

    @Column(name = "xp_earned", nullable = false)
    private int xpEarned;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }
    public UUID getReferenceId() { return referenceId; }
    public void setReferenceId(UUID referenceId) { this.referenceId = referenceId; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    public int getXpEarned() { return xpEarned; }
    public void setXpEarned(int xpEarned) { this.xpEarned = xpEarned; }
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }
}
```

- [ ] **Step 4: Create UserStatisticsEntity**

```java
package com.nihongodev.platform.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_statistics")
public class UserStatisticsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "average_score_7_days", nullable = false)
    private double averageScore7Days;

    @Column(name = "average_score_30_days", nullable = false)
    private double averageScore30Days;

    @Column(name = "average_score_all_time", nullable = false)
    private double averageScoreAllTime;

    @Column(name = "learning_velocity", nullable = false)
    private double learningVelocity;

    @Column(name = "consistency_rate", nullable = false)
    private double consistencyRate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "weak_areas", columnDefinition = "jsonb")
    private String weakAreas;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "recommendations", columnDefinition = "jsonb")
    private String recommendations;

    @Column(name = "progress_trend", nullable = false, length = 20)
    private String progressTrend;

    @Column(name = "last_calculated_at", nullable = false)
    private LocalDateTime lastCalculatedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public double getAverageScore7Days() { return averageScore7Days; }
    public void setAverageScore7Days(double v) { this.averageScore7Days = v; }
    public double getAverageScore30Days() { return averageScore30Days; }
    public void setAverageScore30Days(double v) { this.averageScore30Days = v; }
    public double getAverageScoreAllTime() { return averageScoreAllTime; }
    public void setAverageScoreAllTime(double v) { this.averageScoreAllTime = v; }
    public double getLearningVelocity() { return learningVelocity; }
    public void setLearningVelocity(double v) { this.learningVelocity = v; }
    public double getConsistencyRate() { return consistencyRate; }
    public void setConsistencyRate(double v) { this.consistencyRate = v; }
    public String getWeakAreas() { return weakAreas; }
    public void setWeakAreas(String v) { this.weakAreas = v; }
    public String getRecommendations() { return recommendations; }
    public void setRecommendations(String v) { this.recommendations = v; }
    public String getProgressTrend() { return progressTrend; }
    public void setProgressTrend(String v) { this.progressTrend = v; }
    public LocalDateTime getLastCalculatedAt() { return lastCalculatedAt; }
    public void setLastCalculatedAt(LocalDateTime v) { this.lastCalculatedAt = v; }
}
```

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/nihongodev/platform/infrastructure/persistence/entity/UserProgressEntity.java \
        src/main/java/com/nihongodev/platform/infrastructure/persistence/entity/ModuleProgressEntity.java \
        src/main/java/com/nihongodev/platform/infrastructure/persistence/entity/LearningActivityEntity.java \
        src/main/java/com/nihongodev/platform/infrastructure/persistence/entity/UserStatisticsEntity.java
git commit -m "feat(progress): add JPA entities for progress & analytics tables"
```

---

## Task 12: JPA Repositories, Mappers, and Adapters

**Files:**
- Create: `src/main/java/com/nihongodev/platform/infrastructure/persistence/repository/JpaUserProgressRepository.java`
- Create: `src/main/java/com/nihongodev/platform/infrastructure/persistence/repository/JpaModuleProgressRepository.java`
- Create: `src/main/java/com/nihongodev/platform/infrastructure/persistence/repository/JpaLearningActivityRepository.java`
- Create: `src/main/java/com/nihongodev/platform/infrastructure/persistence/repository/JpaUserStatisticsRepository.java`
- Create: `src/main/java/com/nihongodev/platform/infrastructure/persistence/mapper/UserProgressPersistenceMapper.java`
- Create: `src/main/java/com/nihongodev/platform/infrastructure/persistence/mapper/ModuleProgressPersistenceMapper.java`
- Create: `src/main/java/com/nihongodev/platform/infrastructure/persistence/mapper/LearningActivityPersistenceMapper.java`
- Create: `src/main/java/com/nihongodev/platform/infrastructure/persistence/mapper/UserStatisticsPersistenceMapper.java`
- Create: `src/main/java/com/nihongodev/platform/infrastructure/persistence/adapter/ProgressRepositoryAdapter.java`
- Create: `src/main/java/com/nihongodev/platform/infrastructure/persistence/adapter/ModuleProgressRepositoryAdapter.java`
- Create: `src/main/java/com/nihongodev/platform/infrastructure/persistence/adapter/LearningActivityRepositoryAdapter.java`
- Create: `src/main/java/com/nihongodev/platform/infrastructure/persistence/adapter/StatisticsRepositoryAdapter.java`
- Create: `src/main/java/com/nihongodev/platform/infrastructure/persistence/adapter/AnalyticsQueryAdapter.java`

- [ ] **Step 1: Create JPA Repositories**

```java
// JpaUserProgressRepository.java
package com.nihongodev.platform.infrastructure.persistence.repository;

import com.nihongodev.platform.infrastructure.persistence.entity.UserProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface JpaUserProgressRepository extends JpaRepository<UserProgressEntity, UUID> {
    Optional<UserProgressEntity> findByUserId(UUID userId);
}
```

```java
// JpaModuleProgressRepository.java
package com.nihongodev.platform.infrastructure.persistence.repository;

import com.nihongodev.platform.infrastructure.persistence.entity.ModuleProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaModuleProgressRepository extends JpaRepository<ModuleProgressEntity, UUID> {
    Optional<ModuleProgressEntity> findByUserIdAndModuleType(UUID userId, String moduleType);
    List<ModuleProgressEntity> findAllByUserId(UUID userId);
}
```

```java
// JpaLearningActivityRepository.java
package com.nihongodev.platform.infrastructure.persistence.repository;

import com.nihongodev.platform.infrastructure.persistence.entity.LearningActivityEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface JpaLearningActivityRepository extends JpaRepository<LearningActivityEntity, UUID> {
    Page<LearningActivityEntity> findByUserIdOrderByOccurredAtDesc(UUID userId, Pageable pageable);
    List<LearningActivityEntity> findByUserIdAndOccurredAtAfter(UUID userId, LocalDateTime after);
    boolean existsByUserIdAndReferenceIdAndActivityType(UUID userId, UUID referenceId, String activityType);

    @Query("SELECT COUNT(DISTINCT CAST(a.occurredAt AS date)) FROM LearningActivityEntity a WHERE a.userId = :userId AND a.occurredAt > :after")
    long countDistinctDaysActiveAfter(@Param("userId") UUID userId, @Param("after") LocalDateTime after);
}
```

```java
// JpaUserStatisticsRepository.java
package com.nihongodev.platform.infrastructure.persistence.repository;

import com.nihongodev.platform.infrastructure.persistence.entity.UserStatisticsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaUserStatisticsRepository extends JpaRepository<UserStatisticsEntity, UUID> {
    Optional<UserStatisticsEntity> findByUserId(UUID userId);

    @Query("SELECT DISTINCT a.userId FROM LearningActivityEntity a WHERE a.occurredAt > :since")
    List<UUID> findUserIdsWithActivitySince(@Param("since") LocalDateTime since);
}
```

- [ ] **Step 2: Create Persistence Mappers**

```java
// UserProgressPersistenceMapper.java
package com.nihongodev.platform.infrastructure.persistence.mapper;

import com.nihongodev.platform.domain.model.ProgressLevel;
import com.nihongodev.platform.domain.model.UserProgress;
import com.nihongodev.platform.infrastructure.persistence.entity.UserProgressEntity;
import org.springframework.stereotype.Component;

@Component
public class UserProgressPersistenceMapper {

    public UserProgress toDomain(UserProgressEntity entity) {
        if (entity == null) return null;
        UserProgress p = new UserProgress();
        p.setId(entity.getId());
        p.setUserId(entity.getUserId());
        p.setTotalLessonsCompleted(entity.getTotalLessonsCompleted());
        p.setTotalQuizzesCompleted(entity.getTotalQuizzesCompleted());
        p.setTotalInterviewsCompleted(entity.getTotalInterviewsCompleted());
        p.setTotalCorrectionsCompleted(entity.getTotalCorrectionsCompleted());
        p.setGlobalScore(entity.getGlobalScore());
        p.setCurrentStreak(entity.getCurrentStreak());
        p.setLongestStreak(entity.getLongestStreak());
        p.setLastActivityAt(entity.getLastActivityAt());
        p.setLevel(entity.getLevel() != null ? ProgressLevel.valueOf(entity.getLevel()) : ProgressLevel.BEGINNER);
        p.setTotalXp(entity.getTotalXp());
        p.setScoredActivitiesCount(entity.getScoredActivitiesCount());
        p.setWeightedScoreSum(entity.getWeightedScoreSum());
        p.setWeightSum(entity.getWeightSum());
        p.setCreatedAt(entity.getCreatedAt());
        p.setUpdatedAt(entity.getUpdatedAt());
        return p;
    }

    public UserProgressEntity toEntity(UserProgress p) {
        if (p == null) return null;
        UserProgressEntity entity = new UserProgressEntity();
        entity.setId(p.getId());
        entity.setUserId(p.getUserId());
        entity.setTotalLessonsCompleted(p.getTotalLessonsCompleted());
        entity.setTotalQuizzesCompleted(p.getTotalQuizzesCompleted());
        entity.setTotalInterviewsCompleted(p.getTotalInterviewsCompleted());
        entity.setTotalCorrectionsCompleted(p.getTotalCorrectionsCompleted());
        entity.setGlobalScore(p.getGlobalScore());
        entity.setCurrentStreak(p.getCurrentStreak());
        entity.setLongestStreak(p.getLongestStreak());
        entity.setLastActivityAt(p.getLastActivityAt());
        entity.setLevel(p.getLevel() != null ? p.getLevel().name() : "BEGINNER");
        entity.setTotalXp(p.getTotalXp());
        entity.setScoredActivitiesCount(p.getScoredActivitiesCount());
        entity.setWeightedScoreSum(p.getWeightedScoreSum());
        entity.setWeightSum(p.getWeightSum());
        entity.setCreatedAt(p.getCreatedAt());
        entity.setUpdatedAt(p.getUpdatedAt());
        return entity;
    }
}
```

```java
// ModuleProgressPersistenceMapper.java
package com.nihongodev.platform.infrastructure.persistence.mapper;

import com.nihongodev.platform.domain.model.ModuleProgress;
import com.nihongodev.platform.domain.model.ModuleStatus;
import com.nihongodev.platform.domain.model.ModuleType;
import com.nihongodev.platform.infrastructure.persistence.entity.ModuleProgressEntity;
import org.springframework.stereotype.Component;

@Component
public class ModuleProgressPersistenceMapper {

    public ModuleProgress toDomain(ModuleProgressEntity entity) {
        if (entity == null) return null;
        ModuleProgress mp = new ModuleProgress();
        mp.setId(entity.getId());
        mp.setUserId(entity.getUserId());
        mp.setModuleType(ModuleType.valueOf(entity.getModuleType()));
        mp.setCompletedItems(entity.getCompletedItems());
        mp.setTotalItems(entity.getTotalItems());
        mp.setAverageScore(entity.getAverageScore());
        mp.setBestScore(entity.getBestScore());
        mp.setLastCompletedAt(entity.getLastCompletedAt());
        mp.setStatus(entity.getStatus() != null ? ModuleStatus.valueOf(entity.getStatus()) : ModuleStatus.NOT_STARTED);
        return mp;
    }

    public ModuleProgressEntity toEntity(ModuleProgress mp) {
        if (mp == null) return null;
        ModuleProgressEntity entity = new ModuleProgressEntity();
        entity.setId(mp.getId());
        entity.setUserId(mp.getUserId());
        entity.setModuleType(mp.getModuleType() != null ? mp.getModuleType().name() : null);
        entity.setCompletedItems(mp.getCompletedItems());
        entity.setTotalItems(mp.getTotalItems());
        entity.setAverageScore(mp.getAverageScore());
        entity.setBestScore(mp.getBestScore());
        entity.setLastCompletedAt(mp.getLastCompletedAt());
        entity.setStatus(mp.getStatus() != null ? mp.getStatus().name() : "NOT_STARTED");
        return entity;
    }
}
```

```java
// LearningActivityPersistenceMapper.java
package com.nihongodev.platform.infrastructure.persistence.mapper;

import com.nihongodev.platform.domain.model.ActivityType;
import com.nihongodev.platform.domain.model.LearningActivity;
import com.nihongodev.platform.infrastructure.persistence.entity.LearningActivityEntity;
import org.springframework.stereotype.Component;

@Component
public class LearningActivityPersistenceMapper {

    public LearningActivity toDomain(LearningActivityEntity entity) {
        if (entity == null) return null;
        LearningActivity a = new LearningActivity();
        a.setId(entity.getId());
        a.setUserId(entity.getUserId());
        a.setActivityType(ActivityType.valueOf(entity.getActivityType()));
        a.setReferenceId(entity.getReferenceId());
        a.setScore(entity.getScore());
        a.setXpEarned(entity.getXpEarned());
        a.setMetadata(entity.getMetadata());
        a.setOccurredAt(entity.getOccurredAt());
        return a;
    }

    public LearningActivityEntity toEntity(LearningActivity a) {
        if (a == null) return null;
        LearningActivityEntity entity = new LearningActivityEntity();
        entity.setId(a.getId());
        entity.setUserId(a.getUserId());
        entity.setActivityType(a.getActivityType() != null ? a.getActivityType().name() : null);
        entity.setReferenceId(a.getReferenceId());
        entity.setScore(a.getScore());
        entity.setXpEarned(a.getXpEarned());
        entity.setMetadata(a.getMetadata());
        entity.setOccurredAt(a.getOccurredAt());
        return entity;
    }
}
```

```java
// UserStatisticsPersistenceMapper.java
package com.nihongodev.platform.infrastructure.persistence.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nihongodev.platform.domain.model.*;
import com.nihongodev.platform.infrastructure.persistence.entity.UserStatisticsEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserStatisticsPersistenceMapper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public UserStatistics toDomain(UserStatisticsEntity entity) {
        if (entity == null) return null;
        UserStatistics s = new UserStatistics();
        s.setId(entity.getId());
        s.setUserId(entity.getUserId());
        s.setAverageScore7Days(entity.getAverageScore7Days());
        s.setAverageScore30Days(entity.getAverageScore30Days());
        s.setAverageScoreAllTime(entity.getAverageScoreAllTime());
        s.setLearningVelocity(entity.getLearningVelocity());
        s.setConsistencyRate(entity.getConsistencyRate());
        s.setWeakAreas(deserializeWeakAreas(entity.getWeakAreas()));
        s.setRecommendations(deserializeRecommendations(entity.getRecommendations()));
        s.setProgressTrend(entity.getProgressTrend() != null ? Trend.valueOf(entity.getProgressTrend()) : Trend.STABLE);
        s.setLastCalculatedAt(entity.getLastCalculatedAt());
        return s;
    }

    public UserStatisticsEntity toEntity(UserStatistics s) {
        if (s == null) return null;
        UserStatisticsEntity entity = new UserStatisticsEntity();
        entity.setId(s.getId());
        entity.setUserId(s.getUserId());
        entity.setAverageScore7Days(s.getAverageScore7Days());
        entity.setAverageScore30Days(s.getAverageScore30Days());
        entity.setAverageScoreAllTime(s.getAverageScoreAllTime());
        entity.setLearningVelocity(s.getLearningVelocity());
        entity.setConsistencyRate(s.getConsistencyRate());
        entity.setWeakAreas(serializeWeakAreas(s.getWeakAreas()));
        entity.setRecommendations(serializeRecommendations(s.getRecommendations()));
        entity.setProgressTrend(s.getProgressTrend() != null ? s.getProgressTrend().name() : "STABLE");
        entity.setLastCalculatedAt(s.getLastCalculatedAt());
        return entity;
    }

    private String serializeWeakAreas(List<WeakArea> weakAreas) {
        try { return objectMapper.writeValueAsString(weakAreas != null ? weakAreas : List.of()); }
        catch (Exception e) { return "[]"; }
    }

    private List<WeakArea> deserializeWeakAreas(String json) {
        try { return objectMapper.readValue(json != null ? json : "[]", new TypeReference<>() {}); }
        catch (Exception e) { return new ArrayList<>(); }
    }

    private String serializeRecommendations(List<Recommendation> recommendations) {
        try { return objectMapper.writeValueAsString(recommendations != null ? recommendations : List.of()); }
        catch (Exception e) { return "[]"; }
    }

    private List<Recommendation> deserializeRecommendations(String json) {
        try { return objectMapper.readValue(json != null ? json : "[]", new TypeReference<>() {}); }
        catch (Exception e) { return new ArrayList<>(); }
    }
}
```

- [ ] **Step 3: Create Repository Adapters**

```java
// ProgressRepositoryAdapter.java
package com.nihongodev.platform.infrastructure.persistence.adapter;

import com.nihongodev.platform.application.port.out.ProgressRepositoryPort;
import com.nihongodev.platform.domain.model.UserProgress;
import com.nihongodev.platform.infrastructure.persistence.mapper.UserProgressPersistenceMapper;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaUserProgressRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProgressRepositoryAdapter implements ProgressRepositoryPort {

    private final JpaUserProgressRepository jpaRepository;
    private final UserProgressPersistenceMapper mapper;

    public ProgressRepositoryAdapter(JpaUserProgressRepository jpaRepository, UserProgressPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public UserProgress save(UserProgress progress) {
        var entity = mapper.toEntity(progress);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<UserProgress> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).map(mapper::toDomain);
    }
}
```

```java
// ModuleProgressRepositoryAdapter.java
package com.nihongodev.platform.infrastructure.persistence.adapter;

import com.nihongodev.platform.application.port.out.ModuleProgressRepositoryPort;
import com.nihongodev.platform.domain.model.ModuleProgress;
import com.nihongodev.platform.domain.model.ModuleType;
import com.nihongodev.platform.infrastructure.persistence.mapper.ModuleProgressPersistenceMapper;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaModuleProgressRepository;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ModuleProgressRepositoryAdapter implements ModuleProgressRepositoryPort {

    private final JpaModuleProgressRepository jpaRepository;
    private final ModuleProgressPersistenceMapper mapper;

    public ModuleProgressRepositoryAdapter(JpaModuleProgressRepository jpaRepository, ModuleProgressPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public ModuleProgress save(ModuleProgress moduleProgress) {
        var entity = mapper.toEntity(moduleProgress);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<ModuleProgress> findByUserIdAndModuleType(UUID userId, ModuleType moduleType) {
        return jpaRepository.findByUserIdAndModuleType(userId, moduleType.name()).map(mapper::toDomain);
    }

    @Override
    public List<ModuleProgress> findAllByUserId(UUID userId) {
        return jpaRepository.findAllByUserId(userId).stream().map(mapper::toDomain).toList();
    }
}
```

```java
// LearningActivityRepositoryAdapter.java
package com.nihongodev.platform.infrastructure.persistence.adapter;

import com.nihongodev.platform.application.port.out.LearningActivityRepositoryPort;
import com.nihongodev.platform.domain.model.ActivityType;
import com.nihongodev.platform.domain.model.LearningActivity;
import com.nihongodev.platform.infrastructure.persistence.mapper.LearningActivityPersistenceMapper;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaLearningActivityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class LearningActivityRepositoryAdapter implements LearningActivityRepositoryPort {

    private final JpaLearningActivityRepository jpaRepository;
    private final LearningActivityPersistenceMapper mapper;

    public LearningActivityRepositoryAdapter(JpaLearningActivityRepository jpaRepository, LearningActivityPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public LearningActivity save(LearningActivity activity) {
        var entity = mapper.toEntity(activity);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Page<LearningActivity> findByUserId(UUID userId, Pageable pageable) {
        return jpaRepository.findByUserIdOrderByOccurredAtDesc(userId, pageable).map(mapper::toDomain);
    }

    @Override
    public List<LearningActivity> findByUserIdAndOccurredAfter(UUID userId, LocalDateTime after) {
        return jpaRepository.findByUserIdAndOccurredAtAfter(userId, after).stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByUserIdAndReferenceIdAndActivityType(UUID userId, UUID referenceId, ActivityType activityType) {
        return jpaRepository.existsByUserIdAndReferenceIdAndActivityType(userId, referenceId, activityType.name());
    }

    @Override
    public long countDistinctDaysActiveAfter(UUID userId, LocalDateTime after) {
        return jpaRepository.countDistinctDaysActiveAfter(userId, after);
    }
}
```

```java
// StatisticsRepositoryAdapter.java
package com.nihongodev.platform.infrastructure.persistence.adapter;

import com.nihongodev.platform.application.port.out.StatisticsRepositoryPort;
import com.nihongodev.platform.domain.model.UserStatistics;
import com.nihongodev.platform.infrastructure.persistence.mapper.UserStatisticsPersistenceMapper;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaUserStatisticsRepository;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class StatisticsRepositoryAdapter implements StatisticsRepositoryPort {

    private final JpaUserStatisticsRepository jpaRepository;
    private final UserStatisticsPersistenceMapper mapper;

    public StatisticsRepositoryAdapter(JpaUserStatisticsRepository jpaRepository, UserStatisticsPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public UserStatistics save(UserStatistics statistics) {
        var entity = mapper.toEntity(statistics);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<UserStatistics> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).map(mapper::toDomain);
    }

    @Override
    public List<UUID> findUserIdsWithActivitySince(LocalDateTime since) {
        return jpaRepository.findUserIdsWithActivitySince(since);
    }
}
```

```java
// AnalyticsQueryAdapter.java
package com.nihongodev.platform.infrastructure.persistence.adapter;

import com.nihongodev.platform.application.dto.TopUserDto;
import com.nihongodev.platform.application.port.out.AnalyticsQueryPort;
import com.nihongodev.platform.domain.model.ModuleType;
import com.nihongodev.platform.domain.model.ProgressLevel;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaLearningActivityRepository;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaUserProgressRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class AnalyticsQueryAdapter implements AnalyticsQueryPort {

    private final JpaUserProgressRepository progressRepository;
    private final JpaLearningActivityRepository activityRepository;

    public AnalyticsQueryAdapter(JpaUserProgressRepository progressRepository,
                                 JpaLearningActivityRepository activityRepository) {
        this.progressRepository = progressRepository;
        this.activityRepository = activityRepository;
    }

    @Override
    public long countActiveUsers(LocalDateTime since) {
        return activityRepository.findByUserIdAndOccurredAtAfter(null, since).size();
    }

    @Override
    public double averageGlobalScore() {
        return progressRepository.findAll().stream()
                .mapToDouble(e -> e.getGlobalScore())
                .average().orElse(0);
    }

    @Override
    public Map<ModuleType, Double> averageScoreByModule() {
        return new HashMap<>();
    }

    @Override
    public long countTotalActivities() {
        return activityRepository.count();
    }

    @Override
    public List<TopUserDto> topUsers(int limit) {
        return progressRepository.findAll(PageRequest.of(0, limit)).stream()
                .map(e -> new TopUserDto(
                        e.getUserId(), null, e.getTotalXp(), e.getGlobalScore(),
                        ProgressLevel.valueOf(e.getLevel())))
                .toList();
    }
}
```

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/nihongodev/platform/infrastructure/persistence/repository/JpaUserProgressRepository.java \
        src/main/java/com/nihongodev/platform/infrastructure/persistence/repository/JpaModuleProgressRepository.java \
        src/main/java/com/nihongodev/platform/infrastructure/persistence/repository/JpaLearningActivityRepository.java \
        src/main/java/com/nihongodev/platform/infrastructure/persistence/repository/JpaUserStatisticsRepository.java \
        src/main/java/com/nihongodev/platform/infrastructure/persistence/mapper/UserProgressPersistenceMapper.java \
        src/main/java/com/nihongodev/platform/infrastructure/persistence/mapper/ModuleProgressPersistenceMapper.java \
        src/main/java/com/nihongodev/platform/infrastructure/persistence/mapper/LearningActivityPersistenceMapper.java \
        src/main/java/com/nihongodev/platform/infrastructure/persistence/mapper/UserStatisticsPersistenceMapper.java \
        src/main/java/com/nihongodev/platform/infrastructure/persistence/adapter/ProgressRepositoryAdapter.java \
        src/main/java/com/nihongodev/platform/infrastructure/persistence/adapter/ModuleProgressRepositoryAdapter.java \
        src/main/java/com/nihongodev/platform/infrastructure/persistence/adapter/LearningActivityRepositoryAdapter.java \
        src/main/java/com/nihongodev/platform/infrastructure/persistence/adapter/StatisticsRepositoryAdapter.java \
        src/main/java/com/nihongodev/platform/infrastructure/persistence/adapter/AnalyticsQueryAdapter.java
git commit -m "feat(progress): add JPA repositories, mappers, and adapters for persistence layer"
```

---

## Task 13: Kafka Consumer and Scheduled Jobs

**Files:**
- Create: `src/main/java/com/nihongodev/platform/infrastructure/kafka/ProgressEventConsumer.java`
- Create: `src/main/java/com/nihongodev/platform/infrastructure/scheduling/StatisticsRecalculationJob.java`
- Create: `src/main/java/com/nihongodev/platform/infrastructure/scheduling/StreakCalculationJob.java`
- Create: `src/test/java/com/nihongodev/platform/infrastructure/kafka/ProgressEventConsumerTest.java`

- [ ] **Step 1: Create ProgressEventConsumer**

```java
package com.nihongodev.platform.infrastructure.kafka;

import com.nihongodev.platform.application.port.in.*;
import com.nihongodev.platform.domain.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProgressEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ProgressEventConsumer.class);

    private final UpdateProgressOnLessonCompletedPort lessonCompletedPort;
    private final UpdateProgressOnQuizCompletedPort quizCompletedPort;
    private final UpdateProgressOnInterviewCompletedPort interviewCompletedPort;
    private final UpdateProgressOnCorrectionCompletedPort correctionCompletedPort;

    public ProgressEventConsumer(UpdateProgressOnLessonCompletedPort lessonCompletedPort,
                                 UpdateProgressOnQuizCompletedPort quizCompletedPort,
                                 UpdateProgressOnInterviewCompletedPort interviewCompletedPort,
                                 UpdateProgressOnCorrectionCompletedPort correctionCompletedPort) {
        this.lessonCompletedPort = lessonCompletedPort;
        this.quizCompletedPort = quizCompletedPort;
        this.interviewCompletedPort = interviewCompletedPort;
        this.correctionCompletedPort = correctionCompletedPort;
    }

    @KafkaListener(topics = "lesson-events", groupId = "progress-consumer-group")
    public void handleLessonCompleted(LessonCompletedEvent event) {
        log.info("Received LessonCompletedEvent for user: {}", event.userId());
        try {
            lessonCompletedPort.execute(event);
        } catch (Exception e) {
            log.error("Error processing LessonCompletedEvent for user {}: {}", event.userId(), e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "quiz-events", groupId = "progress-consumer-group")
    public void handleQuizCompleted(QuizCompletedEvent event) {
        log.info("Received QuizCompletedEvent for user: {}", event.userId());
        try {
            quizCompletedPort.execute(event);
        } catch (Exception e) {
            log.error("Error processing QuizCompletedEvent for user {}: {}", event.userId(), e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "interview-events", groupId = "progress-consumer-group")
    public void handleInterviewCompleted(InterviewCompletedEvent event) {
        log.info("Received InterviewCompletedEvent for user: {}", event.userId());
        try {
            interviewCompletedPort.execute(event);
        } catch (Exception e) {
            log.error("Error processing InterviewCompletedEvent for user {}: {}", event.userId(), e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "correction-events", groupId = "progress-consumer-group")
    public void handleCorrectionCompleted(TextCorrectedEvent event) {
        log.info("Received TextCorrectedEvent for user: {}", event.userId());
        try {
            correctionCompletedPort.execute(event);
        } catch (Exception e) {
            log.error("Error processing TextCorrectedEvent for user {}: {}", event.userId(), e.getMessage(), e);
        }
    }
}
```

- [ ] **Step 2: Create StatisticsRecalculationJob**

```java
package com.nihongodev.platform.infrastructure.scheduling;

import com.nihongodev.platform.application.port.in.RecalculateStatisticsPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class StatisticsRecalculationJob {

    private static final Logger log = LoggerFactory.getLogger(StatisticsRecalculationJob.class);

    private final RecalculateStatisticsPort recalculateStatisticsPort;

    public StatisticsRecalculationJob(RecalculateStatisticsPort recalculateStatisticsPort) {
        this.recalculateStatisticsPort = recalculateStatisticsPort;
    }

    @Scheduled(fixedRate = 15, timeUnit = TimeUnit.MINUTES)
    public void recalculate() {
        log.info("Starting statistics recalculation job");
        try {
            recalculateStatisticsPort.recalculateAll();
            log.info("Statistics recalculation completed");
        } catch (Exception e) {
            log.error("Error during statistics recalculation: {}", e.getMessage(), e);
        }
    }
}
```

- [ ] **Step 3: Create StreakCalculationJob**

```java
package com.nihongodev.platform.infrastructure.scheduling;

import com.nihongodev.platform.infrastructure.persistence.entity.UserProgressEntity;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaUserProgressRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class StreakCalculationJob {

    private static final Logger log = LoggerFactory.getLogger(StreakCalculationJob.class);

    private final JpaUserProgressRepository progressRepository;

    public StreakCalculationJob(JpaUserProgressRepository progressRepository) {
        this.progressRepository = progressRepository;
    }

    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void resetBrokenStreaks() {
        log.info("Starting streak calculation job");
        LocalDate today = LocalDate.now();
        List<UserProgressEntity> allProgress = progressRepository.findAll();

        int resetCount = 0;
        for (UserProgressEntity progress : allProgress) {
            if (progress.getCurrentStreak() > 0 && progress.getLastActivityAt() != null) {
                LocalDate lastActive = progress.getLastActivityAt().toLocalDate();
                if (lastActive.isBefore(today.minusDays(1))) {
                    progress.setCurrentStreak(0);
                    progressRepository.save(progress);
                    resetCount++;
                }
            }
        }
        log.info("Streak calculation completed. Reset {} streaks", resetCount);
    }
}
```

- [ ] **Step 4: Write ProgressEventConsumer test**

```java
package com.nihongodev.platform.infrastructure.kafka;

import com.nihongodev.platform.application.port.in.*;
import com.nihongodev.platform.domain.event.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProgressEventConsumer")
class ProgressEventConsumerTest {

    @Mock private UpdateProgressOnLessonCompletedPort lessonPort;
    @Mock private UpdateProgressOnQuizCompletedPort quizPort;
    @Mock private UpdateProgressOnInterviewCompletedPort interviewPort;
    @Mock private UpdateProgressOnCorrectionCompletedPort correctionPort;

    private ProgressEventConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new ProgressEventConsumer(lessonPort, quizPort, interviewPort, correctionPort);
    }

    @Test
    @DisplayName("should delegate LessonCompletedEvent to port")
    void shouldDelegateLessonEvent() {
        LessonCompletedEvent event = new LessonCompletedEvent(
                UUID.randomUUID(), UUID.randomUUID(), "Lesson 1", "GRAMMAR", "N5", LocalDateTime.now());

        consumer.handleLessonCompleted(event);

        verify(lessonPort).execute(event);
    }

    @Test
    @DisplayName("should delegate QuizCompletedEvent to port")
    void shouldDelegateQuizEvent() {
        QuizCompletedEvent event = new QuizCompletedEvent(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "Quiz 1",
                85.0, true, 5, "CLASSIC", LocalDateTime.now());

        consumer.handleQuizCompleted(event);

        verify(quizPort).execute(event);
    }

    @Test
    @DisplayName("should delegate InterviewCompletedEvent to port")
    void shouldDelegateInterviewEvent() {
        InterviewCompletedEvent event = new InterviewCompletedEvent(
                UUID.randomUUID(), UUID.randomUUID(), "TECHNICAL", 75.0, 600, true);

        consumer.handleInterviewCompleted(event);

        verify(interviewPort).execute(event);
    }

    @Test
    @DisplayName("should delegate TextCorrectedEvent to port")
    void shouldDelegateCorrectionEvent() {
        TextCorrectedEvent event = new TextCorrectedEvent(
                UUID.randomUUID(), UUID.randomUUID(), "EMAIL", 80.0, 5, 2, LocalDateTime.now());

        consumer.handleCorrectionCompleted(event);

        verify(correctionPort).execute(event);
    }

    @Test
    @DisplayName("should handle exception gracefully without propagating")
    void shouldHandleExceptionGracefully() {
        LessonCompletedEvent event = new LessonCompletedEvent(
                UUID.randomUUID(), UUID.randomUUID(), "Lesson 1", "GRAMMAR", "N5", LocalDateTime.now());

        doThrow(new RuntimeException("DB error")).when(lessonPort).execute(event);

        consumer.handleLessonCompleted(event);

        verify(lessonPort).execute(event);
    }
}
```

- [ ] **Step 5: Run consumer tests**

Run: `mvn test -pl . -Dtest=ProgressEventConsumerTest -f pom.xml`
Expected: ALL PASS

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/nihongodev/platform/infrastructure/kafka/ProgressEventConsumer.java \
        src/main/java/com/nihongodev/platform/infrastructure/scheduling/StatisticsRecalculationJob.java \
        src/main/java/com/nihongodev/platform/infrastructure/scheduling/StreakCalculationJob.java \
        src/test/java/com/nihongodev/platform/infrastructure/kafka/ProgressEventConsumerTest.java
git commit -m "feat(progress): add Kafka consumer and scheduled jobs for progress tracking"
```

---

## Task 14: REST Controllers

**Files:**
- Create: `src/main/java/com/nihongodev/platform/infrastructure/web/controller/ProgressController.java`
- Create: `src/main/java/com/nihongodev/platform/infrastructure/web/controller/AnalyticsController.java`

- [ ] **Step 1: Create ProgressController**

```java
package com.nihongodev.platform.infrastructure.web.controller;

import com.nihongodev.platform.application.dto.*;
import com.nihongodev.platform.application.port.in.*;
import com.nihongodev.platform.domain.model.ModuleType;
import com.nihongodev.platform.infrastructure.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/progress")
@Tag(name = "Progress", description = "User progress tracking and statistics")
public class ProgressController {

    private final GetUserProgressPort getUserProgressPort;
    private final GetModuleProgressPort getModuleProgressPort;
    private final GetUserActivityHistoryPort getActivityHistoryPort;
    private final GetUserStatisticsPort getUserStatisticsPort;

    public ProgressController(GetUserProgressPort getUserProgressPort,
                              GetModuleProgressPort getModuleProgressPort,
                              GetUserActivityHistoryPort getActivityHistoryPort,
                              GetUserStatisticsPort getUserStatisticsPort) {
        this.getUserProgressPort = getUserProgressPort;
        this.getModuleProgressPort = getModuleProgressPort;
        this.getActivityHistoryPort = getActivityHistoryPort;
        this.getUserStatisticsPort = getUserStatisticsPort;
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user's global progress")
    public ResponseEntity<UserProgressDto> getMyProgress(@AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(getUserProgressPort.execute(user.id()));
    }

    @GetMapping("/me/modules")
    @Operation(summary = "Get progress for all modules")
    public ResponseEntity<List<ModuleProgressDto>> getMyModules(@AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(getModuleProgressPort.getAll(user.id()));
    }

    @GetMapping("/me/modules/{type}")
    @Operation(summary = "Get progress for a specific module")
    public ResponseEntity<ModuleProgressDto> getMyModuleByType(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable String type) {
        ModuleType moduleType = ModuleType.valueOf(type.toUpperCase());
        return ResponseEntity.ok(getModuleProgressPort.getByType(user.id(), moduleType));
    }

    @GetMapping("/me/activities")
    @Operation(summary = "Get learning activity history (paginated)")
    public ResponseEntity<Page<LearningActivityDto>> getMyActivities(
            @AuthenticationPrincipal AuthenticatedUser user,
            Pageable pageable) {
        return ResponseEntity.ok(getActivityHistoryPort.execute(user.id(), pageable));
    }

    @GetMapping("/me/statistics")
    @Operation(summary = "Get calculated statistics, trends, and recommendations")
    public ResponseEntity<UserStatisticsDto> getMyStatistics(@AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(getUserStatisticsPort.execute(user.id()));
    }

    @GetMapping("/me/weak-areas")
    @Operation(summary = "Get identified weak areas")
    public ResponseEntity<List<WeakAreaDto>> getMyWeakAreas(@AuthenticationPrincipal AuthenticatedUser user) {
        UserStatisticsDto stats = getUserStatisticsPort.execute(user.id());
        return ResponseEntity.ok(stats.weakAreas());
    }

    @GetMapping("/me/recommendations")
    @Operation(summary = "Get personalized learning recommendations")
    public ResponseEntity<List<RecommendationDto>> getMyRecommendations(@AuthenticationPrincipal AuthenticatedUser user) {
        UserStatisticsDto stats = getUserStatisticsPort.execute(user.id());
        return ResponseEntity.ok(stats.recommendations());
    }
}
```

- [ ] **Step 2: Create AnalyticsController**

```java
package com.nihongodev.platform.infrastructure.web.controller;

import com.nihongodev.platform.application.dto.PlatformAnalyticsDto;
import com.nihongodev.platform.application.dto.TopUserDto;
import com.nihongodev.platform.application.port.in.GetPlatformAnalyticsPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/analytics")
@Tag(name = "Analytics", description = "Platform analytics for administrators")
public class AnalyticsController {

    private final GetPlatformAnalyticsPort getPlatformAnalyticsPort;

    public AnalyticsController(GetPlatformAnalyticsPort getPlatformAnalyticsPort) {
        this.getPlatformAnalyticsPort = getPlatformAnalyticsPort;
    }

    @GetMapping("/overview")
    @Operation(summary = "Get platform overview analytics")
    public ResponseEntity<PlatformAnalyticsDto> getOverview() {
        return ResponseEntity.ok(getPlatformAnalyticsPort.getOverview());
    }

    @GetMapping("/top-users")
    @Operation(summary = "Get top users by XP")
    public ResponseEntity<List<TopUserDto>> getTopUsers(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(getPlatformAnalyticsPort.getTopUsers(limit));
    }
}
```

- [ ] **Step 3: Enable scheduling in application config**

Add `@EnableScheduling` annotation to the main application class or create a config class:

```java
// If not already present, add to an existing config or NihongoDevApplication.java:
// @EnableScheduling
```

- [ ] **Step 4: Add Kafka consumer deserializer config to application.yml**

Add to `spring.kafka.consumer` section in `application.yml`:

```yaml
spring:
  kafka:
    consumer:
      properties:
        spring.json.trusted.packages: com.nihongodev.platform.domain.event
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
```

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/nihongodev/platform/infrastructure/web/controller/ProgressController.java \
        src/main/java/com/nihongodev/platform/infrastructure/web/controller/AnalyticsController.java
git commit -m "feat(progress): add REST controllers for Progress and Analytics endpoints"
```

---

## Task 15: Enable Scheduling and Kafka Consumer Config

**Files:**
- Modify: `src/main/java/com/nihongodev/platform/NihongoDevApplication.java`
- Modify: `src/main/resources/application.yml`
- Modify: `src/main/java/com/nihongodev/platform/infrastructure/kafka/KafkaEventPublisherAdapter.java`

- [ ] **Step 1: Add @EnableScheduling to main application**

Add `@EnableScheduling` import and annotation to `NihongoDevApplication.java`.

- [ ] **Step 2: Add Kafka consumer config to application.yml**

Add `value-deserializer` and `trusted.packages` under `spring.kafka.consumer`:

```yaml
spring:
  kafka:
    consumer:
      group-id: nihongodev-group
      auto-offset-reset: earliest
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: com.nihongodev.platform.domain.event
```

- [ ] **Step 3: Add correction-events to KafkaEventPublisherAdapter topic registry**

Add `registry.put("correction-events", props.getCorrectionEvents().getName());` to `buildTopicRegistry` method. Also add getter/setter for `correctionEvents` to `KafkaTopicsProperties`.

- [ ] **Step 4: Add correctionEvents to KafkaTopicsProperties**

```java
private TopicDef correctionEvents = new TopicDef();

public TopicDef getCorrectionEvents() { return correctionEvents; }
public void setCorrectionEvents(TopicDef correctionEvents) { this.correctionEvents = correctionEvents; }
```

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/nihongodev/platform/NihongoDevApplication.java \
        src/main/resources/application.yml \
        src/main/java/com/nihongodev/platform/infrastructure/kafka/KafkaEventPublisherAdapter.java \
        src/main/java/com/nihongodev/platform/infrastructure/config/KafkaTopicsProperties.java
git commit -m "feat(progress): enable scheduling and configure Kafka consumer deserialization"
```

---

## Task 16: Run Full Test Suite and Final Verification

- [ ] **Step 1: Run all project tests**

Run: `mvn test -f pom.xml`
Expected: ALL PASS (existing 172 + new ~30 tests)

- [ ] **Step 2: Verify compilation**

Run: `mvn compile -f pom.xml`
Expected: BUILD SUCCESS

- [ ] **Step 3: Final commit if any fixes needed**

```bash
git add -A
git commit -m "fix(progress): address compilation and test issues"
```

---

## Task 17: Update TODO.md

**Files:**
- Modify: `TODO.md`

- [ ] **Step 1: Update TODO.md to mark BLOC 8 as complete**

Mark BLOC 8 as completed with details about what was implemented and the improvements made.

- [ ] **Step 2: Commit and push**

```bash
git add TODO.md
git commit -m "docs: update TODO.md — mark BLOC 8 Progress & Analytics as complete"
git push origin main
```
