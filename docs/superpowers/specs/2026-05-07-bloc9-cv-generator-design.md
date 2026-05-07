# BLOC 9 — CV Generator Design Spec

## Goal

Create a module that helps developers prepare their CV, pitches, and self-introductions for the Japanese job market. Rule-based generation with composable templates, full version history, and markdown export.

## Architecture

Hexagonal architecture with a **pipeline-composable pattern** (same as BLOC 7 CorrectionPipeline). A `PitchAssembler` composes ordered `PitchSection` implementations per `PitchType`. Each section adapts its output based on the user's `CvProfile` and `TargetCompanyType`. Content is stored as structured Markdown for easy export and future PDF conversion.

## Tech Stack

- Java 21 (switch expressions, records)
- Spring Boot 3.3.5
- PostgreSQL (JSONB for structured lists)
- Kafka (PitchGeneratedEvent)
- Flyway V8 migration

---

## Domain Layer

### Enums

| Enum | Values |
|------|--------|
| `PitchType` | `ENGLISH_PITCH`, `JAPANESE_PITCH`, `INTERVIEW_PRESENTATION`, `DEVELOPER_SUMMARY` |
| `TargetCompanyType` | `STARTUP`, `ENTERPRISE`, `FOREIGN_IN_JAPAN`, `TRADITIONAL_JAPANESE` |
| `ExportFormat` | `MARKDOWN`, `PLAIN_TEXT` |

### CvProfile (aggregate)

| Field | Type | Description |
|-------|------|-------------|
| `id` | UUID | Primary key |
| `userId` | UUID | Owner (unique constraint — one profile per user) |
| `fullName` | String | Display name |
| `currentRole` | String | Current job title (optional) |
| `targetRole` | String | Desired position in Japan |
| `yearsOfExperience` | int | Total years |
| `targetCompanyType` | TargetCompanyType | Affects tone of generation |
| `techStack` | List\<String\> | Languages, frameworks, tools |
| `experiences` | List\<WorkExperience\> | Professional history |
| `certifications` | List\<String\> | JLPT, AWS, etc. |
| `notableProjects` | List\<String\> | 1-3 short descriptions |
| `motivationJapan` | String | Why Japan (free text) |
| `japaneseLevel` | String | Retrieved from existing User model |
| `createdAt` | LocalDateTime | |
| `updatedAt` | LocalDateTime | |

Factory method: `CvProfile.create(userId, fullName, targetRole, yearsOfExperience, targetCompanyType)`

### WorkExperience (value object)

| Field | Type |
|-------|------|
| `company` | String |
| `role` | String |
| `durationMonths` | int |
| `highlights` | List\<String\> (1-3 achievements) |

### GeneratedPitch (entity)

| Field | Type | Description |
|-------|------|-------------|
| `id` | UUID | |
| `userId` | UUID | Owner |
| `pitchType` | PitchType | Type of content generated |
| `content` | String | Full Markdown content |
| `profileSnapshotId` | UUID | CvProfile.id at generation time |
| `generatedAt` | LocalDateTime | |

### Domain Event

```java
public record PitchGeneratedEvent(
    UUID userId,
    UUID pitchId,
    String pitchType,
    LocalDateTime generatedAt
) {
    public static PitchGeneratedEvent of(UUID userId, UUID pitchId, PitchType type) {
        return new PitchGeneratedEvent(userId, pitchId, type.name(), LocalDateTime.now());
    }
}
```

---

## Application Layer

### Ports IN

| Port | Signature |
|------|-----------|
| `CreateCvProfilePort` | `CvProfileDto create(UUID userId, CreateCvProfileCommand cmd)` |
| `UpdateCvProfilePort` | `CvProfileDto update(UUID userId, UpdateCvProfileCommand cmd)` |
| `GetCvProfilePort` | `CvProfileDto get(UUID userId)` |
| `GeneratePitchPort` | `GeneratedPitchDto generate(UUID userId, GeneratePitchCommand cmd)` |
| `GetPitchHistoryPort` | `List<GeneratedPitchDto> getHistory(UUID userId, PitchType type)` |
| `GetLatestPitchPort` | `GeneratedPitchDto getLatest(UUID userId, PitchType type)` |
| `ExportPitchPort` | `String export(UUID userId, UUID pitchId, ExportFormat format)` |

### Ports OUT

| Port | Methods |
|------|---------|
| `CvProfileRepositoryPort` | `save(CvProfile)`, `findByUserId(UUID)`, `existsByUserId(UUID)` |
| `GeneratedPitchRepositoryPort` | `save(GeneratedPitch)`, `findById(UUID)`, `findByUserIdAndPitchType(UUID, PitchType)`, `findLatestByUserIdAndPitchType(UUID, PitchType)` |
| `EventPublisherPort` | (existing) `publish(String topic, Object event)` |

