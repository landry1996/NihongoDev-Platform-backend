# BLOC 9 — CV Generator Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a CV/pitch generator module that helps developers prepare customized pitches for the Japanese job market using composable template sections.

**Architecture:** Hexagonal (ports/adapters) with a composable pipeline pattern — a `PitchAssembler` composes ordered `PitchSection` implementations per `PitchType`. Each section adapts output based on user profile and target company type using Java 21 switch expressions. Content stored as structured Markdown.

**Tech Stack:** Java 21, Spring Boot 3.3.5, PostgreSQL (JSONB), Kafka, Flyway V8

---

## File Structure

### Domain Layer
| File | Responsibility |
|------|---------------|
| `domain/model/PitchType.java` | Enum: ENGLISH_PITCH, JAPANESE_PITCH, INTERVIEW_PRESENTATION, DEVELOPER_SUMMARY |
| `domain/model/TargetCompanyType.java` | Enum: STARTUP, ENTERPRISE, FOREIGN_IN_JAPAN, TRADITIONAL_JAPANESE |
| `domain/model/ExportFormat.java` | Enum: MARKDOWN, PLAIN_TEXT |
| `domain/model/WorkExperience.java` | Value object for professional history entry |
| `domain/model/CvProfile.java` | Aggregate with factory method, holds all profile data |
| `domain/model/GeneratedPitch.java` | Entity storing generated pitch content |
| `domain/event/PitchGeneratedEvent.java` | Domain event record with factory method |

### Application Layer
| File | Responsibility |
|------|---------------|
| `application/command/CreateCvProfileCommand.java` | Command record for profile creation |
| `application/command/UpdateCvProfileCommand.java` | Command record for profile update (nullable fields) |
| `application/command/GeneratePitchCommand.java` | Command record with pitchType |
| `application/dto/CvProfileDto.java` | DTO record for profile responses |
| `application/dto/WorkExperienceDto.java` | DTO record for experience |
| `application/dto/GeneratedPitchDto.java` | DTO record for pitch responses |
| `application/port/in/CreateCvProfilePort.java` | Port IN: create profile |
| `application/port/in/UpdateCvProfilePort.java` | Port IN: update profile |
| `application/port/in/GetCvProfilePort.java` | Port IN: get profile |
| `application/port/in/GeneratePitchPort.java` | Port IN: generate pitch |
| `application/port/in/GetPitchHistoryPort.java` | Port IN: get history + latest |
| `application/port/in/ExportPitchPort.java` | Port IN: export pitch |
| `application/port/out/CvProfileRepositoryPort.java` | Port OUT: CvProfile persistence |
| `application/port/out/GeneratedPitchRepositoryPort.java` | Port OUT: GeneratedPitch persistence |
| `application/usecase/CreateCvProfileUseCase.java` | Create profile use case |
| `application/usecase/UpdateCvProfileUseCase.java` | Update profile use case |
| `application/usecase/GetCvProfileUseCase.java` | Get profile use case |
| `application/usecase/GeneratePitchUseCase.java` | Generate pitch use case (orchestrator) |
| `application/usecase/GetPitchHistoryUseCase.java` | Get history/latest use case |
| `application/usecase/ExportPitchUseCase.java` | Export pitch use case |

### Infrastructure Layer — Generator
| File | Responsibility |
|------|---------------|
| `application/service/generator/PitchSection.java` | Interface for composable sections |
| `application/service/generator/PitchAssembler.java` | Assembles sections per PitchType |
| `application/service/generator/sections/IntroSectionEN.java` | English introduction section |
| `application/service/generator/sections/IntroSectionJP.java` | Japanese jikoshoukai section |
| `application/service/generator/sections/ExperienceSectionEN.java` | English experience section |
| `application/service/generator/sections/ExperienceSectionJP.java` | Japanese experience section |
| `application/service/generator/sections/TechStackSection.java` | Tech stack listing |
| `application/service/generator/sections/MotivationSectionEN.java` | English motivation section |
| `application/service/generator/sections/MotivationSectionJP.java` | Japanese motivation section |
| `application/service/generator/sections/CertificationsSection.java` | Certifications listing |
| `application/service/generator/sections/ClosingSectionEN.java` | English closing section |
| `application/service/generator/sections/ClosingSectionJP.java` | Japanese closing section |
| `application/service/generator/sections/InterviewOpeningSection.java` | Interview presentation opening |
| `application/service/generator/sections/ProjectHighlightsSection.java` | Notable projects section |

### Infrastructure Layer — Persistence
| File | Responsibility |
|------|---------------|
| `infrastructure/persistence/entity/CvProfileEntity.java` | JPA entity for cv_profiles |
| `infrastructure/persistence/entity/GeneratedPitchEntity.java` | JPA entity for generated_pitches |
| `infrastructure/persistence/repository/JpaCvProfileRepository.java` | Spring Data JPA |
| `infrastructure/persistence/repository/JpaGeneratedPitchRepository.java` | Spring Data JPA |
| `infrastructure/persistence/mapper/CvProfilePersistenceMapper.java` | Domain <-> Entity mapper |
| `infrastructure/persistence/mapper/GeneratedPitchPersistenceMapper.java` | Domain <-> Entity mapper |
| `infrastructure/persistence/adapter/CvProfileRepositoryAdapter.java` | Implements CvProfileRepositoryPort |
| `infrastructure/persistence/adapter/GeneratedPitchRepositoryAdapter.java` | Implements GeneratedPitchRepositoryPort |

### Infrastructure Layer — Web
| File | Responsibility |
|------|---------------|
| `infrastructure/web/controller/CvGeneratorController.java` | REST controller with 7 endpoints |
| `infrastructure/web/request/CreateCvProfileRequest.java` | Validated request DTO |
| `infrastructure/web/request/UpdateCvProfileRequest.java` | Validated request DTO |
| `infrastructure/web/request/GeneratePitchRequest.java` | Validated request DTO |

### Database
| File | Responsibility |
|------|---------------|
| `src/main/resources/db/migration/V8__cv_generator.sql` | Tables: cv_profiles, generated_pitches |

### Modified Files
| File | Change |
|------|--------|
| `infrastructure/config/KafkaTopicsProperties.java` | Add cvGeneratorEvents field |
| `infrastructure/kafka/KafkaEventPublisherAdapter.java` | Add cv-generator-events to registry |
| `src/main/resources/application.yml` | Add cv-generator-events topic config |

### Tests
| File | Responsibility |
|------|---------------|
| `test/.../domain/model/CvProfileTest.java` | Domain model tests |
| `test/.../application/service/generator/sections/IntroSectionENTest.java` | Intro EN tests |
| `test/.../application/service/generator/sections/IntroSectionJPTest.java` | Intro JP tests |
| `test/.../application/service/generator/sections/ExperienceSectionENTest.java` | Experience tests |
| `test/.../application/service/generator/sections/TechStackSectionTest.java` | Tech stack tests |
| `test/.../application/service/generator/PitchAssemblerTest.java` | Assembler tests |
| `test/.../application/usecase/CreateCvProfileUseCaseTest.java` | Create profile tests |
| `test/.../application/usecase/GeneratePitchUseCaseTest.java` | Generate pitch tests |
| `test/.../application/usecase/ExportPitchUseCaseTest.java` | Export pitch tests |

---

## Task 1: Domain Enums & Value Objects

**Files:**
- Create: `src/main/java/com/nihongodev/platform/domain/model/PitchType.java`
- Create: `src/main/java/com/nihongodev/platform/domain/model/TargetCompanyType.java`
- Create: `src/main/java/com/nihongodev/platform/domain/model/ExportFormat.java`
- Create: `src/main/java/com/nihongodev/platform/domain/model/WorkExperience.java`

- [ ] **Step 1: Create PitchType enum**

```java
package com.nihongodev.platform.domain.model;

public enum PitchType {
    ENGLISH_PITCH,
    JAPANESE_PITCH,
    INTERVIEW_PRESENTATION,
    DEVELOPER_SUMMARY
}
```

- [ ] **Step 2: Create TargetCompanyType enum**

```java
package com.nihongodev.platform.domain.model;

public enum TargetCompanyType {
    STARTUP,
    ENTERPRISE,
    FOREIGN_IN_JAPAN,
    TRADITIONAL_JAPANESE
}
```

- [ ] **Step 3: Create ExportFormat enum**

```java
package com.nihongodev.platform.domain.model;

public enum ExportFormat {
    MARKDOWN,
    PLAIN_TEXT
}
```

- [ ] **Step 4: Create WorkExperience value object**

```java
package com.nihongodev.platform.domain.model;

import java.util.List;

public class WorkExperience {

    private String company;
    private String role;
    private int durationMonths;
    private List<String> highlights;

    public WorkExperience() {}

    public WorkExperience(String company, String role, int durationMonths, List<String> highlights) {
        this.company = company;
        this.role = role;
        this.durationMonths = durationMonths;
        this.highlights = highlights;
    }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public int getDurationMonths() { return durationMonths; }
    public void setDurationMonths(int durationMonths) { this.durationMonths = durationMonths; }
    public List<String> getHighlights() { return highlights; }
    public void setHighlights(List<String> highlights) { this.highlights = highlights; }
}
```

- [ ] **Step 5: Verify compilation**

Run: `cd /c/Users/pierre.l.tchiengue/NihongoDev-Platform-backend && mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/nihongodev/platform/domain/model/PitchType.java \
        src/main/java/com/nihongodev/platform/domain/model/TargetCompanyType.java \
        src/main/java/com/nihongodev/platform/domain/model/ExportFormat.java \
        src/main/java/com/nihongodev/platform/domain/model/WorkExperience.java
git commit -m "feat(bloc9): add domain enums and WorkExperience value object"
```

---

## Task 2: CvProfile Domain Model

**Files:**
- Create: `src/main/java/com/nihongodev/platform/domain/model/CvProfile.java`
- Test: `src/test/java/com/nihongodev/platform/domain/model/CvProfileTest.java`

- [ ] **Step 1: Write the CvProfile test**

```java
package com.nihongodev.platform.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CvProfile")
class CvProfileTest {

    @Test
    @DisplayName("should create profile with factory method")
    void shouldCreateProfile() {
        UUID userId = UUID.randomUUID();
        CvProfile profile = CvProfile.create(userId, "Tanaka Taro", "Backend Engineer", 5, TargetCompanyType.STARTUP);

        assertThat(profile.getId()).isNotNull();
        assertThat(profile.getUserId()).isEqualTo(userId);
        assertThat(profile.getFullName()).isEqualTo("Tanaka Taro");
        assertThat(profile.getTargetRole()).isEqualTo("Backend Engineer");
        assertThat(profile.getYearsOfExperience()).isEqualTo(5);
        assertThat(profile.getTargetCompanyType()).isEqualTo(TargetCompanyType.STARTUP);
        assertThat(profile.getTechStack()).isEmpty();
        assertThat(profile.getExperiences()).isEmpty();
        assertThat(profile.getCertifications()).isEmpty();
        assertThat(profile.getNotableProjects()).isEmpty();
        assertThat(profile.getCreatedAt()).isNotNull();
        assertThat(profile.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("should add experience to profile")
    void shouldAddExperience() {
        CvProfile profile = CvProfile.create(UUID.randomUUID(), "Tanaka", "Backend", 3, TargetCompanyType.ENTERPRISE);
        WorkExperience exp = new WorkExperience("Google", "SWE", 24, List.of("Built microservices"));

        profile.setExperiences(List.of(exp));

        assertThat(profile.getExperiences()).hasSize(1);
        assertThat(profile.getExperiences().get(0).getCompany()).isEqualTo("Google");
    }

    @Test
    @DisplayName("should set tech stack")
    void shouldSetTechStack() {
        CvProfile profile = CvProfile.create(UUID.randomUUID(), "Tanaka", "Backend", 3, TargetCompanyType.ENTERPRISE);

        profile.setTechStack(List.of("Java", "Spring Boot", "PostgreSQL", "Kafka"));

        assertThat(profile.getTechStack()).containsExactly("Java", "Spring Boot", "PostgreSQL", "Kafka");
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd /c/Users/pierre.l.tchiengue/NihongoDev-Platform-backend && mvn test -pl . -Dtest=CvProfileTest -q`
Expected: FAIL — `CvProfile` class does not exist

- [ ] **Step 3: Implement CvProfile domain model**

