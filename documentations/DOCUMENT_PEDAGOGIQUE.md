# NihongoDev Platform — Document Pedagogique

## 1. Philosophie Pedagogique

### 1.1 Approche

NihongoDev adopte une pedagogie **contextuelle et immersive** : l'apprenant n'etudie pas le japonais de maniere abstraite, mais directement dans les situations qu'il rencontrera en entreprise japonaise. Chaque exercice simule un cas reel du quotidien d'un developpeur au Japon.

### 1.2 Principes Directeurs

| Principe | Application |
|----------|-------------|
| Apprentissage par la pratique | Quiz interactifs, simulations, exercices de redaction |
| Contextualisation IT | Vocabulaire, exemples et scenarios tires du monde du developpement |
| Progression adaptive | Algorithme SRS, difficulte ajustee, recommandations personnalisees |
| Gamification | XP, niveaux, badges, streaks, leaderboard |
| Feedback immediat | Correction en temps reel, scores instantanes, suggestions |
| Immersion culturelle | Keigo, normes sociales, codes d'entreprise japonaise |

### 1.3 Alignement JLPT

La plateforme s'aligne sur les niveaux du Japanese Language Proficiency Test :

| Niveau | Competence NihongoDev | Situation professionnelle |
|--------|----------------------|--------------------------|
| N5 | Lecture hiragana/katakana, vocabulaire de base | Comprendre les menus, noms de fichiers |
| N4 | Phrases simples, vocabulaire IT basique | Lire des commentaires de code simples |
| N3 | Conversations quotidiennes, emails basiques | Participer aux standups, lire la doc |
| N2 | Japonais professionnel, keigo de base | Rediger des PR, emails clients, entretiens |
| N1 | Maitrise avancee, nuances culturelles | Presenter en reunion, negocier, mentorer |

---

## 2. Parcours d'Apprentissage

### 2.1 Parcours Debutant (0-3 mois)

```
Semaine 1-2 : Hiragana + Katakana (Lessons)
    │
    ▼
Semaine 3-4 : Vocabulaire IT basique (Vocabulary)
    │         - Termes de programmation
    │         - Commandes git en japonais
    │         - Noms des langages
    │
    ▼
Semaine 5-6 : Quiz de consolidation (Quiz - CLASSIC mode)
    │
    ▼
Semaine 7-8 : Premiers scenarios culturels (Cultural)
    │         - Se presenter
    │         - Salutations au bureau
    │
    ▼
Semaine 9-12 : Correction de textes simples (Correction)
              - Messages Slack en japonais
              - Commit messages
```

### 2.2 Parcours Intermediaire (3-6 mois)

```
Mois 3-4 : Grammaire technique (Lessons N3-N2)
    │       - Particules dans le contexte IT
    │       - Formes conditionnelles pour la doc
    │
    ▼
Mois 4-5 : Simulations d'entretien (Interview)
    │       - Self-introduction
    │       - Questions techniques en japonais
    │       - Behavioral questions
    │
    ▼
Mois 5-6 : Redaction professionnelle (Code Japanese)
    │       - PR reviews en japonais
    │       - Emails au client
    │       - Rapports de standup
    │
    ▼
Mois 6 : Lecture de contenu reel (Real Content)
         - Articles tech japonais
         - Documentation officielle
```

### 2.3 Parcours Avance (6-12 mois)

```
Mois 6-8 : Maitrise du keigo (Cultural Intelligence)
    │       - Sonkeigo (respect pour les superieurs)
    │       - Kenjougo (humilite pour soi)
    │       - Teineigo (politesse generale)
    │
    ▼
Mois 8-10 : Entretiens avances (Interview ADVANCED)
    │        - Tech Java/Spring/AWS en japonais
    │        - Business Japanese
    │        - Negociation de conditions
    │
    ▼
Mois 10-12 : Preparation au marche
             - Generation CV/Pitch (CV Generator)
             - Portfolio public (Portfolio)
             - Activation "Open to Work"
```

---

## 3. Modules Pedagogiques en Detail

### 3.1 Vocabulaire IT Japonais

#### Methode : Spaced Repetition System (SM-2)

L'algorithme SM-2 ajuste les intervalles de revision selon la performance :

```
Reponse correcte (facile)  → Intervalle x 2.5
Reponse correcte (moyen)   → Intervalle x 2.0
Reponse correcte (dur)     → Intervalle x 1.3
Reponse incorrecte         → Reset a 1 jour
```

#### Categories de Vocabulaire

