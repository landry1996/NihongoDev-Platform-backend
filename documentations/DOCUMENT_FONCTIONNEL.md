# NihongoDev Platform — Document Fonctionnel

## 1. Presentation du Projet

### 1.1 Vision

NihongoDev est une plateforme d'apprentissage du japonais specialisee pour les developpeurs IT souhaitant travailler au Japon. Elle combine l'enseignement linguistique avec le vocabulaire technique informatique, les codes culturels professionnels japonais et la preparation aux entretiens d'embauche.

### 1.2 Public Cible

| Persona | Description | Objectif |
|---------|-------------|----------|
| Developpeur junior | 0-3 ans XP, debutant en japonais | Apprendre les bases + vocabulaire IT |
| Developpeur senior | 5+ ans XP, niveau intermediaire | Preparer les entretiens techniques en japonais |
| Freelance international | Veut decrocher des missions au Japon | CV/Pitch adapte au marche japonais |
| Reconversion | Changement de pays/carriere | Immersion culturelle + technique |

### 1.3 Proposition de Valeur

- Vocabulaire IT japonais specialise (pas de japonais generique)
- Simulation d'entretiens techniques en japonais
- Correction intelligente adaptee au contexte professionnel
- Generation de CV/Pitch adaptes aux entreprises japonaises (IA)
- Systeme de progression gamifie avec badges et streaks

---

## 2. Modules Fonctionnels

### 2.1 Authentification & Gestion des Utilisateurs

**Fonctionnalites :**
- Inscription avec choix du niveau de japonais et objectif
- Connexion avec JWT + refresh token
- Gestion du profil (nom, niveau, objectif, avatar)
- Changement de mot de passe
- Roles : STUDENT, TEACHER, ADMIN

**Regles metier :**
- Un email = un seul compte
- Le refresh token est invalide apres chaque utilisation (rotation)
- 5 tentatives de connexion echouees = blocage temporaire (10 min)
- Mots de passe : minimum 8 caracteres

---

### 2.2 Module Lessons (Apprentissage)

**Fonctionnalites :**
- Lecons par type : HIRAGANA, KATAKANA, KANJI, GRAMMAR, VOCABULARY, CULTURE
- Niveaux : N5, N4, N3, N2, N1 (alignes sur le JLPT)
- Consultation publique (sans authentification)
- Creation/modification reservee aux TEACHER et ADMIN
- Marquage de completion (declenche un event Kafka)

**Regles metier :**
- Une lecon doit etre publiee pour etre visible publiquement
- La completion d'une lecon donne des XP de base

---

### 2.3 Module Vocabulaire IT Japonais

**Fonctionnalites :**
- Vocabulaire categorise : PROGRAMMING, DATABASE, NETWORK, SECURITY, AGILE, DEVOPS, CLOUD, GENERAL
- Systeme SRS (Spaced Repetition System) avec algorithme SM-2
- Import en batch de vocabulaire
- Recherche full-text avec filtres
- Relations entre mots (synonymes, antonymes, derives, composes)
- Generation de quiz adaptatifs

**Regles metier :**
- Le niveau de maitrise evolue : NEW → LEARNING → REVIEWING → MASTERED
- Les revisions sont espacees selon la performance (SM-2)
- Les quiz peuvent etre FR→JP, JP→FR, EN→JP ou contextuel

---

### 2.4 Module Quiz (Multi-mode, Adaptatif)

**Fonctionnalites :**
- 5 modes de quiz : CLASSIC, TIMED, SURVIVAL, SPRINT, REVIEW
- 4 types de questions : QCM, Vrai/Faux, Saisie texte, Matching
- Systeme de streak (series de bonnes reponses)
- Score dynamique (bonus temps + multiplicateur streak + difficulte)
- Historique des tentatives et resultats

**Regles metier :**
- En mode SURVIVAL : 3 vies, game over a 0
- En mode TIMED : limite de temps par question
- Le score est calcule avec : base + bonus temps + streak multiplier
- Un quiz est declare reussi si score >= seuil de passage

---

### 2.5 Module Simulation d'Entretien