### Use Cases

| Use Case | Implements | Flow |
|----------|-----------|------|
| `CreateCvProfileUseCase` | `CreateCvProfilePort` | Validate no existing profile -> create CvProfile -> save -> map to DTO |
| `UpdateCvProfileUseCase` | `UpdateCvProfilePort` | Load profile -> merge non-null fields -> save -> map to DTO |
| `GetCvProfileUseCase` | `GetCvProfilePort` | Load -> map to DTO (throw NotFound if absent) |
| `GeneratePitchUseCase` | `GeneratePitchPort` | Load profile -> PitchAssembler.assemble(profile, type) -> save GeneratedPitch -> publish event -> map to DTO |
| `GetPitchHistoryUseCase` | `GetPitchHistoryPort` + `GetLatestPitchPort` | Query by userId + type, ordered by generatedAt DESC |
| `ExportPitchUseCase` | `ExportPitchPort` | Load pitch -> verify ownership -> return content as-is (MD) or strip markdown (PLAIN_TEXT) |

### DTOs (records)

- `CvProfileDto` — full profile data
- `WorkExperienceDto` — value object mirror
- `GeneratedPitchDto` — id, pitchType, content, generatedAt
- `CreateCvProfileCommand` — all required profile fields
- `UpdateCvProfileCommand` — all fields nullable (null = no change)
- `GeneratePitchCommand` — pitchType only

---

## Pitch Generation Engine

### Pattern: Pipeline composable

```
GeneratePitchUseCase
  -> PitchAssembler.assemble(profile, pitchType)
    -> resolves List<PitchSection> for this type
    -> each section.render(profile) -> String (markdown fragment)
    -> join fragments with "\n\n" -> complete Markdown
  -> save GeneratedPitch
  -> publish PitchGeneratedEvent
```

### PitchSection interface

```java
public interface PitchSection {
    String render(CvProfile profile);
    int order();
}
```

### Sections

| Section Class | Used By | Description |
|---------------|---------|-------------|
| `IntroSectionEN` | ENGLISH_PITCH, DEVELOPER_SUMMARY | "Hi, I'm X, a Y with Z years..." |
| `IntroSectionJP` | JAPANESE_PITCH | Jikoshoukai in simple Japanese |
| `ExperienceSectionEN` | ENGLISH_PITCH, DEVELOPER_SUMMARY | Work history summary |
| `ExperienceSectionJP` | JAPANESE_PITCH | Work history in simple Japanese |
| `TechStackSection` | ENGLISH_PITCH, JAPANESE_PITCH, DEVELOPER_SUMMARY | Formatted tech list |
| `MotivationSectionEN` | ENGLISH_PITCH, INTERVIEW_PRESENTATION | Why Japan motivation |
| `MotivationSectionJP` | JAPANESE_PITCH | Shibou douki in Japanese |
| `CertificationsSection` | All types | JLPT, AWS, certifications |
| `ClosingSectionEN` | ENGLISH_PITCH, INTERVIEW_PRESENTATION | Call to action |
| `ClosingSectionJP` | JAPANESE_PITCH | Yoroshiku onegaishimasu closing |
| `InterviewOpeningSection` | INTERVIEW_PRESENTATION | Structured interview intro |
| `ProjectHighlightsSection` | DEVELOPER_SUMMARY, INTERVIEW_PRESENTATION | Notable projects |

### TargetCompanyType adaptation

Each section with tone variation uses Java 21 switch expressions internally:

```java
String tone = switch (profile.getTargetCompanyType()) {
    case STARTUP -> "innovative and dynamic";
    case ENTERPRISE -> "scalable and reliable";
    case FOREIGN_IN_JAPAN -> "adaptable and bilingual";
    case TRADITIONAL_JAPANESE -> "respectful and process-oriented";
};
```

### PitchAssembler

```java
@Service
public class PitchAssembler {
    private final Map<PitchType, List<PitchSection>> registry;

    public String assemble(CvProfile profile, PitchType type) {
        return registry.get(type).stream()
            .sorted(Comparator.comparingInt(PitchSection::order))
            .map(section -> section.render(profile))
            .collect(Collectors.joining("\n\n"));
    }
}
```

### Registry mapping