| Categorie | Exemples | Contexte d'usage |
|-----------|----------|------------------|
| PROGRAMMING | 変数 (hensu/variable), 関数 (kansu/function), 配列 (hairetsu/array) | Code, documentation |
| DATABASE | テーブル, クエリ, インデックス | Schema design, requetes |
| NETWORK | サーバー, プロトコル, ファイアウォール | Infrastructure |
| SECURITY | 認証 (ninshou/auth), 暗号化 (angoka/encryption) | Security reviews |
| AGILE | スプリント, バックログ, ふりかえり | Ceremonies Scrum |
| DEVOPS | デプロイ, パイプライン, コンテナ | CI/CD |
| CLOUD | クラウド, マイクロサービス, スケーリング | Architecture |
| GENERAL | 会議 (kaigi/reunion), 締め切り (shimekiri/deadline) | Vie de bureau |

#### Progression de Maitrise

```
NEW → LEARNING → REVIEWING → MASTERED
 │       │          │           │
 │       │          │           └── Intervalle > 21 jours
 │       │          └── 3+ revisions correctes
 │       └── 1ere revision correcte
 └── Jamais vu
```

---

### 3.2 Quiz Multi-Mode

#### Modes de Jeu et Objectifs Pedagogiques

| Mode | Mecanique | Objectif pedagogique |
|------|-----------|---------------------|
| CLASSIC | Questions sequentielles, pas de pression | Consolidation, revision calme |
| TIMED | Limite de temps par question | Reflexes, automatisation |
| SURVIVAL | 3 vies, game over a 0 | Concentration, precision |
| SPRINT | Maximum de bonnes reponses en temps limite | Vitesse de rappel |
| REVIEW | Uniquement le vocabulaire en difficulte | Cibler les faiblesses |

#### Types de Questions

| Type | Competence evaluee |
|------|-------------------|
| MULTIPLE_CHOICE | Reconnaissance, discrimination |
| TRUE_FALSE | Comprehension rapide |
| TEXT_INPUT | Production active, orthographe |
| MATCHING | Associations, relations |

#### Systeme de Score

```
Score = Base × Difficulty_Multiplier × Streak_Bonus + Time_Bonus

- Base : 100 points par bonne reponse
- Difficulty_Multiplier : EASY(1.0), MEDIUM(1.5), HARD(2.0)
- Streak_Bonus : ×1.1 par reponse consecutive (max ×2.0)
- Time_Bonus : 0-50 points selon rapidite
```

---

### 3.3 Simulation d'Entretien

#### Structure d'un Entretien

```
Phase 1 : INTRODUCTION (1-2 questions)
    - Jiko-shoukai (自己紹介 / self-introduction)
    - Ice-breaking en japonais

Phase 2 : MAIN_QUESTIONS (3-5 questions)
    - Questions techniques OU comportementales
    - Adaptees au type d'entretien choisi

Phase 3 : FOLLOW_UP (1-2 questions)
    - Approfondissement des reponses
    - Questions de precision

Phase 4 : CLOSING (1 question)
    - "何か質問はありますか？" (Avez-vous des questions ?)
    - Formules de cloture
```

#### Criteres d'Evaluation (4 dimensions)

| Dimension | Poids | Ce qui est evalue |
|-----------|-------|-------------------|
| Technique | 30% | Exactitude des reponses, mots-cles attendus |
| Langue | 30% | Grammaire, vocabulaire, keigo |
| Culture | 20% | Formules de politesse, structure de reponse |
| Structure | 20% | Methode STAR, introduction/conclusion, clarte |

#### Strategies d'Evaluation

1. **KeywordBased** : Detection de mots-cles attendus + scoring de la presence de keigo
2. **ReferenceAnswer** : Similarite de Jaccard avec la reponse modele + suggestions de vocabulaire
3. **Structure** : Detection de la methode STAR + analyse intro/conclusion

---

### 3.4 Correction Intelligente

#### Pipeline de Correction (6 etapes)

```
Texte utilisateur
    │
    ▼
┌─────────────────┐
│ 1. GrammarStep  │  Particules, conjugaisons, redundances
└────────┬────────┘
         ▼
┌─────────────────┐
│ 2. VocabularyStep│  Registre de langue, termes IT
└────────┬────────┘
         ▼
┌─────────────────┐
│ 3. PolitenessStep│  Keigo, desu/masu, contexte
└────────┬────────┘
         ▼
┌─────────────────┐
│ 4. ClarityStep  │  Longueur phrases, structure
└────────┬────────┘
         ▼
┌─────────────────────┐
│ 5. NaturalnessStep  │  Patterns non-naturels, connecteurs
└────────┬────────────┘
         ▼
┌──────────────────────┐
│ 6. ProfessionalStep  │  Opener/closer, termes IT, emoji
└────────┬─────────────┘
         ▼
Annotations fusionnees + Score 6D
```