**Fonctionnalites :**
- Types d'entretien : HR_JAPANESE, TECH_JAVA, TECH_SPRING, TECH_AWS, BEHAVIORAL, SELF_INTRODUCTION, BUSINESS_JAPANESE
- 3 niveaux de difficulte : BEGINNER, INTERMEDIATE, ADVANCED
- Evaluation multi-dimensionnelle (technique, langue, culture, structure)
- Feedback detaille avec suggestions de vocabulaire
- Detection du keigo et de la structure STAR

**Regles metier :**
- Les questions sont tirees d'une banque et melangees aleatoirement
- L'evaluation utilise 3 strategies : mots-cles, reference, structure
- Le score final determine le pass/fail
- Les phases progressent : INTRODUCTION → MAIN_QUESTIONS → FOLLOW_UP → CLOSING

---

### 2.6 Module Correction Intelligente

**Fonctionnalites :**
- 7 contextes de correction : EMAIL_TO_CLIENT, STANDUP_REPORT, CODE_REVIEW, INTERVIEW, SLACK, COMMIT, GENERAL
- Pipeline de correction en 6 etapes : Grammaire → Vocabulaire → Politesse → Clarte → Naturalite → Professionnalisme
- Tracking des faiblesses recurrentes
- Rapport de faiblesse agrege

**Regles metier :**
- La severite des annotations : INFO, WARNING, ERROR, CRITICAL
- Le niveau de politesse attendu depend du contexte (email client = strict)
- Les patterns recurrents (3+ occurrences) sont marques comme "faiblesse"
- Score sur 6 dimensions avec moyenne ponderee

---

### 2.7 Module Progress & Analytics

**Fonctionnalites :**
- Systeme XP avec niveaux (BEGINNER → INTERMEDIATE → ADVANCED → EXPERT → MASTER)
- Streak journalier
- Score global pondere par module
- Statistiques 7j / 30j / all-time
- Tableau de bord admin (analytics plateforme)
- Leaderboard top users

**Regles metier :**
- XP gagne varie par activite et performance
- Le niveau monte a des seuils fixes d'XP
- Le streak se reset si aucune activite pendant 24h
- Les statistiques sont recalculees toutes les 15 minutes

---

### 2.8 Module CV Generator

**Fonctionnalites :**
- Profil CV : nom, role actuel/cible, stack technique, experiences, certifications
- 4 types de pitch : ENGLISH_PITCH, JAPANESE_PITCH, INTERVIEW_PRESENTATION, DEVELOPER_SUMMARY
- 4 types d'entreprise cible : STARTUP, ENTERPRISE, FOREIGN_IN_JAPAN, TRADITIONAL_JAPANESE
- Generation basee sur regles (pipeline de sections)
- Generation par IA (Claude API) avec prompts adaptes
- Export en Markdown ou texte brut

**Regles metier :**
- Un seul profil CV par utilisateur
- Le ton s'adapte au type d'entreprise (startup = moderne, traditionnelle = keigo formel)
- Le LLM recoit le profil complet et des instructions additionnelles optionnelles
- Chaque generation est sauvegardee avec versionnement

---

### 2.9 Module Cultural Intelligence

**Fonctionnalites :**
- Scenarios culturels (email au client, reunion, standup, apres-travail)
- Validation du keigo (niveaux de politesse)
- Score culturel multi-dimensionnel
- Progression par categorie culturelle

**Regles metier :**
- Chaque scenario a des choix avec un score culturel associe
- Le keigo est evalue sur 3 niveaux : teineigo, sonkeigo, kenjougo
- Les violations culturelles sont classees par gravite

---

### 2.10 Module Code in Japanese

**Fonctionnalites :**
- Exercices de code review en japonais
- Exercices de redaction de PR en japonais
- Validation de messages de commit (prefixes japonais)
- Templates de PR par contexte

**Regles metier :**
- Les commit messages suivent les conventions japonaises
- Les PR reviews utilisent le vocabulaire technique standard
- Score base sur la justesse technique ET linguistique

---

### 2.11 Module Real Content Engine