| PitchType | Sections (in order) |
|-----------|-------------------|
| `ENGLISH_PITCH` | IntroSectionEN, ExperienceSectionEN, TechStackSection, MotivationSectionEN, CertificationsSection, ClosingSectionEN |
| `JAPANESE_PITCH` | IntroSectionJP, ExperienceSectionJP, TechStackSection, MotivationSectionJP, CertificationsSection, ClosingSectionJP |
| `INTERVIEW_PRESENTATION` | InterviewOpeningSection, IntroSectionEN, ExperienceSectionEN, MotivationSectionEN, ProjectHighlightsSection, CertificationsSection, ClosingSectionEN |
| `DEVELOPER_SUMMARY` | IntroSectionEN, ExperienceSectionEN, TechStackSection, ProjectHighlightsSection, CertificationsSection |

---

## Infrastructure Layer

### Flyway Migration V8

```sql
CREATE TABLE cv_profiles (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE REFERENCES users(id),
    full_name VARCHAR(200) NOT NULL,
    current_role VARCHAR(200),
    target_role VARCHAR(200) NOT NULL,
    years_of_experience INT NOT NULL DEFAULT 0,
    target_company_type VARCHAR(50) NOT NULL,
    tech_stack JSONB NOT NULL DEFAULT '[]',
    experiences JSONB NOT NULL DEFAULT '[]',
    certifications JSONB NOT NULL DEFAULT '[]',
    notable_projects JSONB NOT NULL DEFAULT '[]',
    motivation_japan TEXT,
    japanese_level VARCHAR(10),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE generated_pitches (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    pitch_type VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    profile_snapshot_id UUID REFERENCES cv_profiles(id),
    generated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_generated_pitches_user_type ON generated_pitches(user_id, pitch_type);
CREATE INDEX idx_generated_pitches_user_date ON generated_pitches(user_id, generated_at DESC);
```

### JPA Entities

- `CvProfileEntity` — `@JdbcTypeCode(SqlTypes.JSON)` for JSONB fields, `@PrePersist/@PreUpdate` for timestamps, unique constraint on user_id
- `GeneratedPitchEntity` — simple mapping, `@Enumerated(EnumType.STRING)` for pitchType

### Persistence Mappers

- `CvProfilePersistenceMapper` — uses ObjectMapper for JSONB serialization of experiences, techStack, certifications, notableProjects (same pattern as `UserStatisticsPersistenceMapper` from BLOC 8)
- `GeneratedPitchPersistenceMapper` — direct field mapping

### Repository Adapters

- `CvProfileRepositoryAdapter` implements `CvProfileRepositoryPort`
- `GeneratedPitchRepositoryAdapter` implements `GeneratedPitchRepositoryPort`

### Kafka

- Add `cvGeneratorEvents` TopicDef to `KafkaTopicsProperties`
- Add `"cv-generator-events"` to `KafkaEventPublisherAdapter` registry
- Topic config: partitions 3, replicas 1

### REST Controller

```
@RestController
@RequestMapping("/api/cv")
@Tag(name = "CV Generator")
```

| Endpoint | Method | Description |
|----------|--------|-------------|
| `POST /api/cv/profile` | createProfile | Create CV profile |
| `PUT /api/cv/profile` | updateProfile | Update profile |
| `GET /api/cv/profile` | getProfile | Get own profile |
| `POST /api/cv/generate` | generatePitch | Generate a pitch (body: {pitchType}) |
| `GET /api/cv/pitches?type=X` | getHistory | History by type |
| `GET /api/cv/pitches/latest?type=X` | getLatest | Latest version of a type |
| `GET /api/cv/pitches/{id}/export?format=X` | exportPitch | Export as MD or plain text |

All endpoints require authentication via `@AuthenticationPrincipal AuthenticatedUser`.

---

## Security

### Access Control

- All endpoints behind `@AuthenticationPrincipal`
- User ID extracted from JWT token — never from path param
- A user can only access their own profile and pitches
- No admin endpoints for this module
- Pitch ownership verified in ExportPitchUseCase before returning content

### Validation

| Field | Constraint |
|-------|-----------|
| `fullName` | `@NotBlank @Size(max = 200)` |
| `targetRole` | `@NotBlank @Size(max = 200)` |
| `yearsOfExperience` | `@Min(0) @Max(50)` |
| `targetCompanyType` | `@NotNull` |
| `techStack` | `@Size(max = 20)` elements, each `@Size(max = 50)` |
| `experiences` | `@Size(max = 10)` entries |
| `certifications` | `@Size(max = 15)` elements |
| `notableProjects` | `@Size(max = 5)` elements |
| `motivationJapan` | `@Size(max = 2000)` |
| `pitchType` | `@NotNull` |

### Data Protection

- No logging of pitch content or profile data (PII)
- CvProfile contains professional info only — no address, phone, email (those live in User)
- Export responses use `Content-Type: text/plain` or `text/markdown`

---

## Tests

### Domain Tests

