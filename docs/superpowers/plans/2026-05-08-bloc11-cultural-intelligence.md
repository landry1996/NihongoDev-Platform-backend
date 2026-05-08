# BLOC 11 — Cultural Intelligence (Innovation)

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build an immersive cultural intelligence module that trains developers on Japanese workplace norms through scenario-based exercises, keigo validation, and multi-dimensional cultural scoring — bridging the gap between language knowledge and workplace-ready cultural fluency.

**Architecture:** Hexagonal (ports/adapters) with Strategy Pattern for scenario evaluators and Chain of Responsibility for keigo analysis. Domain events feed into the progress system (BLOC 8). Scoring is multi-dimensional: keigo correctness, cultural appropriateness, situational awareness, and professional tone.

**Tech Stack:** Java 21, Spring Boot 3.3.5, PostgreSQL (JSONB), Kafka, Flyway V9

---

## Innovation: Pourquoi ce module est unique

La plupart des apps de japonais enseignent la grammaire et le vocabulaire, mais **aucune** ne simule les situations culturelles du monde du travail IT au Japon :

- Refuser poliment un superieur (断る — kotowaru)
- Ecrire un email a un client en sonkeigo (尊敬語)
- Gerer un conflit avec un collegue en evitant la confrontation directe
- Se comporter pendant un nomikai (飲み会 — soiree d'equipe)
- Communiquer un retard sur un projet sans perdre la face
- Adapter son discours selon uchi/soto (内/外 — in-group/out-group)

Ce module transforme ces situations en **exercices interactifs scores** avec feedback actionnable.

---

## 1. Concepts Cles du Domaine

### 1.1 Scenario Culturel

Un scenario presente une situation de travail. L'utilisateur doit choisir/ecrire la reponse culturellement appropriee.

**Modes d'interaction :**
- `MULTIPLE_CHOICE` — 4 reponses, une seule culturellement optimale (les autres sont grammaticalement correctes mais culturellement maladroites)
- `FREE_TEXT` — L'utilisateur ecrit sa reponse, evaluee par le moteur de scoring
- `ROLE_PLAY` — Dialogue a trous, l'utilisateur complete les repliques

### 1.2 Dimensions Culturelles Scorees

| Dimension | Description | Poids |
|-----------|-------------|-------|
| Keigo (敬語) | Correctness du niveau de politesse | 30% |
| Appropriateness (場面適切さ) | Adequation au contexte situationnel | 25% |
| Uchi/Soto (内/外) | Respect de la hierarchie in-group/out-group | 20% |
| Indirectness (間接性) | Niveau d'implicite vs direct (preference japonaise) | 15% |
| Professional Tone (ビジネストーン) | Vocabulaire et expressions business | 10% |

### 1.3 Keigo — Les 3 Niveaux

| Type | Japonais | Usage | Exemple |
|------|----------|-------|---------|
| Sonkeigo (尊敬語) | Honoring others | Client, superieur externe | いらっしゃる, ご覧になる |
| Kenjougo (謙譲語) | Humbling oneself | Self to client/superior | 参る, 拝見する |
| Teineigo (丁寧語) | Polite neutral | Collegues, daily speech | です/ます |

Le `KeigoValidator` detecte les melanges incorrects (utiliser sonkeigo pour soi-meme, kenjougo pour un superieur, etc.).

---

## 2. Domain Model

### 2.1 Enumerations

```java
// Le contexte de travail ou se deroule le scenario
public enum WorkplaceContext {
    MEETING,           // 会議 — Reunion d'equipe/projet
    EMAIL,             // メール — Communication ecrite
    PHONE_CALL,        // 電話 — Appel client/collegue
    NOMIKAI,           // 飲み会 — Afterwork/soiree
    STANDUP,           // 朝会 — Daily standup
    ONE_ON_ONE,        // 1on1 — Entretien avec manager
    CLIENT_VISIT,      // 客先訪問 — Visite client
    ONBOARDING,        // 入社 — Premiers jours
    CODE_REVIEW,       // コードレビュー — Feedback technique
    CONFLICT           // トラブル — Resolution de conflit
}

// Le type de relation entre interlocuteurs
public enum RelationshipType {
    TO_SUPERIOR,       // 上司へ — Vers un superieur
    TO_PEER,           // 同僚へ — Vers un pair
    TO_JUNIOR,         // 後輩へ — Vers un junior
    TO_CLIENT,         // お客様へ — Vers un client
    TO_EXTERNAL,       // 社外へ — Vers une personne externe
    GROUP_MIXED        // 混合 — Groupe hierarchique mixte
}

// Le mode d'interaction de l'exercice
public enum ScenarioMode {
    MULTIPLE_CHOICE,
    FREE_TEXT,
    ROLE_PLAY
}

// Le niveau de keigo attendu
public enum KeigoLevel {
    CASUAL,            // タメ口 — Familier
    TEINEIGO,          // 丁寧語 — Poli standard (desu/masu)
    SONKEIGO,          // 尊敬語 — Honorifique (vers l'autre)
    KENJOUGO,          // 謙譲語 — Humble (vers soi)
    MIXED_FORMAL       // 混合敬語 — Contexte mixte (client + collegue)
}

// Categorie du scenario pour filtrage et progression
public enum ScenarioCategory {
    COMMUNICATION,     // Communication quotidienne
    REPORTING,         // Reporting/escalation
    SOCIAL,            // Situations sociales (nomikai, etc.)
    CONFLICT,          // Resolution de conflit
    CEREMONY,          // Rituels (debut/fin projet, accueil, etc.)
    NEGOTIATION        // Negociation/persuasion implicite
}
```

### 2.2 CulturalScenario (Aggregate)

```java
public class CulturalScenario {
    private UUID id;
    private String title;                       // "Refuser une tache de votre manager"
    private String titleJp;                     // "上司からのタスクを断る"
    private String situation;                   // Description detaillee de la situation (FR)
    private String situationJp;                 // Contexte en japonais
    private WorkplaceContext context;
    private RelationshipType relationship;
    private ScenarioMode mode;
    private ScenarioCategory category;
    private KeigoLevel expectedKeigoLevel;
    private JapaneseLevel difficulty;           // BEGINNER -> N1
    private List<ScenarioChoice> choices;       // Pour MULTIPLE_CHOICE
    private String modelAnswer;                 // Reponse ideale (pour FREE_TEXT scoring)
    private String modelAnswerExplanation;      // Pourquoi c'est la bonne reponse
    private List<String> keyPhrases;            // Expressions cles attendues
    private List<String> avoidPhrases;          // Expressions a eviter
    private String culturalNote;                // Explication culturelle pedagogique
    private int xpReward;
    private boolean published;
    private LocalDateTime createdAt;
}
```

### 2.3 ScenarioChoice (Value Object — pour MULTIPLE_CHOICE)

```java
public record ScenarioChoice(
    String text,                    // Le texte de la reponse
    String textJp,                  // En japonais
    boolean isOptimal,              // La meilleure reponse culturellement
    int culturalScore,              // 0-100 (meme les "mauvaises" ont un score partiel)
    String feedbackIfChosen         // Feedback specifique si le user choisit cette reponse
) {}
```

### 2.4 CulturalScore (Value Object — Score multi-dimensionnel)

```java
public record CulturalScore(
    int keigoScore,                 // 0-100 : correctness de la politesse
    int appropriatenessScore,       // 0-100 : adequation situationnelle
    int uchiSotoScore,              // 0-100 : respect hierarchie
    int indirectnessScore,          // 0-100 : niveau d'implicite
    int professionalToneScore,      // 0-100 : vocabulaire business
    int overallScore                // Weighted average
) {
    public static CulturalScore calculate(int keigo, int appropriateness, int uchiSoto, int indirectness, int professional) {
        int overall = (int) (keigo * 0.30 + appropriateness * 0.25 + uchiSoto * 0.20 + indirectness * 0.15 + professional * 0.10);
        return new CulturalScore(keigo, appropriateness, uchiSoto, indirectness, professional, overall);
    }
}
```

### 2.5 ScenarioAttempt (Entity — Tentative d'un user)

```java
public class ScenarioAttempt {
    private UUID id;
    private UUID userId;
    private UUID scenarioId;
    private String userResponse;               // Reponse donnee par l'utilisateur
    private UUID selectedChoiceId;             // Pour MULTIPLE_CHOICE
    private CulturalScore score;
    private List<KeigoViolation> violations;   // Erreurs de keigo detectees
    private String feedback;                   // Feedback genere
    private int timeSpentSeconds;
    private LocalDateTime attemptedAt;
}
```

### 2.6 KeigoViolation (Value Object)

```java
public record KeigoViolation(
    String originalText,           // Le texte fautif
    String suggestion,             // La correction suggeree
    KeigoLevel usedLevel,          // Le niveau utilise (incorrect)
    KeigoLevel expectedLevel,      // Le niveau attendu
    String rule,                   // La regle violee (ex: "sonkeigo_for_self")
    Severity severity              // MINOR, MODERATE, CRITICAL
) {}
```

### 2.7 CulturalProgress (Entity — Progression par categorie)

```java
public class CulturalProgress {
    private UUID id;
    private UUID userId;
    private ScenarioCategory category;
    private int scenariosCompleted;
    private int totalScore;                    // Somme des scores
    private int averageScore;                  // Moyenne
    private int bestScore;
    private int currentStreak;                 // Scenarios consecutifs > 70%
    private KeigoLevel unlockedLevel;          // Niveau le plus haut debloque
    private LocalDateTime lastActivityAt;
}
```

### 2.8 Domain Events

```java
public record ScenarioCompletedEvent(
    UUID eventId,
    UUID userId,
    UUID scenarioId,
    int overallScore,
    ScenarioCategory category,
    LocalDateTime occurredAt
) implements DomainEvent { ... }

public record KeigoMilestoneReachedEvent(
    UUID eventId,
    UUID userId,
    KeigoLevel level,
    int averageScore,
    LocalDateTime occurredAt
) implements DomainEvent { ... }
```

---

## 3. Keigo Validator — Moteur d'Analyse

### 3.1 Architecture (Chain of Responsibility)

```java
public interface KeigoAnalysisStep {
    List<KeigoViolation> analyze(String text, KeigoLevel expectedLevel, RelationshipType relationship);
}
```

**Steps implementes :**

| Step | Responsabilite |
|------|---------------|
| `VerbFormStep` | Detecte les formes verbales incorrectes (masu vs dictionary form vs sonkeigo form) |
| `PronounStep` | Verifie l'usage des pronoms (watashi vs boku vs ore ; anata vs [name]-san) |
| `HonorificPrefixStep` | Verifie o-/go- prefixes (お名前 vs 名前, ご連絡 vs 連絡) |
| `SentenceEndingStep` | Verifie les fins de phrase (desu/masu vs casual da/ne/yo) |
| `UchiSotoStep` | Verifie les marqueurs in-group/out-group (notre societe = 弊社 pas うちの会社 vers un client) |
| `BusinessExpressionStep` | Verifie les expressions business figees (お忙しいところ恐れ入りますが, etc.) |

### 3.2 KeigoValidator (Orchestrateur)

```java
@Component
public class KeigoValidator {
    private final List<KeigoAnalysisStep> steps;

    public KeigoValidationResult validate(String text, KeigoLevel expectedLevel, RelationshipType relationship) {
        List<KeigoViolation> allViolations = steps.stream()
            .flatMap(step -> step.analyze(text, expectedLevel, relationship).stream())
            .toList();

        int keigoScore = calculateScore(allViolations);
        return new KeigoValidationResult(keigoScore, allViolations);
    }
}
```

### 3.3 Patterns de Detection (Exemples)

```java
// VerbFormStep — Patterns
Map<String, String> SONKEIGO_VERB_MAP = Map.of(
    "する", "なさる",
    "行く", "いらっしゃる",
    "来る", "いらっしゃる",
    "食べる", "召し上がる",
    "見る", "ご覧になる",
    "言う", "おっしゃる",
    "いる", "いらっしゃる",
    "くれる", "くださる"
);

Map<String, String> KENJOUGO_VERB_MAP = Map.of(
    "する", "いたす",
    "行く", "参る",
    "来る", "参る",
    "食べる", "いただく",
    "見る", "拝見する",
    "言う", "申す",
    "いる", "おる",
    "もらう", "いただく"
);
```

---

## 4. Scenario Evaluator — Scoring Engine

### 4.1 Strategy Pattern

```java
public interface ScenarioEvaluator {
    CulturalScore evaluate(CulturalScenario scenario, String userResponse);
}
```

**Implementations :**

| Evaluator | Mode | Logique |
|-----------|------|---------|
| `MultipleChoiceEvaluator` | MULTIPLE_CHOICE | Score = choice.culturalScore, feedback = choice.feedbackIfChosen |
| `FreeTextEvaluator` | FREE_TEXT | KeigoValidator + keyword matching + indirectness analysis |
| `RolePlayEvaluator` | ROLE_PLAY | Sequence evaluation per dialogue turn |

### 4.2 FreeTextEvaluator — Detail

```java
@Component
public class FreeTextEvaluator implements ScenarioEvaluator {
    private final KeigoValidator keigoValidator;
    private final IndirectnessAnalyzer indirectnessAnalyzer;
    private final BusinessPhraseDetector businessPhraseDetector;

    @Override
    public CulturalScore evaluate(CulturalScenario scenario, String userResponse) {
        // 1. Keigo score via KeigoValidator
        KeigoValidationResult keigoResult = keigoValidator.validate(
            userResponse, scenario.getExpectedKeigoLevel(), scenario.getRelationship());

        // 2. Appropriateness: key phrases present + avoid phrases absent
        int appropriateness = evaluateAppropiateness(scenario, userResponse);

        // 3. Uchi/Soto: correct group markers
        int uchiSoto = evaluateUchiSoto(scenario, userResponse);

        // 4. Indirectness: level of implicitness vs directness
        int indirectness = indirectnessAnalyzer.score(userResponse, scenario.getContext());

        // 5. Professional tone: business vocabulary usage
        int professional = businessPhraseDetector.score(userResponse, scenario.getContext());

        return CulturalScore.calculate(keigoResult.score(), appropriateness, uchiSoto, indirectness, professional);
    }
}
```

### 4.3 IndirectnessAnalyzer

Mesure le degre d'implicite dans la reponse (prefere au Japon dans les situations delicates) :

| Pattern | Score bonus | Exemple |
|---------|-------------|---------|
| Cushion words (クッション言葉) | +15 | 恐れ入りますが, 申し訳ございませんが |
| Hedging (曖昧表現) | +10 | かもしれません, と思いますが |
| Negative question form | +10 | していただけないでしょうか |
| Suggestive phrasing | +10 | いかがでしょうか |
| Direct refusal (ストレート) | -20 | できません, 無理です |
| Blunt assertion | -15 | やります, やってください |

---

## 5. Application Layer — Use Cases

| Use Case | Responsabilite |
|----------|---------------|
| `StartScenarioUseCase` | Charger un scenario par filtres (context, difficulty, category) |
| `SubmitScenarioResponseUseCase` | Evaluer la reponse, calculer le score, enregistrer la tentative |
| `GetScenarioHistoryUseCase` | Historique par user + filtres |
| `GetCulturalProgressUseCase` | Progression par categorie + global |
| `GetKeigoReportUseCase` | Rapport des erreurs de keigo recurrentes |
| `GetScenarioCatalogUseCase` | Catalogue des scenarios disponibles (published, par difficulte) |

---

## 6. Infrastructure — Endpoints REST

### 6.1 CulturalController

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/cultural/scenarios` | Catalogue filtre (context, difficulty, category) | Authenticated |
| GET | `/api/cultural/scenarios/{id}` | Detail d'un scenario | Authenticated |
| POST | `/api/cultural/scenarios/{id}/attempt` | Soumettre une reponse | Authenticated |
| GET | `/api/cultural/history` | Historique des tentatives du user | Authenticated |
| GET | `/api/cultural/progress` | Progression culturelle par categorie | Authenticated |
| GET | `/api/cultural/progress/keigo` | Rapport keigo (violations recurrentes) | Authenticated |
| POST | `/api/cultural/scenarios` | Creer un scenario (ADMIN/TEACHER) | ADMIN, TEACHER |
| PUT | `/api/cultural/scenarios/{id}` | Modifier un scenario | ADMIN, TEACHER |

### 6.2 Request DTOs

```java
public record SubmitScenarioResponseRequest(
    @Size(max = 2000) String response,              // Pour FREE_TEXT / ROLE_PLAY
    UUID selectedChoiceId,                           // Pour MULTIPLE_CHOICE
    int timeSpentSeconds
) {}

public record CreateScenarioRequest(
    @NotBlank @Size(max = 200) String title,
    @Size(max = 200) String titleJp,
    @NotBlank @Size(max = 2000) String situation,
    @Size(max = 2000) String situationJp,
    @NotNull WorkplaceContext context,
    @NotNull RelationshipType relationship,
    @NotNull ScenarioMode mode,
    @NotNull ScenarioCategory category,
    @NotNull KeigoLevel expectedKeigoLevel,
    @NotNull JapaneseLevel difficulty,
    List<ScenarioChoiceData> choices,
    @Size(max = 2000) String modelAnswer,
    @Size(max = 2000) String modelAnswerExplanation,
    List<String> keyPhrases,
    List<String> avoidPhrases,
    @Size(max = 1000) String culturalNote,
    @Min(10) @Max(500) int xpReward
) {}
```

---

## 7. Database — Migration V9

```sql
-- V9__cultural_intelligence.sql

CREATE TABLE cultural_scenarios (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title           VARCHAR(200) NOT NULL,
    title_jp        VARCHAR(200),
    situation       TEXT NOT NULL,
    situation_jp    TEXT,
    context         VARCHAR(30) NOT NULL,
    relationship    VARCHAR(30) NOT NULL,
    mode            VARCHAR(30) NOT NULL,
    category        VARCHAR(30) NOT NULL,
    expected_keigo_level VARCHAR(30) NOT NULL,
    difficulty      VARCHAR(20) NOT NULL,
    choices         JSONB,
    model_answer    TEXT,
    model_answer_explanation TEXT,
    key_phrases     JSONB,
    avoid_phrases   JSONB,
    cultural_note   TEXT,
    xp_reward       INTEGER NOT NULL DEFAULT 50,
    published       BOOLEAN NOT NULL DEFAULT false,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE scenario_attempts (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL REFERENCES users(id),
    scenario_id     UUID NOT NULL REFERENCES cultural_scenarios(id),
    user_response   TEXT,
    selected_choice_id UUID,
    keigo_score     INTEGER NOT NULL,
    appropriateness_score INTEGER NOT NULL,
    uchi_soto_score INTEGER NOT NULL,
    indirectness_score INTEGER NOT NULL,
    professional_tone_score INTEGER NOT NULL,
    overall_score   INTEGER NOT NULL,
    violations      JSONB,
    feedback        TEXT,
    time_spent_seconds INTEGER NOT NULL DEFAULT 0,
    attempted_at    TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE cultural_progress (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL REFERENCES users(id),
    category        VARCHAR(30) NOT NULL,
    scenarios_completed INTEGER NOT NULL DEFAULT 0,
    total_score     INTEGER NOT NULL DEFAULT 0,
    average_score   INTEGER NOT NULL DEFAULT 0,
    best_score      INTEGER NOT NULL DEFAULT 0,
    current_streak  INTEGER NOT NULL DEFAULT 0,
    unlocked_level  VARCHAR(30) NOT NULL DEFAULT 'TEINEIGO',
    last_activity_at TIMESTAMP,
    UNIQUE(user_id, category)
);

CREATE INDEX idx_scenario_attempts_user ON scenario_attempts(user_id);
CREATE INDEX idx_scenario_attempts_scenario ON scenario_attempts(scenario_id);
CREATE INDEX idx_cultural_progress_user ON cultural_progress(user_id);
CREATE INDEX idx_cultural_scenarios_context ON cultural_scenarios(context, difficulty);
CREATE INDEX idx_cultural_scenarios_category ON cultural_scenarios(category, published);

-- Seed: 12 scenarios initiaux couvrant les situations IT les plus courantes
INSERT INTO cultural_scenarios (title, title_jp, situation, situation_jp, context, relationship, mode, category, expected_keigo_level, difficulty, choices, model_answer, key_phrases, avoid_phrases, cultural_note, xp_reward, published)
VALUES
-- Scenario 1: Refuser une tache pendant une reunion
('Declining extra work from your manager', '上司からの追加タスクを断る',
 'During a team meeting, your manager asks you to take on an additional project. You are already overloaded with your current sprint tasks. You need to decline politely without damaging the relationship.',
 'チームミーティング中、上司から追加プロジェクトの担当を依頼されました。現在のスプリントタスクで手一杯です。関係を壊さずに丁寧に断る必要があります。',
 'MEETING', 'TO_SUPERIOR', 'FREE_TEXT', 'COMMUNICATION', 'KENJOUGO', 'N3',
 NULL,
 'お声がけいただきありがとうございます。大変興味深いプロジェクトですが、現在のスプリントタスクの期限が迫っておりまして、品質を保つためにも、今回は見送らせていただけないでしょうか。次のスプリントであれば、ぜひ取り組ませていただきたいと思います。',
 '["お声がけいただき", "見送らせていただけないでしょうか", "次のスプリント", "品質を保つ"]',
 '["できません", "無理です", "忙しいので", "やりたくない"]',
 'In Japanese workplace culture, directly saying "no" (できません) is considered rude toward a superior. Instead, express gratitude first, give a valid reason, then offer an alternative timeline. This is called "yawarageru" (和らげる) — softening the refusal.',
 75, true),

-- Scenario 2: Premier jour — se presenter a l'equipe
('Self-introduction on your first day', '入社初日の自己紹介',
 'It is your first day at a Japanese IT company. You need to introduce yourself to the engineering team during the morning standup. Include your name, previous experience, and what you look forward to.',
 '日本のIT企業の入社初日です。朝会でエンジニアリングチームに自己紹介する必要があります。',
 'ONBOARDING', 'GROUP_MIXED', 'FREE_TEXT', 'CEREMONY', 'TEINEIGO', 'BEGINNER',
 NULL,
 'はじめまして。本日から入社いたしました[名前]と申します。前職では3年間Javaのバックエンド開発に携わっておりました。まだまだ分からないことが多いですが、一日も早く戦力になれるよう頑張りますので、どうぞよろしくお願いいたします。',
 '["と申します", "よろしくお願いいたします", "携わっておりました", "頑張ります"]',
 '["俺", "僕は", "よろしく (without お願いします)", "です (without ます in verbs)"]',
 'Japanese self-introductions (jikoshoukai) follow a strict structure: greeting → name → background → humility expression → yoroshiku. Ending with よろしくお願いいたします is essential and shows willingness to integrate.',
 50, true),

-- Scenario 3: Signaler un retard a un client (email)
('Reporting a project delay to a client', 'クライアントへの遅延報告メール',
 'Your team discovered a critical bug that will delay the release by one week. You need to write an email to the client informing them of the delay while maintaining trust.',
 'チームがリリースを1週間遅延させるクリティカルバグを発見しました。信頼を維持しながらクライアントに遅延を報告するメールを書く必要があります。',
 'EMAIL', 'TO_CLIENT', 'FREE_TEXT', 'REPORTING', 'SONKEIGO', 'N2',
 NULL,
 'お世話になっております。[プロジェクト名]の件でご連絡いたします。テスト工程にて重要な不具合が発見され、品質確保のため、リリース日を1週間延期させていただきたく存じます。ご迷惑をおかけし大変申し訳ございません。対策として[具体策]を実施しており、新しいスケジュールを本日中にご共有いたします。何卒ご理解賜りますようお願い申し上げます。',
 '["お世話になっております", "させていただきたく存じます", "申し訳ございません", "ご理解賜りますよう"]',
 '["遅れます", "バグがあって", "すみません (too casual)", "お願いします (should be 申し上げます)"]',
 'When reporting bad news to a Japanese client, always: (1) apologize sincerely, (2) explain the reason briefly, (3) present a concrete countermeasure (対策), and (4) commit to a new timeline. Never blame individuals.',
 100, true),

-- Scenario 4: Nomikai — gerer les conversations avec le CTO
('Navigating conversation at a team dinner', '飲み会でCTOとの会話',
 'At the team nomikai (drinking party), the CTO sits next to you and asks about your hobbies. The atmosphere is relaxed but he is still your superior.',
 'チームの飲み会でCTOが隣に座り、趣味について聞いてきました。雰囲気はカジュアルですが、上司であることに変わりありません。',
 'NOMIKAI', 'TO_SUPERIOR', 'MULTIPLE_CHOICE', 'SOCIAL', 'TEINEIGO', 'N4',
 '[{"text":"Gaming is my hobby, I play every night.","textJp":"ゲームが趣味です。毎晩やってます。","isOptimal":false,"culturalScore":45,"feedbackIfChosen":"Too casual for a CTO interaction. Even at nomikai, maintain teineigo with superiors. Also, revealing that you game every night might suggest you dont prioritize work-life balance."},{"text":"I enjoy programming side projects on weekends. Recently I have been learning Rust.","textJp":"週末は個人プロジェクトでプログラミングをしています。最近はRustを勉強しています。","isOptimal":true,"culturalScore":90,"feedbackIfChosen":"Perfect balance: teineigo maintained, shows intellectual curiosity relevant to work, and invites follow-up conversation. Mentioning a technical hobby to a CTO is strategically smart."},{"text":"I like drinking! This beer is great!","textJp":"飲むのが好きです！このビールうまい！","isOptimal":false,"culturalScore":30,"feedbackIfChosen":"Too casual language (うまい instead of おいしいです). Also, centering the conversation on drinking rather than sharing about yourself misses the social bonding opportunity."},{"text":"Thank you for asking. I do not have any particular hobbies.","textJp":"聞いていただきありがとうございます。特に趣味はありません。","isOptimal":false,"culturalScore":55,"feedbackIfChosen":"Polite but too closed. At nomikai, the purpose is team bonding. Not sharing about yourself creates distance. Even a small hobby shows personality."}]',
 NULL, NULL, NULL,
 'Nomikai has a dual nature: casual atmosphere but professional relationships remain. Use teineigo (not casual speech) with superiors. Share interests that show positive traits. Its an opportunity to build rapport (親睦を深める).',
 60, true);
```

---

## 8. Integration avec le Systeme de Progression (BLOC 8)

Le `ScenarioCompletedEvent` est consomme par le `ProgressEventConsumer` pour :
- Ajouter du XP au user (base sur `scenario.xpReward` * score multiplier)
- Mettre a jour le `ModuleProgress` pour `ModuleType.CULTURAL`
- Enregistrer un `LearningActivity` de type `CULTURAL_SCENARIO`

Ajouter dans `ModuleType` :
```java
CULTURAL  // Module Cultural Intelligence
```

Ajouter dans `ActivityType` :
```java
CULTURAL_SCENARIO_COMPLETED
```

---

## 9. File Structure Complet

### Domain Layer
| File | Responsibility |
|------|---------------|
| `domain/model/WorkplaceContext.java` | Enum: MEETING, EMAIL, PHONE_CALL, NOMIKAI, etc. |
| `domain/model/RelationshipType.java` | Enum: TO_SUPERIOR, TO_PEER, TO_CLIENT, etc. |
| `domain/model/ScenarioMode.java` | Enum: MULTIPLE_CHOICE, FREE_TEXT, ROLE_PLAY |
| `domain/model/KeigoLevel.java` | Enum: CASUAL, TEINEIGO, SONKEIGO, KENJOUGO, MIXED_FORMAL |
| `domain/model/ScenarioCategory.java` | Enum: COMMUNICATION, REPORTING, SOCIAL, etc. |
| `domain/model/CulturalScenario.java` | Aggregate — scenario complet |
| `domain/model/ScenarioChoice.java` | Value object — choix pour MULTIPLE_CHOICE |
| `domain/model/CulturalScore.java` | Value object — score 5 dimensions |
| `domain/model/ScenarioAttempt.java` | Entity — tentative d'un user |
| `domain/model/KeigoViolation.java` | Value object — erreur de keigo detectee |
| `domain/model/CulturalProgress.java` | Entity — progression par categorie |
| `domain/event/ScenarioCompletedEvent.java` | Domain event |
| `domain/event/KeigoMilestoneReachedEvent.java` | Domain event |

### Application Layer
| File | Responsibility |
|------|---------------|
| `application/command/CreateScenarioCommand.java` | Command pour creation scenario |
| `application/command/SubmitScenarioResponseCommand.java` | Command pour soumission reponse |
| `application/dto/CulturalScenarioDto.java` | DTO scenario |
| `application/dto/ScenarioAttemptDto.java` | DTO tentative avec score |
| `application/dto/CulturalProgressDto.java` | DTO progression |
| `application/dto/KeigoReportDto.java` | DTO rapport keigo |
| `application/dto/CulturalScoreDto.java` | DTO score 5 dimensions |
| `application/port/in/StartScenarioPort.java` | Port IN |
| `application/port/in/SubmitScenarioResponsePort.java` | Port IN |
| `application/port/in/GetScenarioHistoryPort.java` | Port IN |
| `application/port/in/GetCulturalProgressPort.java` | Port IN |
| `application/port/in/GetKeigoReportPort.java` | Port IN |
| `application/port/in/GetScenarioCatalogPort.java` | Port IN |
| `application/port/in/CreateScenarioPort.java` | Port IN (ADMIN) |
| `application/port/out/ScenarioRepositoryPort.java` | Port OUT |
| `application/port/out/ScenarioAttemptRepositoryPort.java` | Port OUT |
| `application/port/out/CulturalProgressRepositoryPort.java` | Port OUT |
| `application/usecase/StartScenarioUseCase.java` | Charger scenario |
| `application/usecase/SubmitScenarioResponseUseCase.java` | Evaluer + scorer |
| `application/usecase/GetScenarioHistoryUseCase.java` | Historique |
| `application/usecase/GetCulturalProgressUseCase.java` | Progression |
| `application/usecase/GetKeigoReportUseCase.java` | Rapport keigo |
| `application/usecase/GetScenarioCatalogUseCase.java` | Catalogue filtre |
| `application/usecase/CreateScenarioUseCase.java` | CRUD scenario |

### Application Layer — Keigo Engine
| File | Responsibility |
|------|---------------|
| `application/service/cultural/KeigoAnalysisStep.java` | Interface Chain of Responsibility |
| `application/service/cultural/KeigoValidator.java` | Orchestrateur keigo |
| `application/service/cultural/KeigoValidationResult.java` | Resultat d'analyse |
| `application/service/cultural/steps/VerbFormStep.java` | Detection formes verbales |
| `application/service/cultural/steps/PronounStep.java` | Verification pronoms |
| `application/service/cultural/steps/HonorificPrefixStep.java` | Verification o-/go- |
| `application/service/cultural/steps/SentenceEndingStep.java` | Fins de phrase |
| `application/service/cultural/steps/UchiSotoStep.java` | Marqueurs in-group/out-group |
| `application/service/cultural/steps/BusinessExpressionStep.java` | Expressions business |

### Application Layer — Scenario Evaluators
| File | Responsibility |
|------|---------------|
| `application/service/cultural/ScenarioEvaluator.java` | Interface Strategy |
| `application/service/cultural/ScenarioEvaluatorFactory.java` | Factory par ScenarioMode |
| `application/service/cultural/evaluators/MultipleChoiceEvaluator.java` | Evaluateur QCM |
| `application/service/cultural/evaluators/FreeTextEvaluator.java` | Evaluateur texte libre |
| `application/service/cultural/evaluators/RolePlayEvaluator.java` | Evaluateur dialogue |
| `application/service/cultural/IndirectnessAnalyzer.java` | Analyseur d'implicite |
| `application/service/cultural/BusinessPhraseDetector.java` | Detecteur expressions business |

### Infrastructure Layer — Persistence
| File | Responsibility |
|------|---------------|
| `infrastructure/persistence/entity/CulturalScenarioEntity.java` | JPA entity |
| `infrastructure/persistence/entity/ScenarioAttemptEntity.java` | JPA entity |
| `infrastructure/persistence/entity/CulturalProgressEntity.java` | JPA entity |
| `infrastructure/persistence/repository/JpaCulturalScenarioRepository.java` | Spring Data JPA |
| `infrastructure/persistence/repository/JpaScenarioAttemptRepository.java` | Spring Data JPA |
| `infrastructure/persistence/repository/JpaCulturalProgressRepository.java` | Spring Data JPA |
| `infrastructure/persistence/mapper/CulturalScenarioPersistenceMapper.java` | Domain <-> Entity |
| `infrastructure/persistence/mapper/ScenarioAttemptPersistenceMapper.java` | Domain <-> Entity |
| `infrastructure/persistence/mapper/CulturalProgressPersistenceMapper.java` | Domain <-> Entity |
| `infrastructure/persistence/adapter/ScenarioRepositoryAdapter.java` | Implements port |
| `infrastructure/persistence/adapter/ScenarioAttemptRepositoryAdapter.java` | Implements port |
| `infrastructure/persistence/adapter/CulturalProgressRepositoryAdapter.java` | Implements port |

### Infrastructure Layer — Web
| File | Responsibility |
|------|---------------|
| `infrastructure/web/controller/CulturalController.java` | REST controller (8 endpoints) |
| `infrastructure/web/request/SubmitScenarioResponseRequest.java` | Validated request |
| `infrastructure/web/request/CreateScenarioRequest.java` | Validated request (ADMIN) |

### Database
| File | Responsibility |
|------|---------------|
| `src/main/resources/db/migration/V9__cultural_intelligence.sql` | Tables + seed data |

### Modified Files
| File | Change |
|------|--------|
| `domain/model/ModuleType.java` | Add CULTURAL |
| `domain/model/ActivityType.java` | Add CULTURAL_SCENARIO_COMPLETED |
| `infrastructure/kafka/ProgressEventConsumer.java` | Handle ScenarioCompletedEvent |
| `src/main/resources/application.yml` | Add cultural-events Kafka topic |

### Tests
| File | Tests |
|------|-------|
| `CulturalScenarioTest.java` | 4 tests (model validation) |
| `CulturalScoreTest.java` | 5 tests (weighted calculation, bounds) |
| `KeigoValidatorTest.java` | 8 tests (verb forms, pronouns, uchi/soto, etc.) |
| `VerbFormStepTest.java` | 5 tests |
| `PronounStepTest.java` | 4 tests |
| `UchiSotoStepTest.java` | 4 tests |
| `IndirectnessAnalyzerTest.java` | 5 tests |
| `FreeTextEvaluatorTest.java` | 5 tests |
| `MultipleChoiceEvaluatorTest.java` | 3 tests |
| `SubmitScenarioResponseUseCaseTest.java` | 5 tests |
| `GetCulturalProgressUseCaseTest.java` | 3 tests |
| `CulturalArchitectureTest.java` | 3 tests (ArchUnit) |

---

## 10. Ameliorations par rapport au TODO initial

| TODO original | Amelioration apportee |
|---------------|----------------------|
| CulturalScenario domain model | + ScenarioChoice, CulturalProgress, KeigoViolation, ScenarioAttempt value objects |
| Cultural score system | Score multi-dimensionnel 5 axes ponderes (pas un simple score unique) |
| KeigoValidator | Chain of Responsibility avec 6 steps specialises + IndirectnessAnalyzer |
| CulturalController | 8 endpoints au lieu de 4, avec CRUD admin + rapport keigo + catalogue |
| (absent) | Integration Kafka + Progress module (XP, streaks, ModuleType.CULTURAL) |
| (absent) | 12 scenarios seed couvrant les situations IT reelles |
| (absent) | 3 modes d'interaction (QCM, texte libre, role-play) |
| (absent) | Uchi/Soto analysis (innovation unique) |
| (absent) | IndirectnessAnalyzer (mesure la preference japonaise pour l'implicite) |
| (absent) | BusinessPhraseDetector (expressions figees du monde pro) |

---

## 11. Checklist d'Implementation

### Phase 1 — Domain & Application (priorite haute)
- [ ] Enums: WorkplaceContext, RelationshipType, ScenarioMode, KeigoLevel, ScenarioCategory
- [ ] Value objects: ScenarioChoice, CulturalScore, KeigoViolation
- [ ] Aggregate: CulturalScenario
- [ ] Entities: ScenarioAttempt, CulturalProgress
- [ ] Events: ScenarioCompletedEvent, KeigoMilestoneReachedEvent
- [ ] Commands: CreateScenarioCommand, SubmitScenarioResponseCommand
- [ ] DTOs: CulturalScenarioDto, ScenarioAttemptDto, CulturalProgressDto, KeigoReportDto, CulturalScoreDto
- [ ] Ports IN: 7 ports
- [ ] Ports OUT: 3 ports

### Phase 2 — Keigo Engine
- [ ] KeigoAnalysisStep interface
- [ ] VerbFormStep (sonkeigo/kenjougo verb detection)
- [ ] PronounStep (watashi/boku/ore detection)
- [ ] HonorificPrefixStep (o-/go- prefix verification)
- [ ] SentenceEndingStep (desu/masu vs casual)
- [ ] UchiSotoStep (heisha/onsha markers)
- [ ] BusinessExpressionStep (set phrases)
- [ ] KeigoValidator orchestrateur
- [ ] KeigoValidationResult

### Phase 3 — Evaluators
- [ ] ScenarioEvaluator interface
- [ ] ScenarioEvaluatorFactory
- [ ] MultipleChoiceEvaluator
- [ ] FreeTextEvaluator
- [ ] RolePlayEvaluator
- [ ] IndirectnessAnalyzer
- [ ] BusinessPhraseDetector

### Phase 4 — Use Cases
- [ ] StartScenarioUseCase
- [ ] SubmitScenarioResponseUseCase
- [ ] GetScenarioHistoryUseCase
- [ ] GetCulturalProgressUseCase
- [ ] GetKeigoReportUseCase
- [ ] GetScenarioCatalogUseCase
- [ ] CreateScenarioUseCase

### Phase 5 — Infrastructure
- [ ] V9 migration (tables + seed data 12 scenarios)
- [ ] JPA entities (3)
- [ ] JPA repositories (3)
- [ ] Persistence mappers (3)
- [ ] Repository adapters (3)
- [ ] CulturalController (8 endpoints)
- [ ] Request DTOs (2)
- [ ] ModuleType.CULTURAL + ActivityType update
- [ ] Kafka: cultural-events topic + ProgressEventConsumer update

### Phase 6 — Tests
- [ ] Unit tests keigo engine (21+ tests)
- [ ] Unit tests evaluators (13+ tests)
- [ ] Unit tests use cases (8+ tests)
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
- Retro-compatible avec les 266+ tests existants
- Kafka events conformes a l'interface DomainEvent existante
- Scenarios seed en donnees realistes (situations IT au Japon)
- Score system doit etre extensible (futurs axes possibles)

---

## 14. Estime

**2 jours d'implementation** :
- Jour 1 : Domain + Keigo Engine + Evaluators + Use Cases
- Jour 2 : Infrastructure (persistence, controller, migration, tests)