#### Contextes et Niveaux de Strictesse

| Contexte | Keigo requis | Formalite | Cas d'usage |
|----------|-------------|-----------|-------------|
| EMAIL_TO_CLIENT | Strict (sonkeigo) | Tres haute | Email a un client japonais |
| STANDUP_REPORT | Moyen (teineigo) | Moyenne | Rapport quotidien |
| CODE_REVIEW | Bas (teineigo) | Moyenne | Review de PR |
| INTERVIEW | Strict | Haute | Entretien d'embauche |
| SLACK | Relaxe | Basse | Chat interne equipe |
| COMMIT | Aucun | Technique | Messages de commit |

---

### 3.5 Intelligence Culturelle

#### Scenarios Interactifs

Les scenarios placent l'apprenant dans des situations reelles :

| Scenario | Competence culturelle |
|----------|----------------------|
| Premier jour au bureau | Aisatsu (挨拶), presentation, meishi (名刺) |
| Reunion d'equipe | Tour de parole, ne pas couper, "hai" actif |
| Email au superieur | Keigo d'ouverture/fermeture, structure |
| Nomikai (飲み会) | Regles de l'alcool au travail, hierarchie |
| Refuser poliment | Indirection japonaise, "chotto..." |
| Signaler un bug | Euphemismes, ne pas blamer directement |

#### Systeme de Keigo

```
Teineigo (丁寧語) — Politesse de base
    です、ます、ございます
    → Utilise avec tous sauf amis proches

Sonkeigo (尊敬語) — Respect pour l'autre
    いらっしゃる、おっしゃる、ご覧になる
    → Utilise pour les clients, superieurs

Kenjougo (謙譲語) — Humilite pour soi
    参る、申す、拝見する
    → Utilise pour soi-meme face aux superieurs
```

---

### 3.6 Code in Japanese

#### Exercices de Code Review

L'apprenant doit rediger des reviews de code en japonais :

```
// Code a reviewer
public void processOrder(Order order) {
    if (order == null) return;
    // ... processing
}

// Review attendue (japonais)
「null チェックの後に早期リターンするのは良いですが、
ログを追加した方がデバッグしやすくなると思います。」
```

#### Messages de Commit en Japonais

| Prefixe | Japonais | Signification |
|---------|----------|---------------|
| feat | 機能追加 | Nouvelle fonctionnalite |
| fix | バグ修正 | Correction de bug |
| refactor | リファクタリング | Refactoring |
| docs | ドキュメント更新 | Documentation |
| test | テスト追加 | Ajout de tests |

---

## 4. Systeme de Gamification

### 4.1 Experience (XP)

| Activite | XP gagne | Bonus |
|----------|----------|-------|
| Completer une lecon | 50 XP | - |
| Quiz reussi (>80%) | 100 XP | +50 si perfect |
| Quiz echoue | 25 XP | - |
| Entretien complete | 150 XP | ×score/100 |
| Correction de texte | 75 XP | ×score moyen |
| Scenario culturel | 80 XP | ×score culturel |
| Lecture d'article | 60 XP | - |
| Code review | 90 XP | ×score |

### 4.2 Niveaux

| Niveau | XP requis | Titre |
|--------|-----------|-------|
| BEGINNER | 0 | 初心者 (Shoshinsha) |
| INTERMEDIATE | 1000 | 中級者 (Chuukyuusha) |
| ADVANCED | 5000 | 上級者 (Joukyuusha) |
| EXPERT | 15000 | 達人 (Tatsujin) |
| MASTER | 50000 | 師匠 (Shishou) |

### 4.3 Badges

| Badge | Condition | Rarete |
|-------|-----------|--------|
| 初クイズ (First Quiz) | Completer 1 quiz | COMMON |
| 連続7日 (7-Day Streak) | 7 jours consecutifs | UNCOMMON |
| 文法マスター (Grammar Master) | Score >90% en grammaire | RARE |
| 面接合格 (Interview Pass) | Reussir un entretien avance | EPIC |
| 全モジュール制覇 (All Modules) | Completer tous les modules | LEGENDARY |

### 4.4 Streak System

```
Jour 1 : Streak = 1
Jour 2 : Streak = 2 (activite faite)
Jour 3 : Streak = 3 (activite faite)
Jour 4 : Streak = 0 (pas d'activite → RESET a 2h du matin)
```

Le streak encourage la regularite quotidienne. Un job planifie (@Scheduled daily 2:00 AM) reset les streaks des utilisateurs inactifs.

