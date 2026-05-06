# NihongoDev Platform — Backend

Japanese learning platform for IT developers preparing to work in Japan.

## Architecture

- **Java 21** + **Spring Boot 3.3**
- **Hexagonal Architecture** (Ports & Adapters)
- **Event-Driven** with Apache Kafka
- **PostgreSQL** + **Redis** + **Flyway**
- **JWT** authentication with refresh tokens
- **MapStruct** for object mapping
- **ArchUnit** for architecture enforcement

## Project Structure

```
src/main/java/com/nihongodev/platform/
├── domain/          # Business logic, models, events, exceptions
├── application/     # Use cases, ports (in/out), DTOs, commands
├── infrastructure/  # Controllers, persistence, security, kafka, AI
└── config/          # Cross-cutting configuration
```

## Prerequisites

- Java 21
- Maven 3.9+
- Docker & Docker Compose

## Quick Start

```bash
# Start infrastructure (PostgreSQL, Kafka, Redis)
docker-compose up -d

# Run the application
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev,secret

# Run tests
./mvnw test
```

## API Documentation

Once running, access Swagger UI at: http://localhost:8080/swagger-ui.html

## Configuration

| Profile | Purpose |
|---------|---------|
| `dev` | Local development |
| `test` | Integration tests (Testcontainers) |
| `secret` | Secrets (JWT key, API keys) — never committed |

Copy `application-secret.example.yml` to `application-secret.yml` and fill in real values.

## Modules

| Module | Description |
|--------|-------------|
| Auth | Registration, login, JWT, refresh tokens |
| User | Profile management, roles |
| Learning | Lessons (Hiragana, Katakana, Kanji, Grammar) |
| Vocabulary | IT Japanese vocabulary with categories |
| Quiz | Quizzes with scoring |
| Interview | Technical & HR interview simulation |
| Correction | AI-powered answer correction |
| Progress | User progress tracking |
| CV Generator | Japanese CV generation |
| Shadow Day | Immersive work day simulation |
| Cultural Intelligence | Keigo & business culture training |
| Code Japanese | Code review in Japanese |
| Real Content | Learning from real Japanese tech articles |
| Portfolio | Public profile for recruiters |
| Notification | Email & push notifications |
