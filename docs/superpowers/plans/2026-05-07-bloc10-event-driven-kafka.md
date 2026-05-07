# BLOC 10 — Event-Driven Architecture Kafka (Refactoring & Hardening)

## Objectif

Refactoring et renforcement de l'architecture événementielle Kafka existante.
Pas de nouvelles fonctionnalités business — uniquement hardening de l'infrastructure.

## Scope

### 1. DomainEvent Interface
- Interface commune pour tous les événements du domaine
- Champs: `eventId()`, `eventType()`, `userId()`, `occurredAt()`
- Tous les records événements implémentent cette interface

### 2. Refactoring des 11 événements
| Événement | eventType |
|-----------|-----------|
| UserRegisteredEvent | USER_REGISTERED |
| LessonCompletedEvent | LESSON_COMPLETED |
| QuizCompletedEvent | QUIZ_COMPLETED |
| InterviewStartedEvent | INTERVIEW_STARTED |
| InterviewAnswerEvaluatedEvent | INTERVIEW_ANSWER_EVALUATED |
| InterviewCompletedEvent | INTERVIEW_COMPLETED |
| TextCorrectedEvent | TEXT_CORRECTED |
| VocabularyQuizGeneratedEvent | VOCABULARY_QUIZ_GENERATED |
| ProgressUpdatedEvent | PROGRESS_UPDATED |
| PitchGeneratedEvent | PITCH_GENERATED |
| NotificationRequestedEvent | NOTIFICATION_REQUESTED (inert) |

### 3. KafkaEventPublisherAdapter (enhanced)
- Keying par `eventId` (UUID) pour déduplication naturelle
- Pattern matching `event instanceof DomainEvent`
- Async callback avec logging structuré (topic, eventId, offset)
- Fire-and-forget publishing

### 4. KafkaConsumerConfig (new)
- `DefaultErrorHandler` + `DeadLetterPublishingRecoverer`
- Topic DLT: `dead-letter-events`
- Exponential backoff: 1s, 2s, 4s (maxElapsedTime 7000ms — 3 retries)
- Non-retryable: `IllegalArgumentException`, `NullPointerException` → DLT direct

### 5. ProgressEventConsumer (simplified)
- Suppression de tous les try/catch (DefaultErrorHandler gère les failures)
- Validation: `eventId` null ou `userId` null → IllegalArgumentException → DLT
- Logging structuré: `[eventId={}, type=X, userId={}]`

### 6. Configuration
- `application.yml`: ajout `dead-letter-events` (partitions: 1, replicas: 1)
- `spring.json.add.type.headers: true` (producer)
- `KafkaTopicsProperties`: ajout `deadLetterEvents`
- `KafkaConfig`: ajout beans `cvGeneratorEventsTopic()`, `deadLetterEventsTopic()`, `correctionEventsTopic()`

### 7. Tests
- `KafkaEventPublisherAdapterTest`: send to resolved topic, extract eventId, fallback
- `ProgressEventConsumerTest`: delegates events, throws on null eventId/userId
- `KafkaConsumerConfigTest`: error handler creation
- `KafkaIntegrationTest` (@EmbeddedKafka): publish → consume, DLT routing

## Contraintes
- Spring Boot 3.3.5, Java 21, Kafka 3.7.x
- Hexagonal architecture respectée
- Aucun nouveau consumer, aucune nouvelle feature business
- `spring-kafka-test` ajouté pour tests d'intégration

## Status: DONE