```java
package com.nihongodev.platform.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CvProfile {

    private UUID id;
    private UUID userId;
    private String fullName;
    private String currentRole;
    private String targetRole;
    private int yearsOfExperience;
    private TargetCompanyType targetCompanyType;
    private List<String> techStack;
    private List<WorkExperience> experiences;
    private List<String> certifications;
    private List<String> notableProjects;
    private String motivationJapan;
    private String japaneseLevel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CvProfile() {}

    public static CvProfile create(UUID userId, String fullName, String targetRole,
                                   int yearsOfExperience, TargetCompanyType targetCompanyType) {
        CvProfile profile = new CvProfile();
        profile.id = UUID.randomUUID();
        profile.userId = userId;
        profile.fullName = fullName;
        profile.targetRole = targetRole;
        profile.yearsOfExperience = yearsOfExperience;
        profile.targetCompanyType = targetCompanyType;
        profile.techStack = new ArrayList<>();
        profile.experiences = new ArrayList<>();
        profile.certifications = new ArrayList<>();
        profile.notableProjects = new ArrayList<>();
        profile.createdAt = LocalDateTime.now();
        profile.updatedAt = LocalDateTime.now();
        return profile;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getCurrentRole() { return currentRole; }
    public void setCurrentRole(String currentRole) { this.currentRole = currentRole; }
    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }
    public int getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(int yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }
    public TargetCompanyType getTargetCompanyType() { return targetCompanyType; }
    public void setTargetCompanyType(TargetCompanyType targetCompanyType) { this.targetCompanyType = targetCompanyType; }
    public List<String> getTechStack() { return techStack; }
    public void setTechStack(List<String> techStack) { this.techStack = techStack; }
    public List<WorkExperience> getExperiences() { return experiences; }
    public void setExperiences(List<WorkExperience> experiences) { this.experiences = experiences; }
    public List<String> getCertifications() { return certifications; }
    public void setCertifications(List<String> certifications) { this.certifications = certifications; }
    public List<String> getNotableProjects() { return notableProjects; }
    public void setNotableProjects(List<String> notableProjects) { this.notableProjects = notableProjects; }
    public String getMotivationJapan() { return motivationJapan; }
    public void setMotivationJapan(String motivationJapan) { this.motivationJapan = motivationJapan; }
    public String getJapaneseLevel() { return japaneseLevel; }
    public void setJapaneseLevel(String japaneseLevel) { this.japaneseLevel = japaneseLevel; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `cd /c/Users/pierre.l.tchiengue/NihongoDev-Platform-backend && mvn test -pl . -Dtest=CvProfileTest -q`
Expected: 3 tests PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/nihongodev/platform/domain/model/CvProfile.java \
        src/test/java/com/nihongodev/platform/domain/model/CvProfileTest.java
git commit -m "feat(bloc9): add CvProfile domain model with tests"
```

---

## Task 3: GeneratedPitch Model & Domain Event

**Files:**
- Create: `src/main/java/com/nihongodev/platform/domain/model/GeneratedPitch.java`
- Create: `src/main/java/com/nihongodev/platform/domain/event/PitchGeneratedEvent.java`

- [ ] **Step 1: Create GeneratedPitch domain model**

```java
package com.nihongodev.platform.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class GeneratedPitch {

    private UUID id;
    private UUID userId;
    private PitchType pitchType;
    private String content;
    private UUID profileSnapshotId;
    private LocalDateTime generatedAt;

    public GeneratedPitch() {}

    public static GeneratedPitch create(UUID userId, PitchType pitchType, String content, UUID profileSnapshotId) {
        GeneratedPitch pitch = new GeneratedPitch();
        pitch.id = UUID.randomUUID();
        pitch.userId = userId;
        pitch.pitchType = pitchType;
        pitch.content = content;
        pitch.profileSnapshotId = profileSnapshotId;
        pitch.generatedAt = LocalDateTime.now();
        return pitch;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public PitchType getPitchType() { return pitchType; }
    public void setPitchType(PitchType pitchType) { this.pitchType = pitchType; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public UUID getProfileSnapshotId() { return profileSnapshotId; }
    public void setProfileSnapshotId(UUID profileSnapshotId) { this.profileSnapshotId = profileSnapshotId; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
}
```

- [ ] **Step 2: Create PitchGeneratedEvent domain event**

```java
package com.nihongodev.platform.domain.event;

import com.nihongodev.platform.domain.model.PitchType;

import java.time.LocalDateTime;
import java.util.UUID;

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

- [ ] **Step 3: Verify compilation**

Run: `cd /c/Users/pierre.l.tchiengue/NihongoDev-Platform-backend && mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/nihongodev/platform/domain/model/GeneratedPitch.java \
        src/main/java/com/nihongodev/platform/domain/event/PitchGeneratedEvent.java
git commit -m "feat(bloc9): add GeneratedPitch model and PitchGeneratedEvent"
```

---

## Task 4: Application Layer — Commands & DTOs

**Files:**
- Create: `src/main/java/com/nihongodev/platform/application/command/CreateCvProfileCommand.java`
- Create: `src/main/java/com/nihongodev/platform/application/command/UpdateCvProfileCommand.java`
- Create: `src/main/java/com/nihongodev/platform/application/command/GeneratePitchCommand.java`
- Create: `src/main/java/com/nihongodev/platform/application/dto/WorkExperienceDto.java`
- Create: `src/main/java/com/nihongodev/platform/application/dto/CvProfileDto.java`
- Create: `src/main/java/com/nihongodev/platform/application/dto/GeneratedPitchDto.java`

- [ ] **Step 1: Create CreateCvProfileCommand**

```java
package com.nihongodev.platform.application.command;

import com.nihongodev.platform.domain.model.TargetCompanyType;

import java.util.List;

public record CreateCvProfileCommand(
        String fullName,
        String currentRole,
        String targetRole,
        int yearsOfExperience,
        TargetCompanyType targetCompanyType,
        List<String> techStack,
        List<WorkExperienceData> experiences,
        List<String> certifications,
        List<String> notableProjects,
        String motivationJapan,
        String japaneseLevel
) {
    public record WorkExperienceData(
            String company,
            String role,
            int durationMonths,
            List<String> highlights
    ) {}
}
```

- [ ] **Step 2: Create UpdateCvProfileCommand**

```java
package com.nihongodev.platform.application.command;

import com.nihongodev.platform.domain.model.TargetCompanyType;

import java.util.List;

public record UpdateCvProfileCommand(
        String fullName,
        String currentRole,
        String targetRole,
        Integer yearsOfExperience,
        TargetCompanyType targetCompanyType,
        List<String> techStack,
        List<CreateCvProfileCommand.WorkExperienceData> experiences,
        List<String> certifications,
        List<String> notableProjects,
        String motivationJapan,
        String japaneseLevel
) {}
```

- [ ] **Step 3: Create GeneratePitchCommand**

```java
package com.nihongodev.platform.application.command;

import com.nihongodev.platform.domain.model.PitchType;

public record GeneratePitchCommand(PitchType pitchType) {}
```

- [ ] **Step 4: Create WorkExperienceDto**

```java
package com.nihongodev.platform.application.dto;

import java.util.List;