**Fonctionnalites :**
- Ingestion d'articles techniques japonais reels
- Pipeline d'annotation (vocabulaire, grammaire, kanji, termes techniques)
- Selecteur de contenu personnalise (scoring multi-dimensionnel)
- Sessions de lecture avec suivi

**Regles metier :**
- Le contenu est filtre par domaine, difficulte et preferences utilisateur
- Les annotations enrichissent le texte avec des furigana et traductions
- La completion d'une session de lecture donne des XP

---

### 2.12 Module Portfolio & Recruteur

**Fonctionnalites :**
- Profil public avec visibilite configurable (PUBLIC, RECRUITERS_ONLY, PRIVATE)
- Systeme de badges (16 badges, 5 categories, 4 rarites)
- Showcase de badges (max 6)
- Recherche de profils par les recruteurs (filtre par niveau, competence, openToWork)
- Toggle "Open to Work"

**Regles metier :**
- Un badge est attribue une seule fois par utilisateur
- Le slug de profil public est unique
- Les recruteurs ne voient que les profils PUBLIC ou RECRUITERS_ONLY
- Maximum 6 badges en showcase

---

### 2.13 Module Notifications

**Fonctionnalites :**
- Notifications in-app et par email
- 9 types : WELCOME, BADGE_EARNED, LEVEL_UP, STREAK_MILESTONE, QUIZ_COMPLETED, INTERVIEW_COMPLETED, PITCH_GENERATED, WEEKLY_SUMMARY, INACTIVITY_REMINDER
- Marquer comme lu (unitaire et batch)
- Compteur de non-lus
- Declenchement automatique via events Kafka

**Regles metier :**
- Les notifications WELCOME et BADGE_EARNED sont envoyees par email + in-app
- Les notifications de completion sont in-app uniquement
- L'envoi d'email est asynchrone et tolerant aux erreurs
- Un utilisateur ne peut marquer comme lu que ses propres notifications

---

## 3. Parcours Utilisateur Type

```
1. Inscription → Notification WELCOME (email + in-app)
2. Premier quiz → Badge "初クイズ" + XP
3. Streak 7 jours → Badge STREAK_MASTER
4. Niveau INTERMEDIATE atteint → Notification LEVEL_UP
5. Simulation d'entretien → Feedback detaille
6. Creation profil CV → Generation pitch IA
7. Activation "Open to Work" → Visible par les recruteurs
```

---

## 4. Roles et Permissions

| Action | STUDENT | TEACHER | ADMIN |
|--------|---------|---------|-------|
| Consulter lecons publiques | Oui | Oui | Oui |
| Faire des quiz | Oui | Oui | Oui |
| Creer/modifier lecons | Non | Oui | Oui |
| Voir analytics plateforme | Non | Non | Oui |
| Gerer les utilisateurs | Non | Non | Oui |

---

## 5. Evenements Metier (Event-Driven)

| Evenement | Declencheur | Consommateurs |
|-----------|-------------|---------------|
| UserRegisteredEvent | Inscription | Notification (welcome email) |
| LessonCompletedEvent | Completion lecon | Progress (XP) |
| QuizCompletedEvent | Fin de quiz | Progress (XP) + Notification |
| InterviewCompletedEvent | Fin entretien | Progress + Notification |
| TextCorrectedEvent | Correction | Progress (XP) |
| BadgeEarnedEvent | Attribution badge | Notification (email) |
| ProgressUpdatedEvent | Mise a jour XP | Notification (level-up) |
| PitchGeneratedEvent | Generation CV | Notification |
| ScenarioCompletedEvent | Fin scenario | Progress (XP) |
| ContentReadCompletedEvent | Fin lecture | Progress (XP) |

---

## 6. KPIs et Metriques

| Metrique | Description |
|----------|-------------|
| DAU / MAU | Utilisateurs actifs quotidiens/mensuels |
| Taux de completion | % de lecons/quiz termines |
| Streak moyen | Nombre moyen de jours consecutifs |
| Score moyen par module | Performance par domaine |
| Taux de conversion | Profils "Open to Work" / inscrits |
| Temps moyen par session | Engagement |
