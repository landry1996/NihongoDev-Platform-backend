# BLOC 12 — Code in Japanese (Innovation)

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a unique module that trains developers to participate in Japanese software engineering workflows — writing code review comments, pull request descriptions, commit messages, and technical discussions entirely in Japanese. Unlike generic language apps, this module targets the precise intersection of IT terminology and business Japanese used daily in Japanese dev teams.

**Architecture:** Hexagonal (ports/adapters) with Strategy Pattern for exercise evaluators, Template Method for commit message validation, and Chain of Responsibility for technical Japanese analysis. Domain events feed into the progress system (BLOC 8). Scoring is multi-dimensional: technical accuracy, Japanese correctness, professional tone, and team communication.

**Tech Stack:** Java 21, Spring Boot 3.3.5, PostgreSQL (JSONB), Kafka, Flyway V10

---

## Innovation: Pourquoi ce module est unique

Aucune app existante n'enseigne le **japonais technique en contexte dev** :

- Ecrire un commentaire de code review en japonais professionnel (指摘を書く)
- Rediger une PR description avec la structure japonaise (背景・変更内容・影響範囲)
- Formuler un commit message suivant les conventions japonaises (プレフィックス + 体言止め)
- Donner du feedback technique sans blesser (やんわり指摘する)
- Demander des changements poliment en review (修正のお願い)
- Ecrire un bug report en japonais structure (再現手順・期待結果・実際の結果)

Ce module transforme ces situations en **exercices interactifs scores** avec feedback actionnable et detection d'erreurs courantes des non-natifs.

---

## 1. Concepts Cles du Domaine

### 1.1 Types d'Exercices

| Type | Description | Exemple |
|------|-------------|---------|
| `CODE_REVIEW` | Ecrire un commentaire de review sur un diff | "Cette boucle a une complexite O(n^2), une HashMap serait plus efficace" → en japonais pro |
| `PR_WRITING` | Rediger une description de PR complete | Background + Changes + Impact + Test plan en japonais |
| `COMMIT_MESSAGE` | Ecrire un commit message en japonais | Prefix + 体言止め (noun-ending style) + scope |
| `BUG_REPORT` | Rediger un rapport de bug structure | 再現手順 + 期待結果 + 実際の結果 |
| `TECH_DISCUSSION` | Participer a une discussion technique (Slack/standup) | Proposer une solution, poser une question technique |

### 1.2 Dimensions Scorees

| Dimension | Description | Poids |
|-----------|-------------|-------|
| Technical Accuracy (技術的正確さ) | Le contenu technique est-il correct et precis ? | 30% |
| Japanese Quality (日本語品質) | Grammaire, vocabulaire, naturalness | 25% |
| Professional Tone (ビジネストーン) | Niveau de politesse adapte au contexte | 20% |
| Structure (構成) | Respect de la structure attendue (template) | 15% |
| Team Communication (チーム力) | Clarte, empathie, actionabilite du feedback | 10% |

### 1.3 Niveaux de Review

| Level | Japonais | Description | Exemple |
|-------|----------|-------------|---------|
| SUGGESTION | 提案 | Suggestion douce, non-bloquante | 「〜するとより良くなるかもしれません」 |
| REQUEST | 修正依頼 | Demande de modification | 「〜の修正をお願いできますか」 |
| MUST_FIX | 必須修正 | Bloquant, doit etre corrige | 「こちらは修正が必要です。理由は〜」 |
| QUESTION | 質問 | Demande de clarification | 「こちらの意図を教えていただけますか」 |
| PRAISE | 称賛 | Compliment technique | 「このアプローチは素晴らしいですね」 |

### 1.4 Conventions de Commit Messages Japonais

| Prefix | Japonais | Usage |
|--------|----------|-------|
| feat | 機能追加 | Nouvelle fonctionnalite |
| fix | バグ修正 | Correction de bug |
| refactor | リファクタリング | Restructuration sans changement fonctionnel |
| docs | ドキュメント修正 | Documentation |
| test | テスト追加 | Ajout/modification de tests |
| perf | パフォーマンス改善 | Amelioration de performance |
| chore | 雑務 | Maintenance, dependencies |