public record WorkExperienceDto(
        String company,
        String role,
        int durationMonths,
        List<String> highlights
) {}
```

- [ ] **Step 5: Create CvProfileDto**

```java
package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.TargetCompanyType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CvProfileDto(
        UUID id,
        UUID userId,
        String fullName,
        String currentRole,
        String targetRole,
        int yearsOfExperience,
        TargetCompanyType targetCompanyType,
        List<String> techStack,
        List<WorkExperienceDto> experiences,
        List<String> certifications,
        List<String> notableProjects,
        String motivationJapan,
        String japaneseLevel,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
```

- [ ] **Step 6: Create GeneratedPitchDto**

```java
package com.nihongodev.platform.application.dto;

import com.nihongodev.platform.domain.model.PitchType;

import java.time.LocalDateTime;
import java.util.UUID;

public record GeneratedPitchDto(
        UUID id,
        PitchType pitchType,
        String content,
        LocalDateTime generatedAt
) {}
```

- [ ] **Step 7: Verify compilation**

Run: `cd /c/Users/pierre.l.tchiengue/NihongoDev-Platform-backend && mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 8: Commit**

```bash
git add src/main/java/com/nihongodev/platform/application/command/CreateCvProfileCommand.java \
        src/main/java/com/nihongodev/platform/application/command/UpdateCvProfileCommand.java \
        src/main/java/com/nihongodev/platform/application/command/GeneratePitchCommand.java \
        src/main/java/com/nihongodev/platform/application/dto/WorkExperienceDto.java \
        src/main/java/com/nihongodev/platform/application/dto/CvProfileDto.java \
        src/main/java/com/nihongodev/platform/application/dto/GeneratedPitchDto.java
git commit -m "feat(bloc9): add commands and DTOs for CV Generator"
```

---

## Task 5: Application Layer — Ports

**Files:**
- Create: `src/main/java/com/nihongodev/platform/application/port/in/CreateCvProfilePort.java`
- Create: `src/main/java/com/nihongodev/platform/application/port/in/UpdateCvProfilePort.java`
- Create: `src/main/java/com/nihongodev/platform/application/port/in/GetCvProfilePort.java`
- Create: `src/main/java/com/nihongodev/platform/application/port/in/GeneratePitchPort.java`
- Create: `src/main/java/com/nihongodev/platform/application/port/in/GetPitchHistoryPort.java`
- Create: `src/main/java/com/nihongodev/platform/application/port/in/ExportPitchPort.java`
- Create: `src/main/java/com/nihongodev/platform/application/port/out/CvProfileRepositoryPort.java`
- Create: `src/main/java/com/nihongodev/platform/application/port/out/GeneratedPitchRepositoryPort.java`

- [ ] **Step 1: Create all Ports IN**

`CreateCvProfilePort.java`:
```java
package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.command.CreateCvProfileCommand;
import com.nihongodev.platform.application.dto.CvProfileDto;

import java.util.UUID;

public interface CreateCvProfilePort {
    CvProfileDto create(UUID userId, CreateCvProfileCommand command);
}
```

`UpdateCvProfilePort.java`:
```java
package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.command.UpdateCvProfileCommand;
import com.nihongodev.platform.application.dto.CvProfileDto;

import java.util.UUID;

public interface UpdateCvProfilePort {
    CvProfileDto update(UUID userId, UpdateCvProfileCommand command);
}
```

`GetCvProfilePort.java`:
```java
package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.CvProfileDto;

import java.util.UUID;

public interface GetCvProfilePort {
    CvProfileDto get(UUID userId);
}
```

`GeneratePitchPort.java`:
```java
package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.command.GeneratePitchCommand;
import com.nihongodev.platform.application.dto.GeneratedPitchDto;

import java.util.UUID;

public interface GeneratePitchPort {
    GeneratedPitchDto generate(UUID userId, GeneratePitchCommand command);
}
```

`GetPitchHistoryPort.java`:
```java
package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.application.dto.GeneratedPitchDto;
import com.nihongodev.platform.domain.model.PitchType;

import java.util.List;
import java.util.UUID;

public interface GetPitchHistoryPort {
    List<GeneratedPitchDto> getHistory(UUID userId, PitchType type);
    GeneratedPitchDto getLatest(UUID userId, PitchType type);
}
```

`ExportPitchPort.java`:
```java
package com.nihongodev.platform.application.port.in;

import com.nihongodev.platform.domain.model.ExportFormat;

import java.util.UUID;

public interface ExportPitchPort {
    String export(UUID userId, UUID pitchId, ExportFormat format);
}
```

- [ ] **Step 2: Create Ports OUT**

`CvProfileRepositoryPort.java`:
```java
package com.nihongodev.platform.application.port.out;

import com.nihongodev.platform.domain.model.CvProfile;

import java.util.Optional;
import java.util.UUID;

public interface CvProfileRepositoryPort {
    CvProfile save(CvProfile profile);
    Optional<CvProfile> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
}
```

`GeneratedPitchRepositoryPort.java`:
```java
package com.nihongodev.platform.application.port.out;

import com.nihongodev.platform.domain.model.GeneratedPitch;
import com.nihongodev.platform.domain.model.PitchType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GeneratedPitchRepositoryPort {
    GeneratedPitch save(GeneratedPitch pitch);
    Optional<GeneratedPitch> findById(UUID id);
    List<GeneratedPitch> findByUserIdAndPitchType(UUID userId, PitchType pitchType);
    Optional<GeneratedPitch> findLatestByUserIdAndPitchType(UUID userId, PitchType pitchType);
}
```

- [ ] **Step 3: Verify compilation**

Run: `cd /c/Users/pierre.l.tchiengue/NihongoDev-Platform-backend && mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/nihongodev/platform/application/port/in/CreateCvProfilePort.java \
        src/main/java/com/nihongodev/platform/application/port/in/UpdateCvProfilePort.java \
        src/main/java/com/nihongodev/platform/application/port/in/GetCvProfilePort.java \
        src/main/java/com/nihongodev/platform/application/port/in/GeneratePitchPort.java \
        src/main/java/com/nihongodev/platform/application/port/in/GetPitchHistoryPort.java \
        src/main/java/com/nihongodev/platform/application/port/in/ExportPitchPort.java \
        src/main/java/com/nihongodev/platform/application/port/out/CvProfileRepositoryPort.java \
        src/main/java/com/nihongodev/platform/application/port/out/GeneratedPitchRepositoryPort.java
git commit -m "feat(bloc9): add ports IN and OUT for CV Generator"
```

---

## Task 6: Pitch Section Interface & PitchAssembler

**Files:**
- Create: `src/main/java/com/nihongodev/platform/application/service/generator/PitchSection.java`
- Create: `src/main/java/com/nihongodev/platform/application/service/generator/PitchAssembler.java`
- Test: `src/test/java/com/nihongodev/platform/application/service/generator/PitchAssemblerTest.java`

- [ ] **Step 1: Create PitchSection interface**

```java
package com.nihongodev.platform.application.service.generator;

import com.nihongodev.platform.domain.model.CvProfile;

public interface PitchSection {
    String render(CvProfile profile);
    int order();
}
```

- [ ] **Step 2: Create PitchAssembler**

```java
package com.nihongodev.platform.application.service.generator;

import com.nihongodev.platform.domain.model.CvProfile;
import com.nihongodev.platform.domain.model.PitchType;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PitchAssembler {

    private final Map<PitchType, List<PitchSection>> registry;

    public PitchAssembler(Map<PitchType, List<PitchSection>> registry) {
        this.registry = registry;
    }

    public String assemble(CvProfile profile, PitchType type) {
        List<PitchSection> sections = registry.get(type);
        if (sections == null || sections.isEmpty()) {
            throw new IllegalArgumentException("No sections registered for pitch type: " + type);
        }
        return sections.stream()
                .sorted(Comparator.comparingInt(PitchSection::order))
                .map(section -> section.render(profile))
                .filter(content -> content != null && !content.isBlank())
                .collect(Collectors.joining("\n\n"));
    }
}
```

- [ ] **Step 3: Write PitchAssembler test**

```java
package com.nihongodev.platform.application.service.generator;

import com.nihongodev.platform.domain.model.CvProfile;
import com.nihongodev.platform.domain.model.PitchType;
import com.nihongodev.platform.domain.model.TargetCompanyType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PitchAssembler")
class PitchAssemblerTest {

    @Test
    @DisplayName("should assemble sections in order")
    void shouldAssembleSectionsInOrder() {
        PitchSection first = new PitchSection() {
            @Override public String render(CvProfile profile) { return "## First\nContent A"; }
            @Override public int order() { return 1; }
        };
        PitchSection second = new PitchSection() {
            @Override public String render(CvProfile profile) { return "## Second\nContent B"; }
            @Override public int order() { return 2; }
        };

        PitchAssembler assembler = new PitchAssembler(Map.of(PitchType.ENGLISH_PITCH, List.of(second, first)));
        CvProfile profile = CvProfile.create(UUID.randomUUID(), "Test", "Dev", 3, TargetCompanyType.STARTUP);

        String result = assembler.assemble(profile, PitchType.ENGLISH_PITCH);

        assertThat(result).isEqualTo("## First\nContent A\n\n## Second\nContent B");
    }

    @Test
    @DisplayName("should skip empty sections")
    void shouldSkipEmptySections() {
        PitchSection content = new PitchSection() {
            @Override public String render(CvProfile profile) { return "## Hello"; }
            @Override public int order() { return 1; }
        };
        PitchSection empty = new PitchSection() {
            @Override public String render(CvProfile profile) { return ""; }
            @Override public int order() { return 2; }
        };

        PitchAssembler assembler = new PitchAssembler(Map.of(PitchType.ENGLISH_PITCH, List.of(content, empty)));
        CvProfile profile = CvProfile.create(UUID.randomUUID(), "Test", "Dev", 3, TargetCompanyType.STARTUP);

        String result = assembler.assemble(profile, PitchType.ENGLISH_PITCH);

        assertThat(result).isEqualTo("## Hello");
    }

    @Test
    @DisplayName("should throw for unknown pitch type")
    void shouldThrowForUnknownType() {
        PitchAssembler assembler = new PitchAssembler(Map.of());
        CvProfile profile = CvProfile.create(UUID.randomUUID(), "Test", "Dev", 3, TargetCompanyType.STARTUP);

        assertThatThrownBy(() -> assembler.assemble(profile, PitchType.ENGLISH_PITCH))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No sections registered");
    }
}
```

- [ ] **Step 4: Run tests**

Run: `cd /c/Users/pierre.l.tchiengue/NihongoDev-Platform-backend && mvn test -pl . -Dtest=PitchAssemblerTest -q`
Expected: 3 tests PASS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/nihongodev/platform/application/service/generator/PitchSection.java \
        src/main/java/com/nihongodev/platform/application/service/generator/PitchAssembler.java \
        src/test/java/com/nihongodev/platform/application/service/generator/PitchAssemblerTest.java
git commit -m "feat(bloc9): add PitchSection interface and PitchAssembler with tests"
```

---

## Task 7: Pitch Sections — English

**Files:**
- Create: `src/main/java/com/nihongodev/platform/application/service/generator/sections/IntroSectionEN.java`
- Create: `src/main/java/com/nihongodev/platform/application/service/generator/sections/ExperienceSectionEN.java`
- Create: `src/main/java/com/nihongodev/platform/application/service/generator/sections/TechStackSection.java`
- Create: `src/main/java/com/nihongodev/platform/application/service/generator/sections/MotivationSectionEN.java`
- Create: `src/main/java/com/nihongodev/platform/application/service/generator/sections/CertificationsSection.java`
- Create: `src/main/java/com/nihongodev/platform/application/service/generator/sections/ClosingSectionEN.java`
- Test: `src/test/java/com/nihongodev/platform/application/service/generator/sections/IntroSectionENTest.java`
- Test: `src/test/java/com/nihongodev/platform/application/service/generator/sections/ExperienceSectionENTest.java`
- Test: `src/test/java/com/nihongodev/platform/application/service/generator/sections/TechStackSectionTest.java`

- [ ] **Step 1: Create IntroSectionEN**

```java
package com.nihongodev.platform.application.service.generator.sections;

import com.nihongodev.platform.application.service.generator.PitchSection;
import com.nihongodev.platform.domain.model.CvProfile;

public class IntroSectionEN implements PitchSection {

    @Override
    public String render(CvProfile profile) {
        String tone = switch (profile.getTargetCompanyType()) {
            case STARTUP -> "passionate about building innovative solutions";
            case ENTERPRISE -> "focused on delivering scalable and reliable systems";
            case FOREIGN_IN_JAPAN -> "adaptable and experienced in multicultural environments";
            case TRADITIONAL_JAPANESE -> "dedicated to quality and continuous improvement";
        };

        StringBuilder sb = new StringBuilder();
        sb.append("## Introduction\n\n");
        sb.append("Hi, I'm **").append(profile.getFullName()).append("**");
        if (profile.getCurrentRole() != null && !profile.getCurrentRole().isBlank()) {
            sb.append(", currently working as a **").append(profile.getCurrentRole()).append("**");
        }
        sb.append(". With **").append(profile.getYearsOfExperience()).append(" years** of experience");
        sb.append(", I am ").append(tone).append(".");
        sb.append("\n\nI'm looking to join as a **").append(profile.getTargetRole()).append("** in Japan.");

        return sb.toString();
    }

    @Override
    public int order() { return 10; }
}
```

- [ ] **Step 2: Create ExperienceSectionEN**

```java
package com.nihongodev.platform.application.service.generator.sections;

import com.nihongodev.platform.application.service.generator.PitchSection;
import com.nihongodev.platform.domain.model.CvProfile;
import com.nihongodev.platform.domain.model.WorkExperience;

public class ExperienceSectionEN implements PitchSection {

    @Override
    public String render(CvProfile profile) {
        if (profile.getExperiences() == null || profile.getExperiences().isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("## Professional Experience\n");

        for (WorkExperience exp : profile.getExperiences()) {
            int years = exp.getDurationMonths() / 12;
            int months = exp.getDurationMonths() % 12;
            String duration = years > 0
                    ? years + " year" + (years > 1 ? "s" : "") + (months > 0 ? " " + months + " months" : "")
                    : months + " months";

            sb.append("\n### ").append(exp.getRole()).append(" at ").append(exp.getCompany());
            sb.append("\n*").append(duration).append("*\n");

            if (exp.getHighlights() != null) {
                for (String highlight : exp.getHighlights()) {
                    sb.append("\n- ").append(highlight);
                }
            }
            sb.append("\n");
        }

        return sb.toString().stripTrailing();
    }

    @Override
    public int order() { return 20; }
}
```

- [ ] **Step 3: Create TechStackSection**

```java
package com.nihongodev.platform.application.service.generator.sections;

import com.nihongodev.platform.application.service.generator.PitchSection;
import com.nihongodev.platform.domain.model.CvProfile;

public class TechStackSection implements PitchSection {

    @Override
    public String render(CvProfile profile) {
        if (profile.getTechStack() == null || profile.getTechStack().isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("## Technical Skills\n\n");
        sb.append(String.join(" | ", profile.getTechStack()));

        return sb.toString();
    }

    @Override
    public int order() { return 30; }
}
```

- [ ] **Step 4: Create MotivationSectionEN**

```java
package com.nihongodev.platform.application.service.generator.sections;

import com.nihongodev.platform.application.service.generator.PitchSection;
import com.nihongodev.platform.domain.model.CvProfile;

public class MotivationSectionEN implements PitchSection {

    @Override
    public String render(CvProfile profile) {
        if (profile.getMotivationJapan() == null || profile.getMotivationJapan().isBlank()) {
            return "";
        }

        String framing = switch (profile.getTargetCompanyType()) {
            case STARTUP -> "I'm drawn to Japan's innovative startup ecosystem.";
            case ENTERPRISE -> "I'm attracted by the engineering culture at scale in Japan.";
            case FOREIGN_IN_JAPAN -> "I want to bridge international collaboration in Japan's tech scene.";
            case TRADITIONAL_JAPANESE -> "I deeply respect Japanese craftsmanship and dedication to quality.";
        };

        return "## Why Japan\n\n" + framing + " " + profile.getMotivationJapan();
    }

    @Override
    public int order() { return 40; }
}
```

- [ ] **Step 5: Create CertificationsSection**

```java
package com.nihongodev.platform.application.service.generator.sections;

import com.nihongodev.platform.application.service.generator.PitchSection;
import com.nihongodev.platform.domain.model.CvProfile;

public class CertificationsSection implements PitchSection {

    @Override
    public String render(CvProfile profile) {
        if (profile.getCertifications() == null || profile.getCertifications().isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("## Certifications\n");
        for (String cert : profile.getCertifications()) {
            sb.append("\n- ").append(cert);
        }

        return sb.toString();
    }

    @Override
    public int order() { return 50; }
}
```

- [ ] **Step 6: Create ClosingSectionEN**

```java
package com.nihongodev.platform.application.service.generator.sections;

import com.nihongodev.platform.application.service.generator.PitchSection;
import com.nihongodev.platform.domain.model.CvProfile;

public class ClosingSectionEN implements PitchSection {

    @Override
    public String render(CvProfile profile) {
        String closing = switch (profile.getTargetCompanyType()) {
            case STARTUP -> "I'm excited to bring energy and fresh ideas to your team. Let's build something great together!";
            case ENTERPRISE -> "I look forward to contributing to your team's mission with reliability and technical depth.";
            case FOREIGN_IN_JAPAN -> "I'm ready to contribute across cultures and bring a global perspective to your team.";
            case TRADITIONAL_JAPANESE -> "I would be honored to contribute to your team and grow together. Thank you for your consideration.";
        };

        return "## Closing\n\n" + closing;
    }

    @Override
    public int order() { return 60; }
}
```

- [ ] **Step 7: Write IntroSectionEN test**

```java
package com.nihongodev.platform.application.service.generator.sections;

import com.nihongodev.platform.domain.model.CvProfile;
import com.nihongodev.platform.domain.model.TargetCompanyType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("IntroSectionEN")
class IntroSectionENTest {

    private final IntroSectionEN section = new IntroSectionEN();

    @Test
    @DisplayName("should render full intro with current role")
    void shouldRenderFullIntro() {
        CvProfile profile = CvProfile.create(UUID.randomUUID(), "Pierre Tchiengue", "Backend Engineer", 5, TargetCompanyType.STARTUP);
        profile.setCurrentRole("Java Developer");

        String result = section.render(profile);

        assertThat(result).contains("**Pierre Tchiengue**");
        assertThat(result).contains("**Java Developer**");
        assertThat(result).contains("**5 years**");
        assertThat(result).contains("innovative");
        assertThat(result).contains("**Backend Engineer**");
    }

    @Test
    @DisplayName("should adapt tone for enterprise")
    void shouldAdaptForEnterprise() {
        CvProfile profile = CvProfile.create(UUID.randomUUID(), "Tanaka", "SRE", 8, TargetCompanyType.ENTERPRISE);

        String result = section.render(profile);

        assertThat(result).contains("scalable and reliable");
    }

    @Test
    @DisplayName("should adapt tone for traditional Japanese")
    void shouldAdaptForTraditionalJapanese() {
        CvProfile profile = CvProfile.create(UUID.randomUUID(), "Tanaka", "Dev", 3, TargetCompanyType.TRADITIONAL_JAPANESE);

        String result = section.render(profile);

        assertThat(result).contains("quality and continuous improvement");
    }

    @Test
    @DisplayName("should handle missing current role")
    void shouldHandleMissingCurrentRole() {
        CvProfile profile = CvProfile.create(UUID.randomUUID(), "Tanaka", "Dev", 2, TargetCompanyType.FOREIGN_IN_JAPAN);

        String result = section.render(profile);

        assertThat(result).doesNotContain("currently working as");
        assertThat(result).contains("multicultural");
    }
}
```

- [ ] **Step 8: Write ExperienceSectionEN test**

```java
package com.nihongodev.platform.application.service.generator.sections;

import com.nihongodev.platform.domain.model.CvProfile;
import com.nihongodev.platform.domain.model.TargetCompanyType;
import com.nihongodev.platform.domain.model.WorkExperience;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ExperienceSectionEN")
class ExperienceSectionENTest {

    private final ExperienceSectionEN section = new ExperienceSectionEN();

    @Test
    @DisplayName("should render experience with highlights")
    void shouldRenderExperience() {
        CvProfile profile = CvProfile.create(UUID.randomUUID(), "Test", "Dev", 3, TargetCompanyType.STARTUP);
        profile.setExperiences(List.of(
                new WorkExperience("Google", "Backend Engineer", 30, List.of("Built API gateway", "Led migration"))
        ));

        String result = section.render(profile);

        assertThat(result).contains("## Professional Experience");
        assertThat(result).contains("Backend Engineer at Google");
        assertThat(result).contains("2 years 6 months");
        assertThat(result).contains("- Built API gateway");
        assertThat(result).contains("- Led migration");
    }

    @Test
    @DisplayName("should return empty for no experiences")
    void shouldReturnEmptyForNoExperiences() {
        CvProfile profile = CvProfile.create(UUID.randomUUID(), "Test", "Dev", 3, TargetCompanyType.STARTUP);

        String result = section.render(profile);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should render multiple experiences")
    void shouldRenderMultipleExperiences() {
        CvProfile profile = CvProfile.create(UUID.randomUUID(), "Test", "Dev", 5, TargetCompanyType.ENTERPRISE);
        profile.setExperiences(List.of(
                new WorkExperience("Company A", "Senior Dev", 24, List.of("Led team")),
                new WorkExperience("Company B", "Junior Dev", 12, List.of("Learned fast"))
        ));

        String result = section.render(profile);

        assertThat(result).contains("Senior Dev at Company A");
        assertThat(result).contains("Junior Dev at Company B");
    }
}
```

- [ ] **Step 9: Write TechStackSection test**

```java
package com.nihongodev.platform.application.service.generator.sections;

import com.nihongodev.platform.domain.model.CvProfile;
import com.nihongodev.platform.domain.model.TargetCompanyType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TechStackSection")
class TechStackSectionTest {

    private final TechStackSection section = new TechStackSection();

    @Test
    @DisplayName("should render tech stack as pipe-separated list")
    void shouldRenderTechStack() {
        CvProfile profile = CvProfile.create(UUID.randomUUID(), "Test", "Dev", 3, TargetCompanyType.STARTUP);
        profile.setTechStack(List.of("Java", "Spring Boot", "PostgreSQL"));

        String result = section.render(profile);

        assertThat(result).contains("## Technical Skills");
        assertThat(result).contains("Java | Spring Boot | PostgreSQL");
    }

    @Test
    @DisplayName("should return empty for empty tech stack")
    void shouldReturnEmptyForNoStack() {
        CvProfile profile = CvProfile.create(UUID.randomUUID(), "Test", "Dev", 3, TargetCompanyType.STARTUP);

        String result = section.render(profile);

        assertThat(result).isEmpty();
    }
}
```

- [ ] **Step 10: Run tests**

Run: `cd /c/Users/pierre.l.tchiengue/NihongoDev-Platform-backend && mvn test -pl . -Dtest="IntroSectionENTest,ExperienceSectionENTest,TechStackSectionTest" -q`
Expected: 9 tests PASS

- [ ] **Step 11: Commit**

```bash
git add src/main/java/com/nihongodev/platform/application/service/generator/sections/ \
        src/test/java/com/nihongodev/platform/application/service/generator/sections/
git commit -m "feat(bloc9): add English pitch sections with tests"
```

---

## Task 8: Pitch Sections — Japanese

**Files:**
- Create: `src/main/java/com/nihongodev/platform/application/service/generator/sections/IntroSectionJP.java`
- Create: `src/main/java/com/nihongodev/platform/application/service/generator/sections/ExperienceSectionJP.java`
- Create: `src/main/java/com/nihongodev/platform/application/service/generator/sections/MotivationSectionJP.java`
- Create: `src/main/java/com/nihongodev/platform/application/service/generator/sections/ClosingSectionJP.java`
- Test: `src/test/java/com/nihongodev/platform/application/service/generator/sections/IntroSectionJPTest.java`

- [ ] **Step 1: Create IntroSectionJP**

```java
package com.nihongodev.platform.application.service.generator.sections;

import com.nihongodev.platform.application.service.generator.PitchSection;
import com.nihongodev.platform.domain.model.CvProfile;

public class IntroSectionJP implements PitchSection {

    @Override
    public String render(CvProfile profile) {
        String greeting = switch (profile.getTargetCompanyType()) {
            case STARTUP, FOREIGN_IN_JAPAN -> "はじめまして。";
            case ENTERPRISE, TRADITIONAL_JAPANESE -> "はじめまして。お時間をいただきありがとうございます。";
        };

        StringBuilder sb = new StringBuilder();
        sb.append("## 自己紹介\n\n");
        sb.append(greeting).append("\n");
        sb.append("**").append(profile.getFullName()).append("**と申します。");
        sb.append("エンジニアとして**").append(profile.getYearsOfExperience()).append("年間**の経験があります。");
        sb.append("\n**").append(profile.getTargetRole()).append("**として働きたいと考えております。");

        if (profile.getJapaneseLevel() != null && !profile.getJapaneseLevel().isBlank()) {
            sb.append("\n日本語レベル: ").append(profile.getJapaneseLevel());
        }

        return sb.toString();
    }

    @Override
    public int order() { return 10; }
}
```

- [ ] **Step 2: Create ExperienceSectionJP**

```java
package com.nihongodev.platform.application.service.generator.sections;

import com.nihongodev.platform.application.service.generator.PitchSection;
import com.nihongodev.platform.domain.model.CvProfile;
import com.nihongodev.platform.domain.model.WorkExperience;

public class ExperienceSectionJP implements PitchSection {

    @Override
    public String render(CvProfile profile) {
        if (profile.getExperiences() == null || profile.getExperiences().isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("## 職歴\n");

        for (WorkExperience exp : profile.getExperiences()) {
            int years = exp.getDurationMonths() / 12;
            int months = exp.getDurationMonths() % 12;
            String duration = years > 0
                    ? years + "年" + (months > 0 ? months + "ヶ月" : "")
                    : months + "ヶ月";

            sb.append("\n### ").append(exp.getCompany()).append(" — ").append(exp.getRole());
            sb.append("\n*").append(duration).append("*\n");

            if (exp.getHighlights() != null) {
                for (String highlight : exp.getHighlights()) {
                    sb.append("\n- ").append(highlight);
                }
            }
            sb.append("\n");
        }

        return sb.toString().stripTrailing();
    }

    @Override
    public int order() { return 20; }
}
```

- [ ] **Step 3: Create MotivationSectionJP**

```java
package com.nihongodev.platform.application.service.generator.sections;

import com.nihongodev.platform.application.service.generator.PitchSection;
import com.nihongodev.platform.domain.model.CvProfile;

public class MotivationSectionJP implements PitchSection {

    @Override
    public String render(CvProfile profile) {
        if (profile.getMotivationJapan() == null || profile.getMotivationJapan().isBlank()) {
            return "";
        }

        String framing = switch (profile.getTargetCompanyType()) {
            case STARTUP -> "日本のスタートアップで新しい技術に挑戦したいと思っています。";
            case ENTERPRISE -> "大規模なシステム開発に貢献したいと考えています。";
            case FOREIGN_IN_JAPAN -> "国際的なチームで働きながら日本の文化を学びたいです。";
            case TRADITIONAL_JAPANESE -> "日本のものづくりの精神を学び、品質の高いソフトウェアを作りたいです。";
        };

        return "## 志望動機\n\n" + framing + "\n" + profile.getMotivationJapan();
    }

    @Override
    public int order() { return 40; }
}
```

- [ ] **Step 4: Create ClosingSectionJP**

```java
package com.nihongodev.platform.application.service.generator.sections;

import com.nihongodev.platform.application.service.generator.PitchSection;
import com.nihongodev.platform.domain.model.CvProfile;

public class ClosingSectionJP implements PitchSection {

    @Override
    public String render(CvProfile profile) {
        String closing = switch (profile.getTargetCompanyType()) {
            case STARTUP, FOREIGN_IN_JAPAN -> "ぜひ一緒に働かせていただければ嬉しいです。よろしくお願いいたします。";
            case ENTERPRISE, TRADITIONAL_JAPANESE -> "御社に貢献できるよう努力してまいります。何卒よろしくお願い申し上げます。";
        };

        return "## おわりに\n\n" + closing;
    }

    @Override
    public int order() { return 60; }
}
```

- [ ] **Step 5: Write IntroSectionJP test**

```java
package com.nihongodev.platform.application.service.generator.sections;

import com.nihongodev.platform.domain.model.CvProfile;
import com.nihongodev.platform.domain.model.TargetCompanyType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("IntroSectionJP")
class IntroSectionJPTest {

    private final IntroSectionJP section = new IntroSectionJP();

    @Test
    @DisplayName("should render Japanese jikoshoukai")
    void shouldRenderJikoshoukai() {
        CvProfile profile = CvProfile.create(UUID.randomUUID(), "Pierre", "Backend Engineer", 5, TargetCompanyType.STARTUP);
        profile.setJapaneseLevel("N3");

        String result = section.render(profile);

        assertThat(result).contains("## 自己紹介");
        assertThat(result).contains("**Pierre**と申します");
        assertThat(result).contains("**5年間**");
        assertThat(result).contains("**Backend Engineer**");
        assertThat(result).contains("日本語レベル: N3");
    }

    @Test
    @DisplayName("should use formal greeting for traditional company")
    void shouldUseFormalGreeting() {
        CvProfile profile = CvProfile.create(UUID.randomUUID(), "Tanaka", "Dev", 3, TargetCompanyType.TRADITIONAL_JAPANESE);

        String result = section.render(profile);

        assertThat(result).contains("お時間をいただきありがとうございます");
    }

    @Test
    @DisplayName("should use casual greeting for startup")
    void shouldUseCasualGreeting() {
        CvProfile profile = CvProfile.create(UUID.randomUUID(), "Tanaka", "Dev", 3, TargetCompanyType.STARTUP);

        String result = section.render(profile);

        assertThat(result).contains("はじめまして。");
        assertThat(result).doesNotContain("お時間をいただき");
    }

    @Test
    @DisplayName("should skip japanese level if not set")
    void shouldSkipJapaneseLevelIfNotSet() {
        CvProfile profile = CvProfile.create(UUID.randomUUID(), "Tanaka", "Dev", 3, TargetCompanyType.STARTUP);

        String result = section.render(profile);

        assertThat(result).doesNotContain("日本語レベル");
    }
}
```

- [ ] **Step 6: Run tests**

Run: `cd /c/Users/pierre.l.tchiengue/NihongoDev-Platform-backend && mvn test -pl . -Dtest="IntroSectionJPTest" -q`
Expected: 4 tests PASS

- [ ] **Step 7: Commit**

```bash
git add src/main/java/com/nihongodev/platform/application/service/generator/sections/IntroSectionJP.java \
        src/main/java/com/nihongodev/platform/application/service/generator/sections/ExperienceSectionJP.java \
        src/main/java/com/nihongodev/platform/application/service/generator/sections/MotivationSectionJP.java \
        src/main/java/com/nihongodev/platform/application/service/generator/sections/ClosingSectionJP.java \
        src/test/java/com/nihongodev/platform/application/service/generator/sections/IntroSectionJPTest.java
git commit -m "feat(bloc9): add Japanese pitch sections with tests"
```

---

## Task 9: Pitch Sections — Interview & Projects

**Files:**
- Create: `src/main/java/com/nihongodev/platform/application/service/generator/sections/InterviewOpeningSection.java`
- Create: `src/main/java/com/nihongodev/platform/application/service/generator/sections/ProjectHighlightsSection.java`

- [ ] **Step 1: Create InterviewOpeningSection**

```java
package com.nihongodev.platform.application.service.generator.sections;

import com.nihongodev.platform.application.service.generator.PitchSection;
import com.nihongodev.platform.domain.model.CvProfile;

public class InterviewOpeningSection implements PitchSection {

    @Override
    public String render(CvProfile profile) {
        StringBuilder sb = new StringBuilder();
        sb.append("## Interview Presentation\n\n");
        sb.append("*Structured self-introduction for a technical interview*\n\n");
        sb.append("Thank you for this opportunity. I'd like to take a moment to introduce myself.\n\n");
        sb.append("My name is **").append(profile.getFullName()).append("**");
        sb.append(" and I'm applying for the **").append(profile.getTargetRole()).append("** position.");
        sb.append(" I bring **").append(profile.getYearsOfExperience()).append(" years** of professional experience.");

        return sb.toString();
    }

    @Override
    public int order() { return 5; }
}
```

- [ ] **Step 2: Create ProjectHighlightsSection**

```java
package com.nihongodev.platform.application.service.generator.sections;

import com.nihongodev.platform.application.service.generator.PitchSection;
import com.nihongodev.platform.domain.model.CvProfile;

public class ProjectHighlightsSection implements PitchSection {

    @Override
    public String render(CvProfile profile) {
        if (profile.getNotableProjects() == null || profile.getNotableProjects().isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("## Notable Projects\n");
        for (String project : profile.getNotableProjects()) {
            sb.append("\n- ").append(project);
        }

        return sb.toString();
    }

    @Override
    public int order() { return 45; }
}
```

- [ ] **Step 3: Verify compilation**

Run: `cd /c/Users/pierre.l.tchiengue/NihongoDev-Platform-backend && mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/nihongodev/platform/application/service/generator/sections/InterviewOpeningSection.java \
        src/main/java/com/nihongodev/platform/application/service/generator/sections/ProjectHighlightsSection.java
git commit -m "feat(bloc9): add interview opening and project highlights sections"
```

---

## Task 10: Use Cases — CvProfile CRUD

**Files:**
- Create: `src/main/java/com/nihongodev/platform/application/usecase/CreateCvProfileUseCase.java`
- Create: `src/main/java/com/nihongodev/platform/application/usecase/UpdateCvProfileUseCase.java`
- Create: `src/main/java/com/nihongodev/platform/application/usecase/GetCvProfileUseCase.java`
- Test: `src/test/java/com/nihongodev/platform/application/usecase/CreateCvProfileUseCaseTest.java`

- [ ] **Step 1: Write CreateCvProfileUseCase test**

```java
package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.CreateCvProfileCommand;
import com.nihongodev.platform.application.dto.CvProfileDto;
import com.nihongodev.platform.application.port.out.CvProfileRepositoryPort;
import com.nihongodev.platform.domain.model.CvProfile;
import com.nihongodev.platform.domain.model.TargetCompanyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateCvProfileUseCase")
class CreateCvProfileUseCaseTest {

    @Mock private CvProfileRepositoryPort profileRepository;

    private CreateCvProfileUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateCvProfileUseCase(profileRepository);
    }

    @Test
    @DisplayName("should create CV profile successfully")
    void shouldCreateProfile() {
        UUID userId = UUID.randomUUID();
        CreateCvProfileCommand command = new CreateCvProfileCommand(
                "Pierre Tchiengue", "Java Dev", "Backend Engineer", 5,
                TargetCompanyType.STARTUP, List.of("Java", "Spring"),
                List.of(), List.of("JLPT N3"), List.of(), "Love Japan", "N3");

        when(profileRepository.existsByUserId(userId)).thenReturn(false);
        when(profileRepository.save(any(CvProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        CvProfileDto dto = useCase.create(userId, command);

        assertThat(dto.fullName()).isEqualTo("Pierre Tchiengue");
        assertThat(dto.targetRole()).isEqualTo("Backend Engineer");
        assertThat(dto.yearsOfExperience()).isEqualTo(5);
        assertThat(dto.targetCompanyType()).isEqualTo(TargetCompanyType.STARTUP);
        assertThat(dto.techStack()).containsExactly("Java", "Spring");
    }

    @Test
    @DisplayName("should fail if profile already exists")
    void shouldFailIfProfileExists() {
        UUID userId = UUID.randomUUID();
        CreateCvProfileCommand command = new CreateCvProfileCommand(
                "Pierre", null, "Dev", 3, TargetCompanyType.STARTUP,
                List.of(), List.of(), List.of(), List.of(), null, null);

        when(profileRepository.existsByUserId(userId)).thenReturn(true);

        assertThatThrownBy(() -> useCase.create(userId, command))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already exists");
    }
}
```

- [ ] **Step 2: Implement CreateCvProfileUseCase**

```java
package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.CreateCvProfileCommand;
import com.nihongodev.platform.application.dto.CvProfileDto;
import com.nihongodev.platform.application.dto.WorkExperienceDto;
import com.nihongodev.platform.application.port.in.CreateCvProfilePort;
import com.nihongodev.platform.application.port.out.CvProfileRepositoryPort;
import com.nihongodev.platform.domain.model.CvProfile;
import com.nihongodev.platform.domain.model.WorkExperience;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CreateCvProfileUseCase implements CreateCvProfilePort {

    private final CvProfileRepositoryPort profileRepository;

    public CreateCvProfileUseCase(CvProfileRepositoryPort profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    @Transactional
    public CvProfileDto create(UUID userId, CreateCvProfileCommand command) {
        if (profileRepository.existsByUserId(userId)) {
            throw new IllegalStateException("CV profile already exists for this user");
        }

        CvProfile profile = CvProfile.create(userId, command.fullName(), command.targetRole(),
                command.yearsOfExperience(), command.targetCompanyType());
        profile.setCurrentRole(command.currentRole());
        profile.setTechStack(command.techStack() != null ? command.techStack() : List.of());
        profile.setExperiences(mapExperiences(command.experiences()));
        profile.setCertifications(command.certifications() != null ? command.certifications() : List.of());
        profile.setNotableProjects(command.notableProjects() != null ? command.notableProjects() : List.of());
        profile.setMotivationJapan(command.motivationJapan());
        profile.setJapaneseLevel(command.japaneseLevel());

        CvProfile saved = profileRepository.save(profile);
        return mapToDto(saved);
    }

    private List<WorkExperience> mapExperiences(List<CreateCvProfileCommand.WorkExperienceData> data) {
        if (data == null) return List.of();
        return data.stream()
                .map(d -> new WorkExperience(d.company(), d.role(), d.durationMonths(), d.highlights()))
                .toList();
    }

    static CvProfileDto mapToDto(CvProfile p) {
        List<WorkExperienceDto> expDtos = p.getExperiences() != null
                ? p.getExperiences().stream()
                    .map(e -> new WorkExperienceDto(e.getCompany(), e.getRole(), e.getDurationMonths(), e.getHighlights()))
                    .toList()
                : List.of();

        return new CvProfileDto(
                p.getId(), p.getUserId(), p.getFullName(), p.getCurrentRole(), p.getTargetRole(),
                p.getYearsOfExperience(), p.getTargetCompanyType(), p.getTechStack(),
                expDtos, p.getCertifications(), p.getNotableProjects(),
                p.getMotivationJapan(), p.getJapaneseLevel(), p.getCreatedAt(), p.getUpdatedAt());
    }
}
```

- [ ] **Step 3: Implement UpdateCvProfileUseCase**

```java
package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.CreateCvProfileCommand;
import com.nihongodev.platform.application.command.UpdateCvProfileCommand;
import com.nihongodev.platform.application.dto.CvProfileDto;
import com.nihongodev.platform.application.port.in.UpdateCvProfilePort;
import com.nihongodev.platform.application.port.out.CvProfileRepositoryPort;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.CvProfile;
import com.nihongodev.platform.domain.model.WorkExperience;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UpdateCvProfileUseCase implements UpdateCvProfilePort {

    private final CvProfileRepositoryPort profileRepository;

    public UpdateCvProfileUseCase(CvProfileRepositoryPort profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    @Transactional
    public CvProfileDto update(UUID userId, UpdateCvProfileCommand command) {
        CvProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("CV profile not found"));

        if (command.fullName() != null) profile.setFullName(command.fullName());
        if (command.currentRole() != null) profile.setCurrentRole(command.currentRole());
        if (command.targetRole() != null) profile.setTargetRole(command.targetRole());
        if (command.yearsOfExperience() != null) profile.setYearsOfExperience(command.yearsOfExperience());
        if (command.targetCompanyType() != null) profile.setTargetCompanyType(command.targetCompanyType());
        if (command.techStack() != null) profile.setTechStack(command.techStack());
        if (command.experiences() != null) profile.setExperiences(mapExperiences(command.experiences()));
        if (command.certifications() != null) profile.setCertifications(command.certifications());
        if (command.notableProjects() != null) profile.setNotableProjects(command.notableProjects());
        if (command.motivationJapan() != null) profile.setMotivationJapan(command.motivationJapan());
        if (command.japaneseLevel() != null) profile.setJapaneseLevel(command.japaneseLevel());
        profile.setUpdatedAt(LocalDateTime.now());

        CvProfile saved = profileRepository.save(profile);
        return CreateCvProfileUseCase.mapToDto(saved);
    }

    private List<WorkExperience> mapExperiences(List<CreateCvProfileCommand.WorkExperienceData> data) {
        if (data == null) return List.of();
        return data.stream()
                .map(d -> new WorkExperience(d.company(), d.role(), d.durationMonths(), d.highlights()))
                .toList();
    }
}
```

- [ ] **Step 4: Implement GetCvProfileUseCase**

```java
package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.CvProfileDto;
import com.nihongodev.platform.application.port.in.GetCvProfilePort;
import com.nihongodev.platform.application.port.out.CvProfileRepositoryPort;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.CvProfile;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetCvProfileUseCase implements GetCvProfilePort {

    private final CvProfileRepositoryPort profileRepository;

    public GetCvProfileUseCase(CvProfileRepositoryPort profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public CvProfileDto get(UUID userId) {
        CvProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("CV profile not found"));
        return CreateCvProfileUseCase.mapToDto(profile);
    }
}
```

- [ ] **Step 5: Run tests**

Run: `cd /c/Users/pierre.l.tchiengue/NihongoDev-Platform-backend && mvn test -pl . -Dtest=CreateCvProfileUseCaseTest -q`
Expected: 2 tests PASS

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/nihongodev/platform/application/usecase/CreateCvProfileUseCase.java \
        src/main/java/com/nihongodev/platform/application/usecase/UpdateCvProfileUseCase.java \
        src/main/java/com/nihongodev/platform/application/usecase/GetCvProfileUseCase.java \
        src/test/java/com/nihongodev/platform/application/usecase/CreateCvProfileUseCaseTest.java
git commit -m "feat(bloc9): add CvProfile CRUD use cases with tests"
```

---

## Task 11: Use Cases — GeneratePitch & Export

**Files:**
- Create: `src/main/java/com/nihongodev/platform/application/usecase/GeneratePitchUseCase.java`
- Create: `src/main/java/com/nihongodev/platform/application/usecase/GetPitchHistoryUseCase.java`
- Create: `src/main/java/com/nihongodev/platform/application/usecase/ExportPitchUseCase.java`
- Test: `src/test/java/com/nihongodev/platform/application/usecase/GeneratePitchUseCaseTest.java`
- Test: `src/test/java/com/nihongodev/platform/application/usecase/ExportPitchUseCaseTest.java`

- [ ] **Step 1: Write GeneratePitchUseCase test**

```java
package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.GeneratePitchCommand;
import com.nihongodev.platform.application.dto.GeneratedPitchDto;
import com.nihongodev.platform.application.port.out.CvProfileRepositoryPort;
import com.nihongodev.platform.application.port.out.EventPublisherPort;
import com.nihongodev.platform.application.port.out.GeneratedPitchRepositoryPort;
import com.nihongodev.platform.application.service.generator.PitchAssembler;
import com.nihongodev.platform.domain.event.PitchGeneratedEvent;
import com.nihongodev.platform.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GeneratePitchUseCase")
class GeneratePitchUseCaseTest {

    @Mock private CvProfileRepositoryPort profileRepository;
    @Mock private GeneratedPitchRepositoryPort pitchRepository;
    @Mock private EventPublisherPort eventPublisher;
    @Mock private PitchAssembler pitchAssembler;

    private GeneratePitchUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GeneratePitchUseCase(profileRepository, pitchRepository, eventPublisher, pitchAssembler);
    }

    @Test
    @DisplayName("should generate pitch and save")
    void shouldGeneratePitchAndSave() {
        UUID userId = UUID.randomUUID();
        CvProfile profile = CvProfile.create(userId, "Pierre", "Backend", 5, TargetCompanyType.STARTUP);
        GeneratePitchCommand command = new GeneratePitchCommand(PitchType.ENGLISH_PITCH);

        when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(pitchAssembler.assemble(profile, PitchType.ENGLISH_PITCH)).thenReturn("## Intro\n\nHello");
        when(pitchRepository.save(any(GeneratedPitch.class))).thenAnswer(inv -> inv.getArgument(0));

        GeneratedPitchDto dto = useCase.generate(userId, command);

        assertThat(dto.pitchType()).isEqualTo(PitchType.ENGLISH_PITCH);
        assertThat(dto.content()).isEqualTo("## Intro\n\nHello");
    }

    @Test
    @DisplayName("should publish PitchGeneratedEvent")
    void shouldPublishEvent() {
        UUID userId = UUID.randomUUID();
        CvProfile profile = CvProfile.create(userId, "Pierre", "Backend", 5, TargetCompanyType.STARTUP);
        GeneratePitchCommand command = new GeneratePitchCommand(PitchType.JAPANESE_PITCH);

        when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
        when(pitchAssembler.assemble(any(), any())).thenReturn("Content");
        when(pitchRepository.save(any(GeneratedPitch.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.generate(userId, command);

        verify(eventPublisher).publish(eq("cv-generator-events"), any(PitchGeneratedEvent.class));
    }

    @Test
    @DisplayName("should fail if no profile exists")
    void shouldFailIfNoProfile() {
        UUID userId = UUID.randomUUID();
        GeneratePitchCommand command = new GeneratePitchCommand(PitchType.ENGLISH_PITCH);

        when(profileRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.generate(userId, command))
                .isInstanceOf(com.nihongodev.platform.domain.exception.ResourceNotFoundException.class);
    }
}
```

- [ ] **Step 2: Implement GeneratePitchUseCase**

```java
package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.command.GeneratePitchCommand;
import com.nihongodev.platform.application.dto.GeneratedPitchDto;
import com.nihongodev.platform.application.port.in.GeneratePitchPort;
import com.nihongodev.platform.application.port.out.CvProfileRepositoryPort;
import com.nihongodev.platform.application.port.out.EventPublisherPort;
import com.nihongodev.platform.application.port.out.GeneratedPitchRepositoryPort;
import com.nihongodev.platform.application.service.generator.PitchAssembler;
import com.nihongodev.platform.domain.event.PitchGeneratedEvent;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.CvProfile;
import com.nihongodev.platform.domain.model.GeneratedPitch;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GeneratePitchUseCase implements GeneratePitchPort {

    private final CvProfileRepositoryPort profileRepository;
    private final GeneratedPitchRepositoryPort pitchRepository;
    private final EventPublisherPort eventPublisher;
    private final PitchAssembler pitchAssembler;

    public GeneratePitchUseCase(CvProfileRepositoryPort profileRepository,
                                GeneratedPitchRepositoryPort pitchRepository,
                                EventPublisherPort eventPublisher,
                                PitchAssembler pitchAssembler) {
        this.profileRepository = profileRepository;
        this.pitchRepository = pitchRepository;
        this.eventPublisher = eventPublisher;
        this.pitchAssembler = pitchAssembler;
    }

    @Override
    @Transactional
    public GeneratedPitchDto generate(UUID userId, GeneratePitchCommand command) {
        CvProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("CV profile not found. Create a profile first."));

        String content = pitchAssembler.assemble(profile, command.pitchType());

        GeneratedPitch pitch = GeneratedPitch.create(userId, command.pitchType(), content, profile.getId());
        GeneratedPitch saved = pitchRepository.save(pitch);

        eventPublisher.publish("cv-generator-events",
                PitchGeneratedEvent.of(userId, saved.getId(), command.pitchType()));

        return new GeneratedPitchDto(saved.getId(), saved.getPitchType(), saved.getContent(), saved.getGeneratedAt());
    }
}
```

- [ ] **Step 3: Implement GetPitchHistoryUseCase**

```java
package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.dto.GeneratedPitchDto;
import com.nihongodev.platform.application.port.in.GetPitchHistoryPort;
import com.nihongodev.platform.application.port.out.GeneratedPitchRepositoryPort;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.model.PitchType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GetPitchHistoryUseCase implements GetPitchHistoryPort {

    private final GeneratedPitchRepositoryPort pitchRepository;

    public GetPitchHistoryUseCase(GeneratedPitchRepositoryPort pitchRepository) {
        this.pitchRepository = pitchRepository;
    }

    @Override
    public List<GeneratedPitchDto> getHistory(UUID userId, PitchType type) {
        return pitchRepository.findByUserIdAndPitchType(userId, type).stream()
                .map(p -> new GeneratedPitchDto(p.getId(), p.getPitchType(), p.getContent(), p.getGeneratedAt()))
                .toList();
    }

    @Override
    public GeneratedPitchDto getLatest(UUID userId, PitchType type) {
        return pitchRepository.findLatestByUserIdAndPitchType(userId, type)
                .map(p -> new GeneratedPitchDto(p.getId(), p.getPitchType(), p.getContent(), p.getGeneratedAt()))
                .orElseThrow(() -> new ResourceNotFoundException("No pitch found for type: " + type));
    }
}
```

- [ ] **Step 4: Write ExportPitchUseCase test**

```java
package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.port.out.GeneratedPitchRepositoryPort;
import com.nihongodev.platform.domain.model.ExportFormat;
import com.nihongodev.platform.domain.model.GeneratedPitch;
import com.nihongodev.platform.domain.model.PitchType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExportPitchUseCase")
class ExportPitchUseCaseTest {

    @Mock private GeneratedPitchRepositoryPort pitchRepository;

    private ExportPitchUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ExportPitchUseCase(pitchRepository);
    }

    @Test
    @DisplayName("should export as markdown unchanged")
    void shouldExportMarkdown() {
        UUID userId = UUID.randomUUID();
        UUID pitchId = UUID.randomUUID();
        GeneratedPitch pitch = GeneratedPitch.create(userId, PitchType.ENGLISH_PITCH,
                "## Title\n\n**Bold** text\n\n- Item 1", UUID.randomUUID());
        pitch.setId(pitchId);

        when(pitchRepository.findById(pitchId)).thenReturn(Optional.of(pitch));

        String result = useCase.export(userId, pitchId, ExportFormat.MARKDOWN);

        assertThat(result).isEqualTo("## Title\n\n**Bold** text\n\n- Item 1");
    }

    @Test
    @DisplayName("should export as plain text with markdown stripped")
    void shouldExportPlainText() {
        UUID userId = UUID.randomUUID();
        UUID pitchId = UUID.randomUUID();
        GeneratedPitch pitch = GeneratedPitch.create(userId, PitchType.ENGLISH_PITCH,
                "## Title\n\n**Bold** text\n\n- Item 1\n- Item 2", UUID.randomUUID());
        pitch.setId(pitchId);

        when(pitchRepository.findById(pitchId)).thenReturn(Optional.of(pitch));

        String result = useCase.export(userId, pitchId, ExportFormat.PLAIN_TEXT);

        assertThat(result).doesNotContain("##");
        assertThat(result).doesNotContain("**");
        assertThat(result).contains("Title");
        assertThat(result).contains("Bold text");
        assertThat(result).contains("Item 1");
    }

    @Test
    @DisplayName("should fail if pitch belongs to another user")
    void shouldFailIfNotOwner() {
        UUID userId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        UUID pitchId = UUID.randomUUID();
        GeneratedPitch pitch = GeneratedPitch.create(otherUserId, PitchType.ENGLISH_PITCH, "content", UUID.randomUUID());
        pitch.setId(pitchId);

        when(pitchRepository.findById(pitchId)).thenReturn(Optional.of(pitch));

        assertThatThrownBy(() -> useCase.export(userId, pitchId, ExportFormat.MARKDOWN))
                .isInstanceOf(com.nihongodev.platform.domain.exception.UnauthorizedException.class);
    }
}
```

- [ ] **Step 5: Implement ExportPitchUseCase**

```java
package com.nihongodev.platform.application.usecase;

import com.nihongodev.platform.application.port.in.ExportPitchPort;
import com.nihongodev.platform.application.port.out.GeneratedPitchRepositoryPort;
import com.nihongodev.platform.domain.exception.ResourceNotFoundException;
import com.nihongodev.platform.domain.exception.UnauthorizedException;
import com.nihongodev.platform.domain.model.ExportFormat;
import com.nihongodev.platform.domain.model.GeneratedPitch;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ExportPitchUseCase implements ExportPitchPort {

    private final GeneratedPitchRepositoryPort pitchRepository;

    public ExportPitchUseCase(GeneratedPitchRepositoryPort pitchRepository) {
        this.pitchRepository = pitchRepository;
    }

    @Override
    public String export(UUID userId, UUID pitchId, ExportFormat format) {
        GeneratedPitch pitch = pitchRepository.findById(pitchId)
                .orElseThrow(() -> new ResourceNotFoundException("Pitch not found"));

        if (!pitch.getUserId().equals(userId)) {
            throw new UnauthorizedException("You do not own this pitch");
        }

        return switch (format) {
            case MARKDOWN -> pitch.getContent();
            case PLAIN_TEXT -> stripMarkdown(pitch.getContent());
        };
    }

    private String stripMarkdown(String markdown) {
        return markdown
                .replaceAll("^#{1,6}\\s+", "")
                .replaceAll("(?m)^#{1,6}\\s+", "")
                .replaceAll("\\*\\*(.+?)\\*\\*", "$1")
                .replaceAll("\\*(.+?)\\*", "$1")
                .replaceAll("(?m)^- ", "");
    }
}
```

- [ ] **Step 6: Run tests**

Run: `cd /c/Users/pierre.l.tchiengue/NihongoDev-Platform-backend && mvn test -pl . -Dtest="GeneratePitchUseCaseTest,ExportPitchUseCaseTest" -q`
Expected: 6 tests PASS

- [ ] **Step 7: Commit**

```bash
git add src/main/java/com/nihongodev/platform/application/usecase/GeneratePitchUseCase.java \
        src/main/java/com/nihongodev/platform/application/usecase/GetPitchHistoryUseCase.java \
        src/main/java/com/nihongodev/platform/application/usecase/ExportPitchUseCase.java \
        src/test/java/com/nihongodev/platform/application/usecase/GeneratePitchUseCaseTest.java \
        src/test/java/com/nihongodev/platform/application/usecase/ExportPitchUseCaseTest.java
git commit -m "feat(bloc9): add GeneratePitch, GetPitchHistory, and ExportPitch use cases with tests"
```

---

## Task 12: Flyway Migration V8

**Files:**
- Create: `src/main/resources/db/migration/V8__cv_generator.sql`

- [ ] **Step 1: Create V8 migration**

```sql
-- V8: CV Generator Module
-- Tables: cv_profiles, generated_pitches

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

- [ ] **Step 2: Commit**

```bash
git add src/main/resources/db/migration/V8__cv_generator.sql
git commit -m "feat(bloc9): add V8 Flyway migration for cv_profiles and generated_pitches"
```

---

## Task 13: JPA Entities

**Files:**
- Create: `src/main/java/com/nihongodev/platform/infrastructure/persistence/entity/CvProfileEntity.java`
- Create: `src/main/java/com/nihongodev/platform/infrastructure/persistence/entity/GeneratedPitchEntity.java`

- [ ] **Step 1: Create CvProfileEntity**

```java
package com.nihongodev.platform.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cv_profiles")
public class CvProfileEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    @Column(name = "current_role", length = 200)
    private String currentRole;

    @Column(name = "target_role", nullable = false, length = 200)
    private String targetRole;

    @Column(name = "years_of_experience", nullable = false)
    private int yearsOfExperience;

    @Column(name = "target_company_type", nullable = false, length = 50)
    private String targetCompanyType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tech_stack", nullable = false, columnDefinition = "jsonb")
    private String techStack;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "experiences", nullable = false, columnDefinition = "jsonb")
    private String experiences;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "certifications", nullable = false, columnDefinition = "jsonb")
    private String certifications;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "notable_projects", nullable = false, columnDefinition = "jsonb")
    private String notableProjects;

    @Column(name = "motivation_japan", columnDefinition = "TEXT")
    private String motivationJapan;

    @Column(name = "japanese_level", length = 10)
    private String japaneseLevel;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getCurrentRole() { return currentRole; }
    public void setCurrentRole(String currentRole) { this.currentRole = currentRole; }
    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }
    public int getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(int yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }
    public String getTargetCompanyType() { return targetCompanyType; }
    public void setTargetCompanyType(String targetCompanyType) { this.targetCompanyType = targetCompanyType; }
    public String getTechStack() { return techStack; }
    public void setTechStack(String techStack) { this.techStack = techStack; }
    public String getExperiences() { return experiences; }
    public void setExperiences(String experiences) { this.experiences = experiences; }
    public String getCertifications() { return certifications; }
    public void setCertifications(String certifications) { this.certifications = certifications; }
    public String getNotableProjects() { return notableProjects; }
    public void setNotableProjects(String notableProjects) { this.notableProjects = notableProjects; }
    public String getMotivationJapan() { return motivationJapan; }
    public void setMotivationJapan(String motivationJapan) { this.motivationJapan = motivationJapan; }
    public String getJapaneseLevel() { return japaneseLevel; }
    public void setJapaneseLevel(String japaneseLevel) { this.japaneseLevel = japaneseLevel; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
```

- [ ] **Step 2: Create GeneratedPitchEntity**

```java
package com.nihongodev.platform.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "generated_pitches")
public class GeneratedPitchEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "pitch_type", nullable = false, length = 50)
    private String pitchType;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "profile_snapshot_id")
    private UUID profileSnapshotId;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    @PrePersist
    void prePersist() {
        if (generatedAt == null) generatedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getPitchType() { return pitchType; }
    public void setPitchType(String pitchType) { this.pitchType = pitchType; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public UUID getProfileSnapshotId() { return profileSnapshotId; }
    public void setProfileSnapshotId(UUID profileSnapshotId) { this.profileSnapshotId = profileSnapshotId; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
}
```

- [ ] **Step 3: Verify compilation**

Run: `cd /c/Users/pierre.l.tchiengue/NihongoDev-Platform-backend && mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/nihongodev/platform/infrastructure/persistence/entity/CvProfileEntity.java \
        src/main/java/com/nihongodev/platform/infrastructure/persistence/entity/GeneratedPitchEntity.java
git commit -m "feat(bloc9): add JPA entities for CvProfile and GeneratedPitch"
```

---

## Task 14: JPA Repositories, Mappers & Adapters

**Files:**
- Create: `src/main/java/com/nihongodev/platform/infrastructure/persistence/repository/JpaCvProfileRepository.java`
- Create: `src/main/java/com/nihongodev/platform/infrastructure/persistence/repository/JpaGeneratedPitchRepository.java`
- Create: `src/main/java/com/nihongodev/platform/infrastructure/persistence/mapper/CvProfilePersistenceMapper.java`
- Create: `src/main/java/com/nihongodev/platform/infrastructure/persistence/mapper/GeneratedPitchPersistenceMapper.java`
- Create: `src/main/java/com/nihongodev/platform/infrastructure/persistence/adapter/CvProfileRepositoryAdapter.java`
- Create: `src/main/java/com/nihongodev/platform/infrastructure/persistence/adapter/GeneratedPitchRepositoryAdapter.java`

- [ ] **Step 1: Create JPA repositories**

`JpaCvProfileRepository.java`:
```java
package com.nihongodev.platform.infrastructure.persistence.repository;

import com.nihongodev.platform.infrastructure.persistence.entity.CvProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaCvProfileRepository extends JpaRepository<CvProfileEntity, UUID> {
    Optional<CvProfileEntity> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
}
```

`JpaGeneratedPitchRepository.java`:
```java
package com.nihongodev.platform.infrastructure.persistence.repository;

import com.nihongodev.platform.infrastructure.persistence.entity.GeneratedPitchEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaGeneratedPitchRepository extends JpaRepository<GeneratedPitchEntity, UUID> {
    List<GeneratedPitchEntity> findByUserIdAndPitchTypeOrderByGeneratedAtDesc(UUID userId, String pitchType);
    Optional<GeneratedPitchEntity> findFirstByUserIdAndPitchTypeOrderByGeneratedAtDesc(UUID userId, String pitchType);
}
```

- [ ] **Step 2: Create CvProfilePersistenceMapper**

```java
package com.nihongodev.platform.infrastructure.persistence.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nihongodev.platform.domain.model.CvProfile;
import com.nihongodev.platform.domain.model.TargetCompanyType;
import com.nihongodev.platform.domain.model.WorkExperience;
import com.nihongodev.platform.infrastructure.persistence.entity.CvProfileEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CvProfilePersistenceMapper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public CvProfile toDomain(CvProfileEntity entity) {
        if (entity == null) return null;
        CvProfile p = new CvProfile();
        p.setId(entity.getId());
        p.setUserId(entity.getUserId());
        p.setFullName(entity.getFullName());
        p.setCurrentRole(entity.getCurrentRole());
        p.setTargetRole(entity.getTargetRole());
        p.setYearsOfExperience(entity.getYearsOfExperience());
        p.setTargetCompanyType(TargetCompanyType.valueOf(entity.getTargetCompanyType()));
        p.setTechStack(deserializeStringList(entity.getTechStack()));
        p.setExperiences(deserializeExperiences(entity.getExperiences()));
        p.setCertifications(deserializeStringList(entity.getCertifications()));
        p.setNotableProjects(deserializeStringList(entity.getNotableProjects()));
        p.setMotivationJapan(entity.getMotivationJapan());
        p.setJapaneseLevel(entity.getJapaneseLevel());
        p.setCreatedAt(entity.getCreatedAt());
        p.setUpdatedAt(entity.getUpdatedAt());
        return p;
    }

    public CvProfileEntity toEntity(CvProfile p) {
        if (p == null) return null;
        CvProfileEntity entity = new CvProfileEntity();
        entity.setId(p.getId());
        entity.setUserId(p.getUserId());
        entity.setFullName(p.getFullName());
        entity.setCurrentRole(p.getCurrentRole());
        entity.setTargetRole(p.getTargetRole());
        entity.setYearsOfExperience(p.getYearsOfExperience());
        entity.setTargetCompanyType(p.getTargetCompanyType().name());
        entity.setTechStack(serializeList(p.getTechStack()));
        entity.setExperiences(serializeList(p.getExperiences()));
        entity.setCertifications(serializeList(p.getCertifications()));
        entity.setNotableProjects(serializeList(p.getNotableProjects()));
        entity.setMotivationJapan(p.getMotivationJapan());
        entity.setJapaneseLevel(p.getJapaneseLevel());
        entity.setCreatedAt(p.getCreatedAt());
        entity.setUpdatedAt(p.getUpdatedAt());
        return entity;
    }

    private String serializeList(Object list) {
        try { return objectMapper.writeValueAsString(list != null ? list : List.of()); }
        catch (Exception e) { return "[]"; }
    }

    private List<String> deserializeStringList(String json) {
        try { return objectMapper.readValue(json != null ? json : "[]", new TypeReference<>() {}); }
        catch (Exception e) { return new ArrayList<>(); }
    }

    private List<WorkExperience> deserializeExperiences(String json) {
        try { return objectMapper.readValue(json != null ? json : "[]", new TypeReference<>() {}); }
        catch (Exception e) { return new ArrayList<>(); }
    }
}
```

- [ ] **Step 3: Create GeneratedPitchPersistenceMapper**

```java
package com.nihongodev.platform.infrastructure.persistence.mapper;

import com.nihongodev.platform.domain.model.GeneratedPitch;
import com.nihongodev.platform.domain.model.PitchType;
import com.nihongodev.platform.infrastructure.persistence.entity.GeneratedPitchEntity;
import org.springframework.stereotype.Component;

@Component
public class GeneratedPitchPersistenceMapper {

    public GeneratedPitch toDomain(GeneratedPitchEntity entity) {
        if (entity == null) return null;
        GeneratedPitch p = new GeneratedPitch();
        p.setId(entity.getId());
        p.setUserId(entity.getUserId());
        p.setPitchType(PitchType.valueOf(entity.getPitchType()));
        p.setContent(entity.getContent());
        p.setProfileSnapshotId(entity.getProfileSnapshotId());
        p.setGeneratedAt(entity.getGeneratedAt());
        return p;
    }

    public GeneratedPitchEntity toEntity(GeneratedPitch p) {
        if (p == null) return null;
        GeneratedPitchEntity entity = new GeneratedPitchEntity();
        entity.setId(p.getId());
        entity.setUserId(p.getUserId());
        entity.setPitchType(p.getPitchType().name());
        entity.setContent(p.getContent());
        entity.setProfileSnapshotId(p.getProfileSnapshotId());
        entity.setGeneratedAt(p.getGeneratedAt());
        return entity;
    }
}
```

- [ ] **Step 4: Create repository adapters**

`CvProfileRepositoryAdapter.java`:
```java
package com.nihongodev.platform.infrastructure.persistence.adapter;

import com.nihongodev.platform.application.port.out.CvProfileRepositoryPort;
import com.nihongodev.platform.domain.model.CvProfile;
import com.nihongodev.platform.infrastructure.persistence.mapper.CvProfilePersistenceMapper;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaCvProfileRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CvProfileRepositoryAdapter implements CvProfileRepositoryPort {

    private final JpaCvProfileRepository repository;
    private final CvProfilePersistenceMapper mapper;

    public CvProfileRepositoryAdapter(JpaCvProfileRepository repository, CvProfilePersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public CvProfile save(CvProfile profile) {
        var entity = mapper.toEntity(profile);
        var saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<CvProfile> findByUserId(UUID userId) {
        return repository.findByUserId(userId).map(mapper::toDomain);
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return repository.existsByUserId(userId);
    }
}
```

`GeneratedPitchRepositoryAdapter.java`:
```java
package com.nihongodev.platform.infrastructure.persistence.adapter;

import com.nihongodev.platform.application.port.out.GeneratedPitchRepositoryPort;
import com.nihongodev.platform.domain.model.GeneratedPitch;
import com.nihongodev.platform.domain.model.PitchType;
import com.nihongodev.platform.infrastructure.persistence.mapper.GeneratedPitchPersistenceMapper;
import com.nihongodev.platform.infrastructure.persistence.repository.JpaGeneratedPitchRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class GeneratedPitchRepositoryAdapter implements GeneratedPitchRepositoryPort {

    private final JpaGeneratedPitchRepository repository;
    private final GeneratedPitchPersistenceMapper mapper;

    public GeneratedPitchRepositoryAdapter(JpaGeneratedPitchRepository repository,
                                           GeneratedPitchPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public GeneratedPitch save(GeneratedPitch pitch) {
        var entity = mapper.toEntity(pitch);
        var saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<GeneratedPitch> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<GeneratedPitch> findByUserIdAndPitchType(UUID userId, PitchType pitchType) {
        return repository.findByUserIdAndPitchTypeOrderByGeneratedAtDesc(userId, pitchType.name()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<GeneratedPitch> findLatestByUserIdAndPitchType(UUID userId, PitchType pitchType) {
        return repository.findFirstByUserIdAndPitchTypeOrderByGeneratedAtDesc(userId, pitchType.name())
                .map(mapper::toDomain);
    }
}
```

- [ ] **Step 5: Verify compilation**

Run: `cd /c/Users/pierre.l.tchiengue/NihongoDev-Platform-backend && mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/nihongodev/platform/infrastructure/persistence/repository/JpaCvProfileRepository.java \
        src/main/java/com/nihongodev/platform/infrastructure/persistence/repository/JpaGeneratedPitchRepository.java \
        src/main/java/com/nihongodev/platform/infrastructure/persistence/mapper/CvProfilePersistenceMapper.java \
        src/main/java/com/nihongodev/platform/infrastructure/persistence/mapper/GeneratedPitchPersistenceMapper.java \
        src/main/java/com/nihongodev/platform/infrastructure/persistence/adapter/CvProfileRepositoryAdapter.java \
        src/main/java/com/nihongodev/platform/infrastructure/persistence/adapter/GeneratedPitchRepositoryAdapter.java
git commit -m "feat(bloc9): add JPA repositories, mappers, and adapters"
```

---

## Task 15: Kafka & Config Integration

**Files:**
- Modify: `src/main/java/com/nihongodev/platform/infrastructure/config/KafkaTopicsProperties.java`
- Modify: `src/main/java/com/nihongodev/platform/infrastructure/kafka/KafkaEventPublisherAdapter.java`
- Modify: `src/main/resources/application.yml`

- [ ] **Step 1: Add cvGeneratorEvents to KafkaTopicsProperties**

Add the field after `notificationEvents`:
```java
private TopicDef cvGeneratorEvents = new TopicDef();

public TopicDef getCvGeneratorEvents() { return cvGeneratorEvents; }
public void setCvGeneratorEvents(TopicDef cvGeneratorEvents) { this.cvGeneratorEvents = cvGeneratorEvents; }
```

- [ ] **Step 2: Add cv-generator-events to KafkaEventPublisherAdapter registry**

Add to the `buildTopicRegistry` method:
```java
registry.put("cv-generator-events", props.getCvGeneratorEvents().getName());
```

- [ ] **Step 3: Add cv-generator-events to application.yml**

Add after `notification-events` topic section:
```yaml
      cv-generator-events:
        name: cv-generator-events
        partitions: 3
        replicas: 1
```

- [ ] **Step 4: Verify compilation**

Run: `cd /c/Users/pierre.l.tchiengue/NihongoDev-Platform-backend && mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/nihongodev/platform/infrastructure/config/KafkaTopicsProperties.java \
        src/main/java/com/nihongodev/platform/infrastructure/kafka/KafkaEventPublisherAdapter.java \
        src/main/resources/application.yml
git commit -m "feat(bloc9): add cv-generator-events Kafka topic configuration"
```

---

## Task 16: PitchAssembler Spring Configuration

**Files:**
- Create: `src/main/java/com/nihongodev/platform/infrastructure/config/PitchAssemblerConfig.java`

- [ ] **Step 1: Create PitchAssembler Spring config**

```java
package com.nihongodev.platform.infrastructure.config;

import com.nihongodev.platform.application.service.generator.PitchAssembler;
import com.nihongodev.platform.application.service.generator.PitchSection;
import com.nihongodev.platform.application.service.generator.sections.*;
import com.nihongodev.platform.domain.model.PitchType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
public class PitchAssemblerConfig {

    @Bean
    public PitchAssembler pitchAssembler() {
        IntroSectionEN introEN = new IntroSectionEN();
        IntroSectionJP introJP = new IntroSectionJP();
        ExperienceSectionEN experienceEN = new ExperienceSectionEN();
        ExperienceSectionJP experienceJP = new ExperienceSectionJP();
        TechStackSection techStack = new TechStackSection();
        MotivationSectionEN motivationEN = new MotivationSectionEN();
        MotivationSectionJP motivationJP = new MotivationSectionJP();
        CertificationsSection certifications = new CertificationsSection();
        ClosingSectionEN closingEN = new ClosingSectionEN();
        ClosingSectionJP closingJP = new ClosingSectionJP();
        InterviewOpeningSection interviewOpening = new InterviewOpeningSection();
        ProjectHighlightsSection projectHighlights = new ProjectHighlightsSection();

        Map<PitchType, List<PitchSection>> registry = Map.of(
                PitchType.ENGLISH_PITCH, List.of(
                        introEN, experienceEN, techStack, motivationEN, certifications, closingEN),
                PitchType.JAPANESE_PITCH, List.of(
                        introJP, experienceJP, techStack, motivationJP, certifications, closingJP),
                PitchType.INTERVIEW_PRESENTATION, List.of(
                        interviewOpening, introEN, experienceEN, motivationEN, projectHighlights, certifications, closingEN),
                PitchType.DEVELOPER_SUMMARY, List.of(
                        introEN, experienceEN, techStack, projectHighlights, certifications)
        );

        return new PitchAssembler(registry);
    }
}
```

- [ ] **Step 2: Verify compilation**

Run: `cd /c/Users/pierre.l.tchiengue/NihongoDev-Platform-backend && mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/nihongodev/platform/infrastructure/config/PitchAssemblerConfig.java
git commit -m "feat(bloc9): add PitchAssembler Spring configuration with section registry"
```

---

## Task 17: REST Controller & Request DTOs

**Files:**
- Create: `src/main/java/com/nihongodev/platform/infrastructure/web/request/CreateCvProfileRequest.java`
- Create: `src/main/java/com/nihongodev/platform/infrastructure/web/request/UpdateCvProfileRequest.java`
- Create: `src/main/java/com/nihongodev/platform/infrastructure/web/request/GeneratePitchRequest.java`
- Create: `src/main/java/com/nihongodev/platform/infrastructure/web/controller/CvGeneratorController.java`

- [ ] **Step 1: Create request DTOs**

`CreateCvProfileRequest.java`:
```java
package com.nihongodev.platform.infrastructure.web.request;

import com.nihongodev.platform.domain.model.TargetCompanyType;
import jakarta.validation.constraints.*;

import java.util.List;

public record CreateCvProfileRequest(
        @NotBlank @Size(max = 200) String fullName,
        @Size(max = 200) String currentRole,
        @NotBlank @Size(max = 200) String targetRole,
        @Min(0) @Max(50) int yearsOfExperience,
        @NotNull TargetCompanyType targetCompanyType,
        @Size(max = 20) List<@Size(max = 50) String> techStack,
        @Size(max = 10) List<WorkExperienceData> experiences,
        @Size(max = 15) List<String> certifications,
        @Size(max = 5) List<String> notableProjects,
        @Size(max = 2000) String motivationJapan,
        String japaneseLevel
) {
    public record WorkExperienceData(
            @NotBlank String company,
            @NotBlank String role,
            @Min(1) int durationMonths,
            List<String> highlights
    ) {}
}
```

`UpdateCvProfileRequest.java`:
```java
package com.nihongodev.platform.infrastructure.web.request;

import com.nihongodev.platform.domain.model.TargetCompanyType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateCvProfileRequest(
        @Size(max = 200) String fullName,
        @Size(max = 200) String currentRole,
        @Size(max = 200) String targetRole,
        @Min(0) @Max(50) Integer yearsOfExperience,
        TargetCompanyType targetCompanyType,
        @Size(max = 20) List<@Size(max = 50) String> techStack,
        @Size(max = 10) List<CreateCvProfileRequest.WorkExperienceData> experiences,
        @Size(max = 15) List<String> certifications,
        @Size(max = 5) List<String> notableProjects,
        @Size(max = 2000) String motivationJapan,
        String japaneseLevel
) {}
```

`GeneratePitchRequest.java`:
```java
package com.nihongodev.platform.infrastructure.web.request;

import com.nihongodev.platform.domain.model.PitchType;
import jakarta.validation.constraints.NotNull;

public record GeneratePitchRequest(
        @NotNull PitchType pitchType
) {}
```

- [ ] **Step 2: Create CvGeneratorController**

```java
package com.nihongodev.platform.infrastructure.web.controller;

import com.nihongodev.platform.application.command.CreateCvProfileCommand;
import com.nihongodev.platform.application.command.GeneratePitchCommand;
import com.nihongodev.platform.application.command.UpdateCvProfileCommand;
import com.nihongodev.platform.application.dto.CvProfileDto;
import com.nihongodev.platform.application.dto.GeneratedPitchDto;
import com.nihongodev.platform.application.port.in.*;
import com.nihongodev.platform.domain.model.ExportFormat;
import com.nihongodev.platform.domain.model.PitchType;
import com.nihongodev.platform.infrastructure.security.AuthenticatedUser;
import com.nihongodev.platform.infrastructure.web.request.CreateCvProfileRequest;
import com.nihongodev.platform.infrastructure.web.request.GeneratePitchRequest;
import com.nihongodev.platform.infrastructure.web.request.UpdateCvProfileRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cv")
@Tag(name = "CV Generator", description = "Generate pitches and CV content for the Japanese job market")
public class CvGeneratorController {

    private final CreateCvProfilePort createProfilePort;
    private final UpdateCvProfilePort updateProfilePort;
    private final GetCvProfilePort getProfilePort;
    private final GeneratePitchPort generatePitchPort;
    private final GetPitchHistoryPort getPitchHistoryPort;
    private final ExportPitchPort exportPitchPort;

    public CvGeneratorController(CreateCvProfilePort createProfilePort,
                                 UpdateCvProfilePort updateProfilePort,
                                 GetCvProfilePort getProfilePort,
                                 GeneratePitchPort generatePitchPort,
                                 GetPitchHistoryPort getPitchHistoryPort,
                                 ExportPitchPort exportPitchPort) {
        this.createProfilePort = createProfilePort;
        this.updateProfilePort = updateProfilePort;
        this.getProfilePort = getProfilePort;
        this.generatePitchPort = generatePitchPort;
        this.getPitchHistoryPort = getPitchHistoryPort;
        this.exportPitchPort = exportPitchPort;
    }

    @PostMapping("/profile")
    @Operation(summary = "Create CV profile")
    public ResponseEntity<CvProfileDto> createProfile(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody CreateCvProfileRequest request) {
        CreateCvProfileCommand command = new CreateCvProfileCommand(
                request.fullName(), request.currentRole(), request.targetRole(),
                request.yearsOfExperience(), request.targetCompanyType(),
                request.techStack(),
                request.experiences() != null ? request.experiences().stream()
                        .map(e -> new CreateCvProfileCommand.WorkExperienceData(
                                e.company(), e.role(), e.durationMonths(), e.highlights()))
                        .toList() : List.of(),
                request.certifications(), request.notableProjects(),
                request.motivationJapan(), request.japaneseLevel());
        return ResponseEntity.status(HttpStatus.CREATED).body(createProfilePort.create(user.id(), command));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update CV profile")
    public ResponseEntity<CvProfileDto> updateProfile(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody UpdateCvProfileRequest request) {
        UpdateCvProfileCommand command = new UpdateCvProfileCommand(
                request.fullName(), request.currentRole(), request.targetRole(),
                request.yearsOfExperience(), request.targetCompanyType(),
                request.techStack(),
                request.experiences() != null ? request.experiences().stream()
                        .map(e -> new CreateCvProfileCommand.WorkExperienceData(
                                e.company(), e.role(), e.durationMonths(), e.highlights()))
                        .toList() : null,
                request.certifications(), request.notableProjects(),
                request.motivationJapan(), request.japaneseLevel());
        return ResponseEntity.ok(updateProfilePort.update(user.id(), command));
    }

    @GetMapping("/profile")
    @Operation(summary = "Get own CV profile")
    public ResponseEntity<CvProfileDto> getProfile(
            @AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(getProfilePort.get(user.id()));
    }

    @PostMapping("/generate")
    @Operation(summary = "Generate a pitch")
    public ResponseEntity<GeneratedPitchDto> generatePitch(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody GeneratePitchRequest request) {
        GeneratePitchCommand command = new GeneratePitchCommand(request.pitchType());
        return ResponseEntity.status(HttpStatus.CREATED).body(generatePitchPort.generate(user.id(), command));
    }

    @GetMapping("/pitches")
    @Operation(summary = "Get pitch history by type")
    public ResponseEntity<List<GeneratedPitchDto>> getPitchHistory(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam PitchType type) {
        return ResponseEntity.ok(getPitchHistoryPort.getHistory(user.id(), type));
    }

    @GetMapping("/pitches/latest")
    @Operation(summary = "Get latest pitch by type")
    public ResponseEntity<GeneratedPitchDto> getLatestPitch(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam PitchType type) {
        return ResponseEntity.ok(getPitchHistoryPort.getLatest(user.id(), type));
    }

    @GetMapping("/pitches/{pitchId}/export")
    @Operation(summary = "Export pitch as markdown or plain text")
    public ResponseEntity<String> exportPitch(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID pitchId,
            @RequestParam(defaultValue = "MARKDOWN") ExportFormat format) {
        String content = exportPitchPort.export(user.id(), pitchId, format);
        String contentType = switch (format) {
            case MARKDOWN -> "text/markdown";
            case PLAIN_TEXT -> "text/plain";
        };
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(content);
    }
}
```

- [ ] **Step 3: Verify compilation**

Run: `cd /c/Users/pierre.l.tchiengue/NihongoDev-Platform-backend && mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/nihongodev/platform/infrastructure/web/request/CreateCvProfileRequest.java \
        src/main/java/com/nihongodev/platform/infrastructure/web/request/UpdateCvProfileRequest.java \
        src/main/java/com/nihongodev/platform/infrastructure/web/request/GeneratePitchRequest.java \
        src/main/java/com/nihongodev/platform/infrastructure/web/controller/CvGeneratorController.java
git commit -m "feat(bloc9): add CvGeneratorController with 7 endpoints and validated request DTOs"
```

---

## Task 18: Full Test Run & TODO Update

**Files:**
- Modify: `TODO.md`

- [ ] **Step 1: Run all tests**

Run: `cd /c/Users/pierre.l.tchiengue/NihongoDev-Platform-backend && mvn test -q`
Expected: All tests PASS (existing ~172 + new ~25 = ~197)

- [ ] **Step 2: Update TODO.md**

Add after the BLOC 8 section (replace the BLOC 9 TODO section):

```markdown
## DONE — BLOC 9: CV Generator (Enhanced — Composable Pipeline, Multi-format Export)

- [x] Domain enums: PitchType, TargetCompanyType, ExportFormat
- [x] WorkExperience value object
- [x] CvProfile aggregate (factory method, JSONB-backed lists)
- [x] GeneratedPitch entity (version history, Markdown storage)
- [x] PitchGeneratedEvent domain event (record with factory method)
- [x] Ports IN: CreateCvProfile, UpdateCvProfile, GetCvProfile, GeneratePitch, GetPitchHistory, ExportPitch
- [x] Ports OUT: CvProfileRepositoryPort, GeneratedPitchRepositoryPort
- [x] Commands: CreateCvProfileCommand, UpdateCvProfileCommand, GeneratePitchCommand
- [x] DTOs: CvProfileDto, WorkExperienceDto, GeneratedPitchDto
- [x] PitchSection interface + PitchAssembler (composable pipeline pattern)
- [x] 12 pitch sections: IntroEN/JP, ExperienceEN/JP, TechStack, MotivationEN/JP, Certifications, ClosingEN/JP, InterviewOpening, ProjectHighlights
- [x] TargetCompanyType adaptation via Java 21 switch expressions in each section
- [x] CreateCvProfileUseCase (profile creation with duplicate check)
- [x] UpdateCvProfileUseCase (partial update, null = no change)
- [x] GetCvProfileUseCase
- [x] GeneratePitchUseCase (assemble + save + publish event)
- [x] GetPitchHistoryUseCase (history + latest by type)
- [x] ExportPitchUseCase (Markdown or plain text with ownership check)
- [x] V8 Flyway migration: cv_profiles (JSONB), generated_pitches tables
- [x] JPA entities: CvProfileEntity (JSONB for lists), GeneratedPitchEntity
- [x] JPA repositories with custom queries
- [x] Persistence mappers with ObjectMapper JSON serialization
- [x] Repository adapters: CvProfileRepositoryAdapter, GeneratedPitchRepositoryAdapter
- [x] PitchAssemblerConfig (Spring @Configuration, section registry per PitchType)
- [x] Kafka: cv-generator-events topic + PitchGeneratedEvent publishing
- [x] CvGeneratorController (7 endpoints: profile CRUD, generate, history, latest, export)
- [x] Request DTOs with Jakarta validation (size limits, NotBlank, etc.)
- [x] Unit tests: CvProfileTest (3 tests)
- [x] Unit tests: PitchAssemblerTest (3 tests)
- [x] Unit tests: IntroSectionENTest (4 tests)
- [x] Unit tests: ExperienceSectionENTest (3 tests)
- [x] Unit tests: TechStackSectionTest (2 tests)
- [x] Unit tests: IntroSectionJPTest (4 tests)
- [x] Unit tests: CreateCvProfileUseCaseTest (2 tests)
- [x] Unit tests: GeneratePitchUseCaseTest (3 tests)
- [x] Unit tests: ExportPitchUseCaseTest (3 tests)
- [x] Compilation verified OK (all tests pass)
```

Also update the NEXT BLOC section:
```markdown
## NEXT BLOC

**BLOC 10: Cultural Intelligence (Innovation)** — Cultural scenarios, keigo validator, cultural scoring system.
```

- [ ] **Step 3: Commit**

```bash
git add TODO.md
git commit -m "docs: update TODO.md with BLOC 9 CV Generator completion"
```

- [ ] **Step 4: Push to main**

```bash
git push origin main
```