| Test Class | Tests |
|-----------|-------|
| `CvProfileTest` | create via factory, add experience, validation |
| `WorkExperienceTest` | creation, durationMonths > 0 |

### Pitch Engine Tests

| Test Class | Tests |
|-----------|-------|
| `IntroSectionENTest` | render full profile, adaptation per TargetCompanyType (4 variants) |
| `IntroSectionJPTest` | render jikoshoukai, formal/informal adaptation |
| `ExperienceSectionENTest` | render single experience, render multiple, highlights formatted |
| `TechStackSectionTest` | render list, handle empty list |
| `MotivationSectionENTest` | render with motivation, adaptation per company type |
| `PitchAssemblerTest` | assemble ENGLISH_PITCH complete, assemble JAPANESE_PITCH, section order respected |

### Use Case Tests

| Test Class | Tests |
|-----------|-------|
| `CreateCvProfileUseCaseTest` | creates profile, fails if profile exists |
| `UpdateCvProfileUseCaseTest` | updates fields, ignores null fields |
| `GeneratePitchUseCaseTest` | generates pitch, saves, publishes event, fails if no profile |
| `GetPitchHistoryUseCaseTest` | returns history, returns latest, returns empty |
| `ExportPitchUseCaseTest` | export MARKDOWN returns as-is, export PLAIN_TEXT strips markdown, fails if not owner |

### Infrastructure Tests

| Test Class | Tests |
|-----------|-------|
| `CvProfilePersistenceMapperTest` | domain -> entity -> domain round-trip, JSONB serialization |

### Total: ~25-30 tests

---

## Export Strategy

### Markdown (default)

Content stored and returned as-is. Sections use `## Heading` format.

### Plain Text

Strip markdown syntax:
- Remove `#` heading prefixes
- Remove `**bold**` markers
- Remove `- ` list prefixes (keep content)
- Preserve line breaks

Implementation: simple regex replacements in `ExportPitchUseCase`.

### PDF (future — not in this bloc)

Design allows future addition:
- Parse Markdown sections
- Use a library (OpenPDF / flexmark-java) to render
- Add `PDF` to `ExportFormat` enum
- Add a `PdfExportAdapter` implementing a new port

---

## Kafka Integration

- Topic: `cv-generator-events`
- Event: `PitchGeneratedEvent` published after each successful generation
- Not consumed by any existing module in this bloc — the event is published for future use (e.g., BLOC 8 could add a CV_GENERATED ActivityType later). No consumer implementation needed in BLOC 9.

---

## File Structure Summary

```
domain/model/
  CvProfile.java
  WorkExperience.java
  GeneratedPitch.java
  PitchType.java
  TargetCompanyType.java
  ExportFormat.java

domain/event/
  PitchGeneratedEvent.java

application/port/in/
  CreateCvProfilePort.java
  UpdateCvProfilePort.java
  GetCvProfilePort.java
  GeneratePitchPort.java
  GetPitchHistoryPort.java
  GetLatestPitchPort.java
  ExportPitchPort.java

application/port/out/
  CvProfileRepositoryPort.java
  GeneratedPitchRepositoryPort.java

application/dto/
  CvProfileDto.java
  WorkExperienceDto.java
  GeneratedPitchDto.java
  CreateCvProfileCommand.java
  UpdateCvProfileCommand.java
  GeneratePitchCommand.java

application/usecase/
  CreateCvProfileUseCase.java
  UpdateCvProfileUseCase.java
  GetCvProfileUseCase.java
  GeneratePitchUseCase.java
  GetPitchHistoryUseCase.java
  ExportPitchUseCase.java

infrastructure/persistence/entity/
  CvProfileEntity.java
  GeneratedPitchEntity.java

infrastructure/persistence/repository/
  JpaCvProfileRepository.java
  JpaGeneratedPitchRepository.java

infrastructure/persistence/mapper/
  CvProfilePersistenceMapper.java
  GeneratedPitchPersistenceMapper.java

infrastructure/persistence/adapter/
  CvProfileRepositoryAdapter.java
  GeneratedPitchRepositoryAdapter.java

infrastructure/generator/
  PitchSection.java
  PitchAssembler.java
  sections/
    IntroSectionEN.java
    IntroSectionJP.java
    ExperienceSectionEN.java
    ExperienceSectionJP.java
    TechStackSection.java
    MotivationSectionEN.java
    MotivationSectionJP.java
    CertificationsSection.java
    ClosingSectionEN.java
    ClosingSectionJP.java
    InterviewOpeningSection.java
    ProjectHighlightsSection.java

infrastructure/web/controller/
  CvGeneratorController.java

infrastructure/web/request/
  CreateCvProfileRequest.java
  UpdateCvProfileRequest.java
  GeneratePitchRequest.java
```