---

## 5. Recommandations Personnalisees

### 5.1 Detection des Faiblesses

Le systeme analyse automatiquement les performances et identifie les zones faibles :

```
Si score_module < 60% pendant 7 jours
    → WeakArea cree avec priorite HIGH

Si score_module entre 60-75%
    → WeakArea cree avec priorite MEDIUM

Si score_module > 75% mais tendance baissiere
    → WeakArea cree avec priorite LOW
```

### 5.2 Types de Recommandations

| Type | Declencheur | Action suggeree |
|------|-------------|-----------------|
| PRACTICE_MORE | Score < 60% sur un module | "Refaites les quiz de vocabulaire N3" |
| REVIEW_BASICS | Erreurs repetitives | "Revisez les particules de base" |
| TRY_HARDER | Score stagne | "Passez au niveau superieur" |
| TAKE_QUIZ | Pas de quiz depuis 3 jours | "Testez vos connaissances" |
| READ_CONTENT | Bon score mais peu de lecture | "Lisez un article tech japonais" |

### 5.3 Selecteur de Contenu Personnalise

Le feed de contenu est personnalise par un scoring multi-dimensionnel :

```
Score_total = w1×Score_domaine + w2×Score_difficulte + w3×Score_temps + w4×Score_source

- Score_domaine : Alignement avec les preferences et le profil
- Score_difficulte : Adequation avec le niveau actuel (+1 challenge)
- Score_temps : Favorise le contenu recent
- Score_source : Preference de source (blog, doc officielle, news)
```

---

## 6. Evaluation et Feedback

### 6.1 Feedback Immediat

Chaque exercice fournit un retour immediat :
- **Quiz** : Bonne/mauvaise reponse + explication
- **Entretien** : Score par dimension + suggestions de vocabulaire
- **Correction** : Annotations positionnees avec severite et suggestion
- **Code Review** : Score technique + linguistique + corrections

### 6.2 Feedback Long Terme

- **Rapport de faiblesses** : Patterns recurrents identifies
- **Statistiques** : Tendance (UP/DOWN/STABLE), velocite d'apprentissage
- **Recommandations** : Actions concretes generees automatiquement

### 6.3 Metriques d'Apprentissage

| Metrique | Calcul | Objectif |
|----------|--------|----------|
| Consistency | Jours actifs / 30 derniers jours | > 0.7 (70%) |
| Velocity | Delta XP / Semaine | Progression constante |
| Global Score | Moyenne ponderee des modules | > 75% |
| Streak | Jours consecutifs | > 7 |

---

## 7. Integration dans un Parcours de Carriere

### 7.1 Preparation au Marche Japonais

```
┌─────────────────────────────────────────────┐
│          PARCOURS COMPLET (12 mois)          │
├─────────────────────────────────────────────┤
│                                             │
│  Mois 1-3 : FONDATIONS                     │
│  └── Hiragana/Katakana + Vocabulaire IT     │
│                                             │
│  Mois 4-6 : COMMUNICATION                  │
│  └── Emails, Standups, Code Reviews        │
│                                             │
│  Mois 7-9 : PREPARATION ENTRETIENS         │
│  └── Simulations + Correction + Culture     │
│                                             │
│  Mois 10-12 : MISE EN MARCHE               │
│  └── CV Generation + Portfolio + Recruteurs │
│                                             │
└─────────────────────────────────────────────┘
```

### 7.2 CV et Pitch Generation

La plateforme genere des documents adaptes au type d'entreprise visee :

| Type d'entreprise | Ton | Specificites |
|-------------------|-----|--------------|
| Startup japonaise | Moderne, agile | Innovation, flexibilite, impact |
| Grande entreprise (大企業) | Tres formel, keigo | Stabilite, equipe, long terme |
| Entreprise etrangere au Japon | Bilingue | Pont culturel, adaptabilite |
| Entreprise traditionnelle | Keigo maximal | Harmonie, dedication, humilite |

### 7.3 Portfolio Public

L'apprenant peut activer un profil public qui affiche :
- Niveau atteint et badges
- Competences techniques
- Score de japonais par domaine
- Statut "Open to Work"

Les recruteurs peuvent rechercher par niveau, competence et disponibilite.

---

## 8. Methodologie d'Enseignement du Japonais Technique

### 8.1 Les 4 Piliers