**Regle 体言止め (taigen-dome):** Les commit messages japonais se terminent typiquement par un nom, pas un verbe conjugue.
- Correct: 「ユーザー認証機能の追加」 (ajout de la fonction d'authentification)
- Incorrect: 「ユーザー認証機能を追加しました」 (j'ai ajoute...)

---

## 2. Domain Model

### 2.1 Enumerations

```java
public enum ExerciseType {
    CODE_REVIEW,       // コードレビューコメント
    PR_WRITING,        // PR説明文の作成
    COMMIT_MESSAGE,    // コミットメッセージの作成
    BUG_REPORT,        // バグレポート作成
    TECH_DISCUSSION    // 技術的議論
}

public enum ReviewLevel {
    SUGGESTION,        // 提案 — Soft suggestion
    REQUEST,           // 修正依頼 — Change request
    MUST_FIX,          // 必須修正 — Blocking issue
    QUESTION,          // 質問 — Question/clarification
    PRAISE             // 称賛 — Positive feedback
}

public enum CommitPrefix {
    FEAT("feat", "機能追加"),
    FIX("fix", "バグ修正"),
    REFACTOR("refactor", "リファクタリング"),
    DOCS("docs", "ドキュメント修正"),
    TEST("test", "テスト追加"),
    PERF("perf", "パフォーマンス改善"),
    CHORE("chore", "雑務"),
    STYLE("style", "スタイル修正"),
    CI("ci", "CI/CD修正");

    private final String english;
    private final String japanese;

    CommitPrefix(String english, String japanese) {
        this.english = english;
        this.japanese = japanese;
    }

    public String getEnglish() { return english; }
    public String getJapanese() { return japanese; }
}

public enum PRSection {
    BACKGROUND,        // 背景 — Why this PR
    CHANGES,           // 変更内容 — What changed
    IMPACT,            // 影響範囲 — Affected areas
    TEST_PLAN,         // テスト計画 — How tested
    NOTES,             // 備考 — Additional notes
    SCREENSHOTS        // スクリーンショット — Visual evidence
}

public enum CodeContext {
    BACKEND_JAVA,      // Javaバックエンド
    FRONTEND_REACT,    // Reactフロントエンド
    DATABASE,          // データベース
    INFRASTRUCTURE,    // インフラ/DevOps
    API_DESIGN,        // API設計
    TESTING,           // テスト
    SECURITY,          // セキュリティ
    PERFORMANCE        // パフォーマンス
}
```

### 2.2 CodeReviewExercise (Aggregate)

```java
public class CodeReviewExercise {
    private UUID id;
    private String title;                        // "Review: unnecessary null check"
    private String titleJp;                      // "レビュー：不要なnullチェック"
    private ExerciseType type;
    private CodeContext codeContext;
    private JapaneseLevel difficulty;
    private String codeSnippet;                  // Le code a reviewer (diff format ou snippet)
    private String codeLanguage;                 // "java", "typescript", "sql", etc.
    private String scenario;                     // Description de la situation (FR)
    private String scenarioJp;                   // Description en japonais
    private ReviewLevel expectedReviewLevel;     // Pour CODE_REVIEW: quel niveau de feedback
    private List<String> technicalIssues;        // Les problemes techniques a identifier
    private String modelAnswer;                  // Reponse modele en japonais
    private String modelAnswerExplanation;       // Pourquoi c'est la bonne formulation
    private List<String> keyPhrases;             // Expressions japonaises attendues
    private List<String> avoidPhrases;           // Expressions a eviter
    private List<String> technicalTermsJp;       // Termes techniques en japonais a utiliser
    private PRTemplate prTemplate;               // Pour PR_WRITING: structure attendue
    private CommitMessageRule commitRule;         // Pour COMMIT_MESSAGE: regles de validation
    private String culturalNote;                 // Note pedagogique
    private int xpReward;
    private boolean published;
    private LocalDateTime createdAt;
}
```

### 2.3 PRTemplate (Value Object)

```java
public record PRTemplate(
    List<PRSection> requiredSections,        // Sections obligatoires
    Map<PRSection, String> sectionHints,     // Indications par section
    Map<PRSection, List<String>> sectionKeyPhrases,  // Phrases cles par section
    int minSections,                         // Nombre minimum de sections
    boolean requiresJapaneseOnly             // Pas de mix anglais/japonais
) {}
```

### 2.4 CommitMessageRule (Value Object)

```java
public record CommitMessageRule(
    CommitPrefix expectedPrefix,             // Le prefix attendu
    boolean requireTaigenDome,               // Doit finir par un nom (体言止め)
    int maxLength,                           // Longueur max
    boolean requireScope,                    // ex: feat(auth): ...
    String expectedScope,                    // Le scope attendu
    List<String> forbiddenPatterns,          // Patterns interdits (ex: "しました", "します")
    List<String> goodExamples,              // Exemples valides
    List<String> badExamples                // Exemples invalides
) {}
```

### 2.5 CodeExerciseScore (Value Object)

```java
public record CodeExerciseScore(
    int technicalAccuracyScore,              // 0-100 : precision technique
    int japaneseQualityScore,                // 0-100 : qualite du japonais
    int professionalToneScore,               // 0-100 : ton professionnel
    int structureScore,                      // 0-100 : structure/template
    int teamCommunicationScore,              // 0-100 : clarte, empathie
    int overallScore                         // Weighted average
) {
    public static CodeExerciseScore calculate(int technical, int japanese, int professional, int structure, int teamComm) {
        int overall = (int) (technical * 0.30 + japanese * 0.25 + professional * 0.20 + structure * 0.15 + teamComm * 0.10);
        return new CodeExerciseScore(technical, japanese, professional, structure, teamComm, overall);
    }
}
```

### 2.6 CodeExerciseAttempt (Entity)

```java
public class CodeExerciseAttempt {
    private UUID id;
    private UUID userId;
    private UUID exerciseId;
    private ExerciseType exerciseType;
    private String userResponse;                 // Reponse complete de l'utilisateur
    private CodeExerciseScore score;
    private List<TechnicalJapaneseViolation> violations;
    private CommitMessageAnalysis commitAnalysis; // Pour COMMIT_MESSAGE uniquement
    private String feedback;                     // Feedback genere
    private int timeSpentSeconds;
    private LocalDateTime attemptedAt;
}
```

### 2.7 TechnicalJapaneseViolation (Value Object)

```java
public record TechnicalJapaneseViolation(
    String originalText,            // Le texte problematique
    String suggestion,              // La correction suggeree
    ViolationType violationType,    // Type de violation
    String rule,                    // Regle violee
    Severity severity               // MINOR, MODERATE, CRITICAL
) {}

public enum ViolationType {
    WRONG_TECHNICAL_TERM,           // Mauvais terme technique japonais
    CASUAL_IN_REVIEW,              // Langage trop familier dans un review
    VERB_ENDING_IN_COMMIT,         // Verbe conjugue dans un commit (pas 体言止め)
    MISSING_CUSHION_WORD,          // Manque de mots amortisseurs
    TOO_DIRECT,                    // Trop direct pour un contexte japonais
    MIXED_LANGUAGE,                // Mix anglais/japonais non-idiomatique
    WRONG_STRUCTURE,               // Structure incorrecte (PR, bug report)
    KATAKANA_MISUSE                // Mauvais usage du katakana technique
}
```

### 2.8 CommitMessageAnalysis (Value Object)

```java
public record CommitMessageAnalysis(
    boolean hasValidPrefix,          // Prefix reconnu
    CommitPrefix detectedPrefix,     // Prefix detecte
    boolean isTaigenDome,            // Termine par un nom
    boolean hasScope,                // Contient un scope
    String detectedScope,            // Scope detecte
    int length,                      // Longueur du message
    boolean isWithinMaxLength,       // Sous la limite
    List<String> detectedVerbEndings, // Fins verbales detectees (erreurs)
    int commitScore                  // Score du commit (0-100)
) {}
```

### 2.9 CodeJapaneseProgress (Entity)

```java
public class CodeJapaneseProgress {
    private UUID id;
    private UUID userId;
    private ExerciseType exerciseType;
    private int exercisesCompleted;
    private int totalScore;
    private int averageScore;
    private int bestScore;
    private int currentStreak;
    private Map<ViolationType, Integer> recurringViolations;  // Erreurs recurrentes
    private LocalDateTime lastActivityAt;
}
```

### 2.10 Domain Events

```java
public record CodeExerciseCompletedEvent(
    UUID eventId,
    String eventType,
    UUID userId,
    UUID exerciseId,
    ExerciseType exerciseType,
    int overallScore,
    LocalDateTime occurredAt
) implements DomainEvent {
    public static CodeExerciseCompletedEvent create(UUID userId, UUID exerciseId, ExerciseType type, int score) {
        return new CodeExerciseCompletedEvent(
            UUID.randomUUID(), "CODE_EXERCISE_COMPLETED", userId, exerciseId, type, score, LocalDateTime.now()
        );
    }
}

public record CodeMilestoneReachedEvent(
    UUID eventId,
    String eventType,
    UUID userId,
    ExerciseType exerciseType,
    int exercisesCompleted,
    int averageScore,
    LocalDateTime occurredAt
) implements DomainEvent {
    public static CodeMilestoneReachedEvent create(UUID userId, ExerciseType type, int completed, int avgScore) {
        return new CodeMilestoneReachedEvent(
            UUID.randomUUID(), "CODE_MILESTONE_REACHED", userId, type, completed, avgScore, LocalDateTime.now()
        );
    }
}
```

---

## 3. Commit Message Validator — Moteur d'Analyse

### 3.1 Architecture (Chain of Responsibility)

```java
public interface CommitAnalysisStep {
    List<TechnicalJapaneseViolation> analyze(String commitMessage, CommitMessageRule rule);
}
```

**Steps implementes :**

| Step | Responsabilite |
|------|---------------|
| `PrefixStep` | Detecte et valide le prefix (feat/fix/refactor...) en japonais |
| `ScopeStep` | Verifie la presence et correction du scope |
| `TaigenDomeStep` | Verifie que le message se termine par un nom (体言止め), pas un verbe |
| `LengthStep` | Verifie la longueur maximale |
| `VerbEndingDetectionStep` | Detecte les fins verbales interdites (しました, します, した) |
| `TechnicalTermStep` | Verifie l'usage correct des termes techniques en japonais |

### 3.2 CommitMessageValidator (Orchestrateur)

```java
@Component
public class CommitMessageValidator {
    private final List<CommitAnalysisStep> steps;

    public CommitMessageAnalysis validate(String commitMessage, CommitMessageRule rule) {
        List<TechnicalJapaneseViolation> violations = steps.stream()
            .flatMap(step -> step.analyze(commitMessage, rule).stream())
            .toList();

        boolean hasValidPrefix = detectPrefix(commitMessage, rule) != null;
        CommitPrefix detected = detectPrefix(commitMessage, rule);
        boolean isTaigenDome = checkTaigenDome(commitMessage);
        boolean hasScope = checkScope(commitMessage);
        int length = commitMessage.length();

        int score = calculateCommitScore(violations, hasValidPrefix, isTaigenDome, hasScope, length, rule);

        return new CommitMessageAnalysis(
            hasValidPrefix, detected, isTaigenDome, hasScope,
            extractScope(commitMessage), length, length <= rule.maxLength(),
            extractVerbEndings(commitMessage), score
        );
    }
}
```

### 3.3 Patterns de Detection

```java
// TaigenDomeStep — Noun endings (correct)
List<String> NOUN_ENDINGS = List.of(
    "追加", "修正", "削除", "変更", "改善", "対応", "実装", "導入",
    "更新", "整理", "統合", "分離", "移行", "設定", "構築", "最適化"
);

// VerbEndingDetectionStep — Verb endings (incorrect in commits)
List<String> FORBIDDEN_VERB_ENDINGS = List.of(
    "しました", "します", "した", "する",
    "ました", "ます", "できた", "なった",
    "変えた", "追加した", "修正した", "削除した"
);

// TechnicalTermStep — IT terms in Japanese
Map<String, String> TECH_TERMS_JP = Map.ofEntries(
    Map.entry("authentication", "認証"),
    Map.entry("authorization", "認可"),
    Map.entry("database", "データベース"),
    Map.entry("endpoint", "エンドポイント"),
    Map.entry("refactoring", "リファクタリング"),
    Map.entry("deployment", "デプロイ"),
    Map.entry("performance", "パフォーマンス"),
    Map.entry("dependency", "依存関係"),
    Map.entry("middleware", "ミドルウェア"),
    Map.entry("concurrency", "並行処理"),
    Map.entry("cache", "キャッシュ"),
    Map.entry("migration", "マイグレーション"),
    Map.entry("rollback", "ロールバック"),
    Map.entry("null check", "nullチェック"),
    Map.entry("exception", "例外"),
    Map.entry("unit test", "ユニットテスト"),
    Map.entry("integration test", "結合テスト"),
    Map.entry("code review", "コードレビュー"),
    Map.entry("pull request", "プルリクエスト"),
    Map.entry("bug", "バグ"),
    Map.entry("hotfix", "ホットフィックス")
);
```

---

## 4. Exercise Evaluators — Scoring Engine

### 4.1 Strategy Pattern

```java
public interface CodeExerciseEvaluator {
    CodeExerciseEvaluationResult evaluate(CodeReviewExercise exercise, String userResponse);
}

public record CodeExerciseEvaluationResult(
    CodeExerciseScore score,
    List<TechnicalJapaneseViolation> violations,
    CommitMessageAnalysis commitAnalysis,    // null except for COMMIT_MESSAGE
    String feedback
) {}
```

**Implementations :**

| Evaluator | Type | Logique |
|-----------|------|---------|
| `CodeReviewEvaluator` | CODE_REVIEW | Tone analysis + technical term detection + review level matching |
| `PRWritingEvaluator` | PR_WRITING | Section completeness + structure + key phrases per section |
| `CommitMessageEvaluator` | COMMIT_MESSAGE | CommitMessageValidator + score calculation |
| `BugReportEvaluator` | BUG_REPORT | Structure (steps/expected/actual) + clarity + terminology |
| `TechDiscussionEvaluator` | TECH_DISCUSSION | Indirectness + technical accuracy + proposals |

### 4.2 CodeReviewEvaluator — Detail

```java
@Component
public class CodeReviewEvaluator implements CodeExerciseEvaluator {
    private final TechnicalTermDetector termDetector;
    private final ReviewToneAnalyzer toneAnalyzer;
    private final CushionWordDetector cushionDetector;

    @Override
    public CodeExerciseEvaluationResult evaluate(CodeReviewExercise exercise, String userResponse) {
        // 1. Technical accuracy: are the correct issues identified?
        int technical = evaluateTechnicalContent(exercise, userResponse);

        // 2. Japanese quality: grammar, vocabulary, naturalness
        int japanese = evaluateJapaneseQuality(exercise, userResponse);

        // 3. Professional tone: appropriate review level, cushion words
        int professional = toneAnalyzer.evaluate(userResponse, exercise.getExpectedReviewLevel());

        // 4. Structure: proper review comment structure
        int structure = evaluateStructure(exercise, userResponse);

        // 5. Team communication: actionable, empathetic, clear
        int teamComm = evaluateTeamCommunication(userResponse);

        CodeExerciseScore score = CodeExerciseScore.calculate(technical, japanese, professional, structure, teamComm);
        List<TechnicalJapaneseViolation> violations = collectViolations(exercise, userResponse);
        String feedback = generateFeedback(score, violations, exercise);

        return new CodeExerciseEvaluationResult(score, violations, null, feedback);
    }
}
```

### 4.3 ReviewToneAnalyzer

Mesure si le ton du commentaire de review est adapte au contexte :

| Pattern | Score impact | Exemple |
|---------|-------------|---------|
| Cushion words avant suggestion | +15 | 「恐縮ですが」「もし可能であれば」 |
| Question form for requests | +10 | 「〜していただけますか」「〜はいかがでしょうか」 |
| Reason before request | +10 | 「〜のため、〜をお願いします」 |
| Praise before criticism | +10 | 「ここは素晴らしいですが、〜」 |
| Bare imperative | -20 | 「直せ」「変えろ」 |
| Too casual | -15 | 「これダメだよ」「バグってる」 |
| No action item | -10 | Pointing out issue without suggestion |

### 4.4 TechnicalTermDetector

```java
@Component
public class TechnicalTermDetector {
    // Detects correct usage of IT terms in Japanese context
    // Validates katakana transliterations
    // Checks for common non-native mistakes:
    //   - "オセンティケーション" (wrong) → "認証" (correct)
    //   - "データーベース" (wrong) → "データベース" (correct)
    //   - "ファンクション" (less common) → "関数" (preferred in code context)

    public TechnicalTermAnalysis analyze(String text, CodeContext context) { ... }
}
```

### 4.5 CushionWordDetector

```java
@Component
public class CushionWordDetector {
    // Review-specific cushion words (クッション言葉)
    List<String> REVIEW_CUSHION_WORDS = List.of(
        "恐縮ですが",
        "お手数ですが",
        "もし可能であれば",
        "確認なのですが",
        "念のため",
        "細かい点ですが",
        "個人的な意見ですが",
        "ご検討いただけると"
    );

    public int countCushionWords(String text) { ... }
    public int scoreCushionUsage(String text, ReviewLevel expectedLevel) { ... }
}
```

---

## 5. Application Layer — Use Cases

| Use Case | Responsabilite |
|----------|---------------|
| `GetExerciseCatalogUseCase` | Catalogue des exercices (type, difficulty, context) |
| `StartExerciseUseCase` | Charger un exercice par ID ou par filtres |
| `SubmitExerciseResponseUseCase` | Evaluer la reponse, calculer le score, enregistrer |
| `ValidateCommitMessageUseCase` | Validation standalone d'un commit message |
| `GetExerciseHistoryUseCase` | Historique des tentatives par user |
| `GetCodeJapaneseProgressUseCase` | Progression par type d'exercice |
| `GetViolationReportUseCase` | Rapport des erreurs recurrentes |
| `CreateExerciseUseCase` | CRUD exercice (ADMIN/TEACHER) |

---

## 6. Infrastructure — Endpoints REST

### 6.1 CodeJapaneseController

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/code-japanese/exercises` | Catalogue filtre (type, difficulty, context) | Authenticated |
| GET | `/api/code-japanese/exercises/{id}` | Detail d'un exercice | Authenticated |
| POST | `/api/code-japanese/exercises/{id}/attempt` | Soumettre une reponse | Authenticated |
| POST | `/api/code-japanese/commit/validate` | Valider un commit message (standalone) | Authenticated |
| GET | `/api/code-japanese/history` | Historique des tentatives | Authenticated |
| GET | `/api/code-japanese/progress` | Progression par type d'exercice | Authenticated |
| GET | `/api/code-japanese/progress/violations` | Rapport des erreurs recurrentes | Authenticated |
| POST | `/api/code-japanese/exercises` | Creer un exercice (ADMIN/TEACHER) | ADMIN, TEACHER |
| PUT | `/api/code-japanese/exercises/{id}` | Modifier un exercice | ADMIN, TEACHER |

### 6.2 Request DTOs

```java
public record SubmitCodeExerciseRequest(
    @NotBlank @Size(max = 5000) String response,
    int timeSpentSeconds
) {}

public record ValidateCommitMessageRequest(
    @NotBlank @Size(max = 200) String commitMessage,
    @NotNull CommitPrefix expectedPrefix,
    boolean requireTaigenDome,
    boolean requireScope,
    String expectedScope
) {}

public record CreateCodeExerciseRequest(
    @NotBlank @Size(max = 200) String title,
    @Size(max = 200) String titleJp,
    @NotNull ExerciseType type,
    @NotNull CodeContext codeContext,
    @NotNull JapaneseLevel difficulty,
    @Size(max = 5000) String codeSnippet,
    @Size(max = 30) String codeLanguage,
    @NotBlank @Size(max = 2000) String scenario,
    @Size(max = 2000) String scenarioJp,
    ReviewLevel expectedReviewLevel,
    List<String> technicalIssues,
    @Size(max = 2000) String modelAnswer,
    @Size(max = 2000) String modelAnswerExplanation,
    List<String> keyPhrases,
    List<String> avoidPhrases,
    List<String> technicalTermsJp,
    PRTemplateData prTemplate,
    CommitMessageRuleData commitRule,
    @Size(max = 1000) String culturalNote,
    @Min(10) @Max(500) int xpReward
) {}
```

---

## 7. Database — Migration V10

```sql
-- V10__code_in_japanese.sql

CREATE TABLE code_review_exercises (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title                   VARCHAR(200) NOT NULL,
    title_jp                VARCHAR(200),
    exercise_type           VARCHAR(30) NOT NULL,
    code_context            VARCHAR(30) NOT NULL,
    difficulty              VARCHAR(20) NOT NULL,
    code_snippet            TEXT,
    code_language           VARCHAR(30),
    scenario                TEXT NOT NULL,
    scenario_jp             TEXT,
    expected_review_level   VARCHAR(20),
    technical_issues        JSONB,
    model_answer            TEXT,
    model_answer_explanation TEXT,
    key_phrases             JSONB,
    avoid_phrases           JSONB,
    technical_terms_jp      JSONB,
    pr_template             JSONB,
    commit_rule             JSONB,
    cultural_note           TEXT,
    xp_reward               INTEGER NOT NULL DEFAULT 50,
    published               BOOLEAN NOT NULL DEFAULT false,
    created_at              TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE code_exercise_attempts (
    id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id                     UUID NOT NULL REFERENCES users(id),
    exercise_id                 UUID NOT NULL REFERENCES code_review_exercises(id),
    exercise_type               VARCHAR(30) NOT NULL,
    user_response               TEXT NOT NULL,
    technical_accuracy_score    INTEGER NOT NULL,
    japanese_quality_score      INTEGER NOT NULL,
    professional_tone_score     INTEGER NOT NULL,
    structure_score             INTEGER NOT NULL,
    team_communication_score    INTEGER NOT NULL,
    overall_score               INTEGER NOT NULL,
    violations                  JSONB,
    commit_analysis             JSONB,
    feedback                    TEXT,
    time_spent_seconds          INTEGER NOT NULL DEFAULT 0,
    attempted_at                TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE code_japanese_progress (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id                 UUID NOT NULL REFERENCES users(id),
    exercise_type           VARCHAR(30) NOT NULL,
    exercises_completed     INTEGER NOT NULL DEFAULT 0,
    total_score             INTEGER NOT NULL DEFAULT 0,
    average_score           INTEGER NOT NULL DEFAULT 0,
    best_score              INTEGER NOT NULL DEFAULT 0,
    current_streak          INTEGER NOT NULL DEFAULT 0,
    recurring_violations    JSONB,
    last_activity_at        TIMESTAMP,
    UNIQUE(user_id, exercise_type)
);

CREATE INDEX idx_code_exercises_type ON code_review_exercises(exercise_type, difficulty);
CREATE INDEX idx_code_exercises_context ON code_review_exercises(code_context, published);
CREATE INDEX idx_code_attempts_user ON code_exercise_attempts(user_id);
CREATE INDEX idx_code_attempts_exercise ON code_exercise_attempts(exercise_id);
CREATE INDEX idx_code_progress_user ON code_japanese_progress(user_id);

-- Seed: 15 exercises covering the most common developer workflows in Japanese
INSERT INTO code_review_exercises (title, title_jp, exercise_type, code_context, difficulty, code_snippet, code_language, scenario, scenario_jp, expected_review_level, technical_issues, model_answer, model_answer_explanation, key_phrases, avoid_phrases, technical_terms_jp, pr_template, commit_rule, cultural_note, xp_reward, published)
VALUES
-- Exercise 1: Code Review — Unnecessary null check
('Review: Redundant null check after Optional', 'レビュー：Optional後の不要なnullチェック',
 'CODE_REVIEW', 'BACKEND_JAVA', 'N3',
 'Optional<User> userOpt = userRepository.findById(id);\nif (userOpt.isPresent()) {\n    User user = userOpt.get();\n    if (user != null) {\n        return user.getName();\n    }\n}\nreturn "unknown";',
 'java',
 'You are reviewing a junior developer''s PR. The code has a redundant null check after Optional.get(). Write a constructive review comment in Japanese.',
 'ジュニア開発者のPRをレビューしています。Optional.get()の後に冗長なnullチェックがあります。建設的なレビューコメントを日本語で書いてください。',
 'SUGGESTION', '["redundant null check", "Optional misuse", "can simplify with orElse"]',
 '細かい点ですが、Optional.get()の戻り値はnullにならないため、内側のnullチェックは不要かと思います。orElse("unknown")を使うとよりシンプルに書けるかもしれません。ご検討いただけると幸いです。',
 'Starts with a cushion word (細かい点ですが), explains the reason (get() never returns null), offers a concrete alternative (orElse), and ends with a soft request (ご検討いただけると幸いです).',
 '["細かい点ですが", "不要かと思います", "シンプルに書ける", "ご検討いただけると"]',
 '["直してください (too direct)", "間違っています (too blunt)", "ダメです (rude)", "nullチェックいらない (too casual)"]',
 '["nullチェック", "戻り値", "冗長", "簡潔に"]',
 NULL, NULL,
 'In Japanese code reviews, always start with a cushion word when pointing out issues to junior developers. Frame suggestions as possibilities (かもしれません) rather than commands. The goal is to educate, not embarrass.',
 75, true),

-- Exercise 2: Code Review — Security vulnerability (MUST_FIX)
('Review: SQL injection vulnerability', 'レビュー：SQLインジェクション脆弱性',
 'CODE_REVIEW', 'BACKEND_JAVA', 'N2',
 'String query = "SELECT * FROM users WHERE email = ''" + email + "''";\nStatement stmt = connection.createStatement();\nResultSet rs = stmt.executeQuery(query);',
 'java',
 'You found a critical SQL injection vulnerability in a PR. This is a blocking issue that must be fixed before merge. Write the review comment in Japanese.',
 'PRにクリティカルなSQLインジェクション脆弱性を発見しました。マージ前に必ず修正が必要です。日本語でレビューコメントを書いてください。',
 'MUST_FIX', '["SQL injection", "string concatenation in query", "should use PreparedStatement"]',
 'セキュリティ上の重要な指摘です。文字列結合によるSQL構築はSQLインジェクションの脆弱性になります。PreparedStatementのパラメータバインディングをご利用ください。本件はセキュリティに関わるため、修正をお願いいたします。\n\n修正例：\nPreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE email = ?");\nps.setString(1, email);',
 'Even for critical issues in Japanese reviews, maintain politeness. Use お願いいたします (formal request) instead of commands. Provide the fix example to be helpful.',
 '["セキュリティ上の", "脆弱性", "修正をお願いいたします", "パラメータバインディング"]',
 '["ダメです (too blunt)", "これはひどい (rude)", "なんでこんなコード (personal attack)", "修正しろ (imperative)"]',
 '["SQLインジェクション", "脆弱性", "パラメータバインディング", "PreparedStatement", "文字列結合"]',
 NULL, NULL,
 'For security issues, Japanese dev culture still requires politeness but allows more direct language. Use セキュリティ上の重要な指摘 to signal severity without being rude. Always provide the fix.',
 100, true),

-- Exercise 3: PR Writing — Feature addition
('Write a PR description for authentication feature', '認証機能のPR説明文を作成する',
 'PR_WRITING', 'BACKEND_JAVA', 'N3',
 NULL, NULL,
 'You implemented JWT authentication with refresh token rotation. Write the PR description in Japanese following the standard template: Background, Changes, Impact, Test Plan.',
 'JWTリフレッシュトークンローテーション付きの認証機能を実装しました。標準テンプレートに従ってPR説明文を日本語で書いてください。',
 NULL, '["JWT implementation", "refresh token rotation", "security headers"]',
 '## 背景\nセキュリティ要件として、ステートレスな認証基盤の構築が必要でした。\n\n## 変更内容\n- JWT認証フィルターの実装\n- リフレッシュトークンのローテーション機能追加\n- セキュリティヘッダーの設定\n\n## 影響範囲\n- 全認証済みエンドポイントに影響\n- 既存のセッション管理は廃止\n\n## テスト計画\n- ユニットテスト：JwtServiceTest, AuthFilterTest\n- 結合テスト：認証フロー全体のE2Eテスト\n- トークン期限切れ時の挙動確認',
 'A good Japanese PR description uses clear sections with headers, bullet points for changes, and specific test items. Keep sentences concise and use technical Japanese naturally.',
 '["背景", "変更内容", "影響範囲", "テスト計画", "実装", "追加", "確認"]',
 '["English section headers", "I did this... (first person narrative)", "too vague descriptions"]',
 '["認証", "ステートレス", "リフレッシュトークン", "エンドポイント", "結合テスト"]',
 '{"requiredSections":["BACKGROUND","CHANGES","IMPACT","TEST_PLAN"],"sectionHints":{"BACKGROUND":"Why was this needed?","CHANGES":"What specifically changed?","IMPACT":"What areas are affected?","TEST_PLAN":"How was this tested?"},"sectionKeyPhrases":{"BACKGROUND":["要件として","必要"],"CHANGES":["実装","追加","修正"],"IMPACT":["影響","範囲"],"TEST_PLAN":["テスト","確認"]},"minSections":4,"requiresJapaneseOnly":true}',
 NULL,
 'Japanese PR descriptions follow a structured template. The key difference from English PRs is conciseness — use noun phrases and avoid unnecessary politeness markers in technical descriptions. Headers are always in Japanese (背景, not Background).',
 80, true),

-- Exercise 4: Commit Message — Feature
('Write a commit message for adding user registration', 'ユーザー登録機能追加のコミットメッセージを書く',
 'COMMIT_MESSAGE', 'BACKEND_JAVA', 'N4',
 NULL, NULL,
 'You just implemented user registration with email validation. Write the commit message in Japanese following the convention: prefix + scope + noun-ending description.',
 'メールバリデーション付きのユーザー登録機能を実装しました。日本語のコンベンションに従ってコミットメッセージを書いてください。',
 NULL, NULL,
 'feat(auth): ユーザー登録機能の追加（メールバリデーション付き）',
 'Japanese commit messages use 体言止め (noun ending). 追加 ends the message, not 追加しました. The scope (auth) narrows the context. Keep it concise.',
 '["機能", "追加", "ユーザー登録"]',
 '["しました (verb ending)", "追加した (casual past)", "します (polite verb)"]',
 '["ユーザー登録", "バリデーション", "認証"]',
 NULL,
 '{"expectedPrefix":"FEAT","requireTaigenDome":true,"maxLength":72,"requireScope":true,"expectedScope":"auth","forbiddenPatterns":["しました","します","した","できた"],"goodExamples":["feat(auth): ユーザー登録機能の追加","feat(auth): メールバリデーション付きユーザー登録の実装"],"badExamples":["feat(auth): ユーザー登録機能を追加しました","ユーザー登録を作った","added user registration"]}',
 'In Japanese dev teams, commit messages use 体言止め (taigen-dome) — ending with a noun rather than a conjugated verb. This is a stylistic convention that signals professionalism. Think of it like headlines in a newspaper.',
 60, true),

-- Exercise 5: Commit Message — Bug fix
('Write a commit message for fixing a null pointer', 'NullPointerException修正のコミットメッセージ',
 'COMMIT_MESSAGE', 'BACKEND_JAVA', 'N4',
 NULL, NULL,
 'You fixed a NullPointerException that occurred when a user had no profile set. Write the commit message in Japanese.',
 'プロフィール未設定時のNullPointerExceptionを修正しました。日本語でコミットメッセージを書いてください。',
 NULL, NULL,
 'fix(user): プロフィール未設定時のNullPointerException修正',
 'Bug fix commits in Japanese are concise. State the condition (未設定時) and the fix outcome (修正). No verb conjugation needed.',
 '["修正", "未設定時", "NullPointerException"]',
 '["修正しました", "バグを直した", "fixed"]',
 '["NullPointerException", "プロフィール", "未設定"]',
 NULL,
 '{"expectedPrefix":"FIX","requireTaigenDome":true,"maxLength":72,"requireScope":true,"expectedScope":"user","forbiddenPatterns":["しました","した","直した","修正した"],"goodExamples":["fix(user): プロフィール未設定時のNPE修正","fix(user): null安全なプロフィール参照への修正"],"badExamples":["fix: NPEを修正しました","バグを直した","fixed null pointer"]}',
 'Note: NullPointerException can be abbreviated to NPE in Japanese dev context too. Both are acceptable.',
 50, true),

-- Exercise 6: Bug Report
('Write a bug report for a login failure', 'ログイン失敗のバグレポートを書く',
 'BUG_REPORT', 'BACKEND_JAVA', 'N3',
 NULL, NULL,
 'Users report that login fails intermittently with a 500 error. You reproduced it: it happens when the session cache is cold. Write a structured bug report in Japanese.',
 'ログインが500エラーで断続的に失敗するとユーザーから報告がありました。セッションキャッシュが冷えている時に再現しました。日本語で構造化されたバグレポートを書いてください。',
 NULL, '["session cache cold start", "500 error", "intermittent failure"]',
 '## 概要\nセッションキャッシュ未起動時にログインが500エラーで失敗する\n\n## 再現手順\n1. Redisキャッシュを再起動する\n2. キャッシュが空の状態でログインを試行する\n3. 500エラーが返される\n\n## 期待結果\nキャッシュミス時はDBにフォールバックしてログインが成功する\n\n## 実際の結果\nNullPointerExceptionが発生し、500エラーが返される\n\n## 環境\n- バージョン: v2.1.0\n- 環境: staging\n\n## 備考\n本番では約5%のログイン試行に影響している可能性あり',
 'Japanese bug reports follow a strict template: Overview (概要), Steps to Reproduce (再現手順), Expected (期待結果), Actual (実際の結果), Environment (環境), Notes (備考). Be factual and concise.',
 '["概要", "再現手順", "期待結果", "実際の結果", "環境"]',
 '["何かおかしい (too vague)", "バグってる (too casual)", "壊れてます (unhelpful)"]',
 '["セッションキャッシュ", "フォールバック", "NullPointerException", "断続的"]',
 NULL, NULL,
 'Japanese bug reports value precision and structure. Never blame individuals. State facts and provide reproducible steps. The 再現手順 (reproduction steps) section is the most critical — if you cannot reproduce, state conditions under which it occurs.',
 80, true),

-- Exercise 7: Code Review — Performance (SUGGESTION)
('Review: N+1 query in loop', 'レビュー：ループ内のN+1クエリ',
 'CODE_REVIEW', 'BACKEND_JAVA', 'N3',
 'List<Order> orders = orderRepository.findAll();\nfor (Order order : orders) {\n    Customer customer = customerRepository.findById(order.getCustomerId());\n    order.setCustomerName(customer.getName());\n}',
 'java',
 'You notice an N+1 query problem in a PR. The current implementation fetches customers one by one inside a loop. Suggest a batch fetch approach politely.',
 'PRにN+1クエリの問題を発見しました。ループ内で顧客を1件ずつ取得しています。バッチ取得のアプローチを丁寧に提案してください。',
 'SUGGESTION', '["N+1 query", "batch fetch", "JOIN or IN clause"]',
 'パフォーマンスの観点で一点提案です。現在ループ内で顧客を1件ずつ取得しているため、N+1問題が発生する可能性があります。customerRepository.findAllById(orderIds)でバッチ取得し、Mapに変換してからセットする方法はいかがでしょうか。データ量が少ない場合は現状でも問題ありませんが、将来的なスケーラビリティを考慮するとご検討いただけると幸いです。',
 'This review comment demonstrates the ideal Japanese pattern: context (パフォーマンスの観点), problem identification (N+1問題), concrete alternative (findAllById), and a soft hedge (データ量が少ない場合は問題ありません).',
 '["パフォーマンスの観点", "N+1問題", "バッチ取得", "いかがでしょうか", "ご検討いただけると"]',
 '["遅いです (too blunt)", "N+1だ (too casual)", "修正してください (too direct for suggestion)"]',
 '["N+1問題", "バッチ取得", "パフォーマンス", "スケーラビリティ"]',
 NULL, NULL,
 'For performance suggestions in Japanese reviews, always acknowledge that the current approach might work for small data sets (データ量が少ない場合). This shows you respect the author''s work while suggesting improvements for scalability.',
 75, true),

-- Exercise 8: Tech Discussion — Proposing a solution
('Propose a caching strategy in standup', 'スタンダップでキャッシュ戦略を提案する',
 'TECH_DISCUSSION', 'BACKEND_JAVA', 'N3',
 NULL, NULL,
 'During your team standup, you want to propose adding Redis caching for the user profile endpoint which is getting 10,000 requests per minute. Formulate your proposal in Japanese.',
 'チームスタンダップで、毎分10,000リクエストのユーザープロフィールエンドポイントにRedisキャッシュの導入を提案したいです。日本語で提案を書いてください。',
 NULL, '["Redis caching", "high traffic", "TTL strategy"]',
 'ユーザープロフィールのエンドポイントについてですが、現在毎分1万リクエストほど来ており、DBへの負荷が懸念されています。RedisによるキャッシュレイヤーをTTL 5分で導入することを検討してはいかがでしょうか。実装コストは1〜2日程度で、レスポンスタイムも改善できると考えています。もしよろしければ、設計ドキュメントを作成しますが、いかがでしょうか。',
 'Technical proposals in standup should: present the problem with data, propose a solution with specifics (TTL, timeline), and offer to take ownership. Always end with a question to invite discussion.',
 '["検討してはいかがでしょうか", "懸念されています", "と考えています", "いかがでしょうか"]',
 '["やるべき (too assertive)", "絶対必要 (too strong)", "俺がやる (too casual)"]',
 '["キャッシュ", "レスポンスタイム", "TTL", "負荷"]',
 NULL, NULL,
 'In Japanese standups, proposals should be framed as suggestions (いかがでしょうか), not declarations. Show data to support your point, estimate effort, and offer to own the work. Never be pushy — let the team decide.',
 70, true),

-- Exercise 9: Commit Message — Refactoring
('Write a commit for extracting a service class', 'サービスクラス抽出のコミットメッセージ',
 'COMMIT_MESSAGE', 'BACKEND_JAVA', 'N4',
 NULL, NULL,
 'You refactored a large controller by extracting business logic into a dedicated service class (UserService). Write the commit message.',
 'コントローラーからビジネスロジックを専用サービスクラス(UserService)に抽出するリファクタリングを行いました。コミットメッセージを書いてください。',
 NULL, NULL,
 'refactor(user): ビジネスロジックのUserServiceへの抽出',
 'Refactor commits describe the structural change without explaining why in the message body. 抽出 (extraction) is the noun form used in taigen-dome style.',
 '["リファクタリング", "抽出", "UserService"]',
 '["抽出しました", "移動した", "refactored"]',
 '["ビジネスロジック", "サービスクラス", "抽出"]',
 NULL,
 '{"expectedPrefix":"REFACTOR","requireTaigenDome":true,"maxLength":72,"requireScope":true,"expectedScope":"user","forbiddenPatterns":["しました","した","移した","分けた"],"goodExamples":["refactor(user): ビジネスロジックのUserServiceへの抽出","refactor(user): コントローラーからサービス層への責務分離"],"badExamples":["refactor: ロジックをサービスに移しました","UserServiceを作った"]}',
 'Refactoring commits use 抽出 (extraction), 分離 (separation), 統合 (consolidation), or 整理 (cleanup) as noun endings. These are standard Japanese dev vocabulary.',
 50, true),

-- Exercise 10: PR Writing — Bug fix PR
('Write a PR for fixing a race condition', '競合状態修正のPR説明文',
 'PR_WRITING', 'BACKEND_JAVA', 'N2',
 NULL, NULL,
 'You fixed a race condition in the payment processing module where two concurrent requests could debit the same account twice. Write the PR description in Japanese.',
 '決済処理モジュールで2つの同時リクエストが同じアカウントを二重に引き落とす競合状態を修正しました。PR説明文を日本語で書いてください。',
 NULL, '["race condition", "double debit", "pessimistic locking"]',
 '## 背景\n決済処理で同時リクエスト時に二重引き落としが発生する競合状態が報告されました（チケット #234）。\n\n## 原因\nアカウント残高の読み取りと更新の間にロックがなく、並行トランザクションが同じ残高を読み取っていました。\n\n## 変更内容\n- 悲観的ロック（SELECT FOR UPDATE）の導入\n- トランザクション分離レベルをREAD_COMMITTEDに設定\n- 二重引き落とし検知のバリデーション追加\n\n## 影響範囲\n- PaymentService, AccountRepository\n- パフォーマンス：ロック待ちにより決済処理が最大100ms遅延する可能性\n\n## テスト計画\n- 並行テスト：10スレッドで同時決済を実行し二重引き落としが発生しないことを確認\n- 既存テスト：全決済関連テストがパスすることを確認\n- 負荷テスト：ロック待ち時間が許容範囲内であることを確認',
 'Bug fix PRs in Japanese need a 原因 (root cause) section in addition to the standard template. Being transparent about performance impact shows engineering maturity.',
 '["背景", "原因", "変更内容", "影響範囲", "テスト計画", "競合状態", "悲観的ロック"]',
 '["I fixed... (first person)", "The bug was... (too vague)", "It works now (no detail)"]',
 '["競合状態", "二重引き落とし", "悲観的ロック", "トランザクション", "並行処理"]',
 '{"requiredSections":["BACKGROUND","CHANGES","IMPACT","TEST_PLAN"],"sectionHints":{"BACKGROUND":"Include ticket reference and root cause","CHANGES":"List specific changes with bullet points","IMPACT":"Performance implications","TEST_PLAN":"Concurrency testing details"},"sectionKeyPhrases":{"BACKGROUND":["報告","発生","原因"],"CHANGES":["導入","追加","設定"],"IMPACT":["影響","可能性","パフォーマンス"],"TEST_PLAN":["確認","テスト","実行"]},"minSections":4,"requiresJapaneseOnly":true}',
 NULL,
 'For bug fix PRs, Japanese teams expect: clear root cause analysis, specific changes with technical justification, honest assessment of side effects (performance hit), and thorough test plan. Transparency builds trust.',
 100, true),

-- Exercise 11: Code Review — Praise
('Write praise for elegant error handling', 'エレガントなエラーハンドリングへの称賛',
 'CODE_REVIEW', 'BACKEND_JAVA', 'N4',
 '@ExceptionHandler(BusinessException.class)\npublic ResponseEntity<ApiError> handleBusiness(BusinessException ex) {\n    ApiError error = ApiError.of(ex.getCode(), ex.getMessage());\n    log.warn("Business error: {}", ex.getMessage());\n    return ResponseEntity.status(ex.getHttpStatus()).body(error);\n}',
 'java',
 'A colleague implemented clean, well-structured error handling. Write a positive review comment in Japanese praising the approach.',
 '同僚がきれいで構造化されたエラーハンドリングを実装しました。アプローチを褒めるポジティブなレビューコメントを日本語で書いてください。',
 'PRAISE', NULL,
 'エラーハンドリングの設計がとても分かりやすいですね。ビジネス例外をHTTPステータスにきれいにマッピングしている点と、ログレベルを適切に使い分けている点が素晴らしいと思います。参考にさせていただきます！',
 'Praise in Japanese reviews is important for team morale. Be specific about what is good (not just すごい) and express that you learned something (参考にさせていただきます).',
 '["分かりやすい", "素晴らしい", "参考にさせていただきます", "きれいに"]',
 '["LGTM (only)", "いいね (too casual)", "OK (too minimal)"]',
 '["エラーハンドリング", "ビジネス例外", "マッピング", "ログレベル"]',
 NULL, NULL,
 'In Japanese code review culture, PRAISE comments (称賛) are valued for team building. Always be specific about WHY something is good. Saying 参考にさせていただきます (I will use this as reference) is a powerful compliment in Japanese workplace.',
 50, true),

-- Exercise 12: Tech Discussion — Asking for help
('Ask for help debugging a memory leak', 'メモリリークのデバッグで助けを求める',
 'TECH_DISCUSSION', 'BACKEND_JAVA', 'N3',
 NULL, NULL,
 'You have been investigating a memory leak for 2 days without success. You need to ask your senior developer for help during a 1-on-1. Write your request in Japanese.',
 'メモリリークを2日間調査していますが解決できていません。1on1でシニア開発者に助けを求める必要があります。日本語で依頼文を書いてください。',
 NULL, '["memory leak", "heap dump analysis", "need help"]',
 'お忙しいところ恐れ入りますが、メモリリークの件でご相談させていただけないでしょうか。2日間ヒープダンプの解析を行っているのですが、原因の特定に至っておりません。現状の調査内容を共有させていただき、もし何かヒントをいただけると大変助かります。お時間のある時で構いませんので、よろしくお願いいたします。',
 'Asking for help in Japanese dev teams requires: acknowledge their busy schedule, state your effort so far (2 days of investigation), be specific about what you need (hint/direction), and be flexible on timing.',
 '["恐れ入りますが", "ご相談させていただけないでしょうか", "至っておりません", "いただけると大変助かります"]',
 '["教えて (too casual)", "分からない (too direct)", "手伝って (too informal)", "助けてください (too blunt)"]',
 '["メモリリーク", "ヒープダンプ", "解析", "原因特定"]',
 NULL, NULL,
 'Asking for help is NOT weakness in Japanese teams, but HOW you ask matters. Always show you tried first (effort demonstration), be specific about what kind of help you need, and give the senior flexibility on timing (お時間のある時で).',
 70, true);
```

---

## 8. Integration avec le Systeme de Progression (BLOC 8)

Le `CodeExerciseCompletedEvent` est consomme par le `ProgressEventConsumer` pour :
- Ajouter du XP au user (base sur `exercise.xpReward` * score multiplier)
- Mettre a jour le `ModuleProgress` pour `ModuleType.CODE_REVIEW`
- Enregistrer un `LearningActivity` de type `CODE_EXERCISE_COMPLETED`

Ajouter dans `ActivityType` :
```java
CODE_EXERCISE_COMPLETED(70, 1.4)
```

Ajouter dans le `fromModuleType` switch :
```java
case CODE_REVIEW -> CODE_EXERCISE_COMPLETED;
```

---

## 9. File Structure Complet

### Domain Layer
| File | Responsibility |
|------|---------------|
| `domain/model/ExerciseType.java` | Enum: CODE_REVIEW, PR_WRITING, COMMIT_MESSAGE, BUG_REPORT, TECH_DISCUSSION |
| `domain/model/ReviewLevel.java` | Enum: SUGGESTION, REQUEST, MUST_FIX, QUESTION, PRAISE |
| `domain/model/CommitPrefix.java` | Enum: FEAT, FIX, REFACTOR, DOCS, TEST, PERF, CHORE, STYLE, CI |
| `domain/model/PRSection.java` | Enum: BACKGROUND, CHANGES, IMPACT, TEST_PLAN, NOTES, SCREENSHOTS |
| `domain/model/CodeContext.java` | Enum: BACKEND_JAVA, FRONTEND_REACT, DATABASE, etc. |
| `domain/model/ViolationType.java` | Enum: WRONG_TECHNICAL_TERM, CASUAL_IN_REVIEW, VERB_ENDING_IN_COMMIT, etc. |
| `domain/model/CodeReviewExercise.java` | Aggregate — exercice complet |
| `domain/model/PRTemplate.java` | Value object — structure PR attendue |
| `domain/model/CommitMessageRule.java` | Value object — regles commit |
| `domain/model/CodeExerciseScore.java` | Value object — score 5 dimensions |
| `domain/model/CodeExerciseAttempt.java` | Entity — tentative d'un user |
| `domain/model/TechnicalJapaneseViolation.java` | Value object — erreur detectee |
| `domain/model/CommitMessageAnalysis.java` | Value object — resultat analyse commit |
| `domain/model/CodeJapaneseProgress.java` | Entity — progression par type |
| `domain/event/CodeExerciseCompletedEvent.java` | Domain event |
| `domain/event/CodeMilestoneReachedEvent.java` | Domain event |

### Application Layer — Commands & DTOs
| File | Responsibility |
|------|---------------|
| `application/command/CreateCodeExerciseCommand.java` | Command creation exercice |
| `application/command/SubmitCodeExerciseResponseCommand.java` | Command soumission reponse |
| `application/command/ValidateCommitMessageCommand.java` | Command validation commit |
| `application/dto/CodeReviewExerciseDto.java` | DTO exercice |
| `application/dto/CodeExerciseAttemptDto.java` | DTO tentative avec score |
| `application/dto/CodeJapaneseProgressDto.java` | DTO progression |
| `application/dto/CodeExerciseScoreDto.java` | DTO score 5 dimensions |
| `application/dto/CommitMessageAnalysisDto.java` | DTO analyse commit |
| `application/dto/ViolationReportDto.java` | DTO rapport violations |

### Application Layer — Ports
| File | Responsibility |
|------|---------------|
| `application/port/in/GetExerciseCatalogPort.java` | Port IN — catalogue |
| `application/port/in/StartExercisePort.java` | Port IN — demarrer exercice |
| `application/port/in/SubmitCodeExerciseResponsePort.java` | Port IN — soumettre |
| `application/port/in/ValidateCommitMessagePort.java` | Port IN — validation standalone |
| `application/port/in/GetCodeExerciseHistoryPort.java` | Port IN — historique |
| `application/port/in/GetCodeJapaneseProgressPort.java` | Port IN — progression |
| `application/port/in/GetViolationReportPort.java` | Port IN — rapport erreurs |
| `application/port/in/CreateCodeExercisePort.java` | Port IN — CRUD (ADMIN) |
| `application/port/out/CodeExerciseRepositoryPort.java` | Port OUT |
| `application/port/out/CodeExerciseAttemptRepositoryPort.java` | Port OUT |
| `application/port/out/CodeJapaneseProgressRepositoryPort.java` | Port OUT |

### Application Layer — Use Cases
| File | Responsibility |
|------|---------------|
| `application/usecase/GetExerciseCatalogUseCase.java` | Catalogue filtre |
| `application/usecase/StartExerciseUseCase.java` | Charger exercice |
| `application/usecase/SubmitCodeExerciseResponseUseCase.java` | Evaluer + scorer |
| `application/usecase/ValidateCommitMessageUseCase.java` | Validation commit standalone |
| `application/usecase/GetCodeExerciseHistoryUseCase.java` | Historique |
| `application/usecase/GetCodeJapaneseProgressUseCase.java` | Progression |
| `application/usecase/GetViolationReportUseCase.java` | Rapport erreurs |
| `application/usecase/CreateCodeExerciseUseCase.java` | CRUD exercice |

### Application Layer — Commit Message Engine
| File | Responsibility |
|------|---------------|
| `application/service/codejapanese/CommitAnalysisStep.java` | Interface Chain of Responsibility |
| `application/service/codejapanese/CommitMessageValidator.java` | Orchestrateur validation commit |
| `application/service/codejapanese/steps/PrefixStep.java` | Detection/validation prefix |
| `application/service/codejapanese/steps/ScopeStep.java` | Detection/validation scope |
| `application/service/codejapanese/steps/TaigenDomeStep.java` | Verification 体言止め |
| `application/service/codejapanese/steps/LengthStep.java` | Verification longueur |
| `application/service/codejapanese/steps/VerbEndingDetectionStep.java` | Detection fins verbales interdites |
| `application/service/codejapanese/steps/TechnicalTermStep.java` | Verification termes techniques |

### Application Layer — Exercise Evaluators
| File | Responsibility |
|------|---------------|
| `application/service/codejapanese/CodeExerciseEvaluator.java` | Interface Strategy |
| `application/service/codejapanese/CodeExerciseEvaluatorFactory.java` | Factory par ExerciseType |
| `application/service/codejapanese/evaluators/CodeReviewEvaluator.java` | Evaluateur review comments |
| `application/service/codejapanese/evaluators/PRWritingEvaluator.java` | Evaluateur PR descriptions |
| `application/service/codejapanese/evaluators/CommitMessageEvaluator.java` | Evaluateur commits |
| `application/service/codejapanese/evaluators/BugReportEvaluator.java` | Evaluateur bug reports |
| `application/service/codejapanese/evaluators/TechDiscussionEvaluator.java` | Evaluateur discussions |
| `application/service/codejapanese/ReviewToneAnalyzer.java` | Analyseur de ton |
| `application/service/codejapanese/TechnicalTermDetector.java` | Detecteur termes IT japonais |
| `application/service/codejapanese/CushionWordDetector.java` | Detecteur mots amortisseurs |

### Infrastructure Layer — Persistence
| File | Responsibility |
|------|---------------|
| `infrastructure/persistence/entity/CodeReviewExerciseEntity.java` | JPA entity |
| `infrastructure/persistence/entity/CodeExerciseAttemptEntity.java` | JPA entity |
| `infrastructure/persistence/entity/CodeJapaneseProgressEntity.java` | JPA entity |
| `infrastructure/persistence/repository/JpaCodeReviewExerciseRepository.java` | Spring Data JPA |
| `infrastructure/persistence/repository/JpaCodeExerciseAttemptRepository.java` | Spring Data JPA |
| `infrastructure/persistence/repository/JpaCodeJapaneseProgressRepository.java` | Spring Data JPA |
| `infrastructure/persistence/mapper/CodeReviewExercisePersistenceMapper.java` | Domain <-> Entity |
| `infrastructure/persistence/mapper/CodeExerciseAttemptPersistenceMapper.java` | Domain <-> Entity |
| `infrastructure/persistence/mapper/CodeJapaneseProgressPersistenceMapper.java` | Domain <-> Entity |
| `infrastructure/persistence/adapter/CodeExerciseRepositoryAdapter.java` | Implements port |
| `infrastructure/persistence/adapter/CodeExerciseAttemptRepositoryAdapter.java` | Implements port |
| `infrastructure/persistence/adapter/CodeJapaneseProgressRepositoryAdapter.java` | Implements port |

### Infrastructure Layer — Web
| File | Responsibility |
|------|---------------|
| `infrastructure/web/controller/CodeJapaneseController.java` | REST controller (9 endpoints) |
| `infrastructure/web/request/SubmitCodeExerciseRequest.java` | Validated request |
| `infrastructure/web/request/ValidateCommitMessageRequest.java` | Validated request |
| `infrastructure/web/request/CreateCodeExerciseRequest.java` | Validated request (ADMIN) |

### Database
| File | Responsibility |
|------|---------------|
| `src/main/resources/db/migration/V10__code_in_japanese.sql` | Tables + seed data |

### Modified Files
| File | Change |
|------|--------|
| `domain/model/ActivityType.java` | Add CODE_EXERCISE_COMPLETED(70, 1.4) |
| `infrastructure/kafka/ProgressEventConsumer.java` | Handle CodeExerciseCompletedEvent |
| `src/main/resources/application.yml` | Add code-japanese-events Kafka topic |

### Tests
| File | Tests |
|------|-------|
| `CodeReviewExerciseTest.java` | 4 tests (model validation) |
| `CodeExerciseScoreTest.java` | 5 tests (weighted calculation, bounds) |
| `CommitMessageValidatorTest.java` | 8 tests (prefix, taigen-dome, scope, length, verbs) |
| `PrefixStepTest.java` | 4 tests |
| `TaigenDomeStepTest.java` | 5 tests |
| `VerbEndingDetectionStepTest.java` | 4 tests |
| `TechnicalTermStepTest.java` | 4 tests |
| `ReviewToneAnalyzerTest.java` | 5 tests |
| `CushionWordDetectorTest.java` | 4 tests |
| `CodeReviewEvaluatorTest.java` | 5 tests |
| `PRWritingEvaluatorTest.java` | 4 tests |
| `CommitMessageEvaluatorTest.java` | 5 tests |
| `BugReportEvaluatorTest.java` | 4 tests |
| `SubmitCodeExerciseResponseUseCaseTest.java` | 5 tests |
| `ValidateCommitMessageUseCaseTest.java` | 4 tests |
| `GetCodeJapaneseProgressUseCaseTest.java` | 3 tests |
| `CodeJapaneseArchitectureTest.java` | 3 tests (ArchUnit) |

---

## 10. Ameliorations par rapport au TODO initial

| TODO original | Amelioration apportee |
|---------------|----------------------|
| CodeReviewExercise domain model | + PRTemplate, CommitMessageRule, CommitMessageAnalysis, CodeJapaneseProgress value objects |
| PRWriting exercises | 5 types d'exercices (Review, PR, Commit, Bug Report, Tech Discussion) |
| CommitMessage validator | Chain of Responsibility avec 6 steps + TaigenDome innovation |
| CodeJapaneseController | 9 endpoints au lieu de 4, avec commit validation standalone + violation report |
| (absent) | Score multi-dimensionnel 5 axes (technical, japanese, tone, structure, team) |
| (absent) | ReviewToneAnalyzer (analyse du ton adapte au contexte de review) |
| (absent) | TechnicalTermDetector (verification des termes IT en japonais) |
| (absent) | CushionWordDetector (detection des mots amortisseurs dans les reviews) |
| (absent) | ViolationType taxonomy (8 types d'erreurs specifiques aux devs non-natifs) |
| (absent) | 12 seed exercises couvrant workflows dev reels en japonais |
| (absent) | Integration Kafka + Progress module (XP, streaks, ModuleType.CODE_REVIEW) |
| (absent) | TaigenDome validation (innovation unique : convention commits japonais) |

---

## 11. Checklist d'Implementation

### Phase 1 — Domain & Application (priorite haute)
- [ ] Enums: ExerciseType, ReviewLevel, CommitPrefix, PRSection, CodeContext, ViolationType
- [ ] Value objects: PRTemplate, CommitMessageRule, CodeExerciseScore, TechnicalJapaneseViolation, CommitMessageAnalysis
- [ ] Aggregate: CodeReviewExercise
- [ ] Entities: CodeExerciseAttempt, CodeJapaneseProgress
- [ ] Events: CodeExerciseCompletedEvent, CodeMilestoneReachedEvent
- [ ] Commands: CreateCodeExerciseCommand, SubmitCodeExerciseResponseCommand, ValidateCommitMessageCommand
- [ ] DTOs: CodeReviewExerciseDto, CodeExerciseAttemptDto, CodeJapaneseProgressDto, CodeExerciseScoreDto, CommitMessageAnalysisDto, ViolationReportDto
- [ ] Ports IN: 8 ports
- [ ] Ports OUT: 3 ports

### Phase 2 — Commit Message Engine
- [ ] CommitAnalysisStep interface
- [ ] PrefixStep (prefix detection and validation)
- [ ] ScopeStep (scope extraction and validation)
- [ ] TaigenDomeStep (noun-ending detection — innovation)
- [ ] LengthStep (max length check)
- [ ] VerbEndingDetectionStep (forbidden verb patterns)
- [ ] TechnicalTermStep (IT terminology validation)
- [ ] CommitMessageValidator orchestrateur

### Phase 3 — Exercise Evaluators
- [ ] CodeExerciseEvaluator interface
- [ ] CodeExerciseEvaluatorFactory
- [ ] CodeReviewEvaluator
- [ ] PRWritingEvaluator
- [ ] CommitMessageEvaluator
- [ ] BugReportEvaluator
- [ ] TechDiscussionEvaluator
- [ ] ReviewToneAnalyzer
- [ ] TechnicalTermDetector
- [ ] CushionWordDetector

### Phase 4 — Use Cases
- [ ] GetExerciseCatalogUseCase
- [ ] StartExerciseUseCase
- [ ] SubmitCodeExerciseResponseUseCase
- [ ] ValidateCommitMessageUseCase
- [ ] GetCodeExerciseHistoryUseCase
- [ ] GetCodeJapaneseProgressUseCase
- [ ] GetViolationReportUseCase
- [ ] CreateCodeExerciseUseCase

### Phase 5 — Infrastructure
- [ ] V10 migration (tables + seed data 12 exercises)
- [ ] JPA entities (3)
- [ ] JPA repositories (3)
- [ ] Persistence mappers (3)
- [ ] Repository adapters (3)
- [ ] CodeJapaneseController (9 endpoints)
- [ ] Request DTOs (3)
- [ ] ActivityType.CODE_EXERCISE_COMPLETED update
- [ ] Kafka: code-japanese-events topic + ProgressEventConsumer update

### Phase 6 — Tests
- [ ] Unit tests commit engine (25+ tests)
- [ ] Unit tests evaluators (18+ tests)
- [ ] Unit tests use cases (12+ tests)
- [ ] Unit tests domain models (9+ tests)
- [ ] ArchUnit test (3 tests)
- [ ] Compilation verified OK

---

## 12. Dependances

Aucune nouvelle dependance Maven requise. Le module utilise uniquement :
- Spring Boot starters (web, data-jpa, validation, security) — deja presents
- PostgreSQL + Flyway — deja presents
- Kafka — deja present
- ArchUnit — deja present

---

## 13. Contraintes

- Spring Boot 3.3.5, Spring Security 6.x
- Java 21 (records, sealed classes si utile, pattern matching)
- Hexagonal architecture respectee (domain ne depend pas de l'infra)
- Retro-compatible avec les 331+ tests existants
- Kafka events conformes a l'interface DomainEvent existante
- Exercises seed en donnees realistes (workflows dev au Japon)
- Score system doit etre extensible (futurs axes possibles)
- Commit message validation utilisable en standalone (API publique pour CI/CD integration)

---

## 14. Estime

**2-3 jours d'implementation** :
- Jour 1 : Domain + Commit Message Engine + Evaluators
- Jour 2 : Use Cases + Infrastructure (persistence, controller, migration)
- Jour 3 : Tests + Integration Kafka + Polish seed data