```
     ┌──────────────┐
     │   LECTURE    │  Articles, documentation, code comments
     └──────┬───────┘
            │
     ┌──────┴───────┐
     │  ECRITURE    │  Emails, PR, commits, reports
     └──────┬───────┘
            │
     ┌──────┴───────┐
     │ COMPREHENSION│  Entretiens, reunions, instructions
     └──────┬───────┘
            │
     ┌──────┴───────┐
     │ PRODUCTION   │  Presentations, self-intro, discussions
     └──────────────┘
```

### 8.2 Japonais Specifique IT vs Japonais General

| Aspect | Japonais general | Japonais IT (NihongoDev) |
|--------|-----------------|--------------------------|
| Vocabulaire | Vie quotidienne | Termes techniques, katakana IT |
| Grammaire | Conversations | Redaction technique, specifications |
| Keigo | Situations sociales | Relations professionnelles hierarchiques |
| Lecture | Journaux, livres | Documentation, articles tech, logs |
| Ecriture | Lettres, essais | Emails, tickets JIRA, PR descriptions |

### 8.3 Progression par Competence

```
Lire du code commente en japonais        → Niveau N4
Ecrire un message Slack en japonais       → Niveau N3
Rediger un email professionnel            → Niveau N3
Faire une presentation technique          → Niveau N2
Passer un entretien technique en japonais → Niveau N2
Negocier un contrat en japonais           → Niveau N1
Mentorer en japonais                      → Niveau N1
```

---

## 9. Accessibilite et Inclusion

### 9.1 Adaptation aux Differents Profils

- **Multilingue** : Interface EN/FR, contenu d'apprentissage JP avec traductions
- **Rythme flexible** : Pas de deadline, progression a son rythme
- **Multi-device** : API REST compatible web et mobile
- **Offline-friendly** : Vocabulaire consultable hors-ligne (futur)

### 9.2 Motivation et Retention

| Mecanisme | Objectif |
|-----------|----------|
| Streaks | Regularite quotidienne |
| Badges | Sentiment d'accomplissement |
| Leaderboard | Competition saine |
| Notifications | Rappels d'inactivite |
| Recommandations | Guidance personnalisee |
| Portfolio | Objectif concret (emploi) |

---

## 10. Interface Utilisateur et Experience d'Apprentissage

### 10.1 Design Pedagogique de l'Interface

L'interface a ete concue pour soutenir l'apprentissage :

| Principe UX | Application pedagogique |
|-------------|------------------------|
| Salutation en japonais | Immersion des l'ouverture (おはようございます) |
| Caracteres japonais dans la navigation | Familiarisation passive avec les kana |
| Modules visuels (あ, カ, 日, IT, +) | Association visuelle avec le contenu |
| Cercle de progression | Objectif tangible, motivation par la completion |
| Streak visible en permanence | Renforcement de l'habitude quotidienne |
| Calendrier d'activite | Visualisation de la regularite |
| Recommandations personnalisees | Orientation pedagogique sans effort |

### 10.2 Parcours Utilisateur dans l'Interface

```
Login → Dashboard (vue d'ensemble)
           │
           ├── Modules rapides (Hiragana, Katakana, Kanji, IT, Extension)
           │       → Acces direct au contenu d'apprentissage
           │
           ├── Statistiques (Lecons, XP, Score)
           │       → Feedback immediat sur la progression
           │
           ├── Activite recente
           │       → Rappel de ce qui a ete fait
           │
           ├── Recommandations
           │       → Guidance vers la prochaine etape
           │
           └── Panneau droit
                   ├── Progression circulaire → Vue globale
                   ├── Calendrier → Regularite
                   ├── Streak → Motivation
                   └── Notifications → Recompenses et rappels
```

### 10.3 Feedback Visuel et Gamification dans l'UI

| Element visuel | Role pedagogique |
|---------------|-----------------|
| Skeletons de chargement | Anticiper le contenu, reduire l'impatience |
| Couleurs par module (rouge=grammaire, or=vocabulaire, vert=kanji, bleu=culture) | Categorisation visuelle facilitant la memorisation |
| Animation hover sur les modules | Interactivite, encouragement au clic |
| Badge de notification animé | Sentiment d'accomplissement et d'avancement |
| Score en pourcentage | Objectif quantifiable |
| Flamme du streak 🔥 | Gamification emotionnelle |

### 10.4 Accessibilite de l'Apprentissage

- **Navigation claire** : Sidebar avec 8 sections bien identifiees par icones
- **Hiérarchie visuelle** : Titres en japonais + sous-titres en francais
- **Etats vides** : Messages d'encouragement quand aucune activite ("Aucune activite recente")
- **Responsive** : Adaptation mobile pour apprendre en deplacement
- **Charge cognitive reduite** : Une seule page visible a la fois, navigation laterale persistante
