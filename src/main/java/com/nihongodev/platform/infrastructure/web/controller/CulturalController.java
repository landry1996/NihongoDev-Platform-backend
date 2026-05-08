package com.nihongodev.platform.infrastructure.web.controller;

import com.nihongodev.platform.application.command.CreateScenarioCommand;
import com.nihongodev.platform.application.command.SubmitScenarioResponseCommand;
import com.nihongodev.platform.application.dto.*;
import com.nihongodev.platform.application.port.in.*;
import com.nihongodev.platform.domain.model.*;
import com.nihongodev.platform.infrastructure.security.AuthenticatedUser;
import com.nihongodev.platform.infrastructure.web.request.CreateScenarioRequest;
import com.nihongodev.platform.infrastructure.web.request.SubmitScenarioResponseRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cultural")
@Tag(name = "Cultural Intelligence", description = "Cultural scenario exercises with keigo validation and multi-dimensional scoring")
public class CulturalController {

    private final GetScenarioCatalogPort catalogPort;
    private final StartScenarioPort startScenarioPort;
    private final SubmitScenarioResponsePort submitPort;
    private final GetScenarioHistoryPort historyPort;
    private final GetCulturalProgressPort progressPort;
    private final GetKeigoReportPort keigoReportPort;
    private final CreateScenarioPort createScenarioPort;

    public CulturalController(GetScenarioCatalogPort catalogPort,
                              StartScenarioPort startScenarioPort,
                              SubmitScenarioResponsePort submitPort,
                              GetScenarioHistoryPort historyPort,
                              GetCulturalProgressPort progressPort,
                              GetKeigoReportPort keigoReportPort,
                              CreateScenarioPort createScenarioPort) {
        this.catalogPort = catalogPort;
        this.startScenarioPort = startScenarioPort;
        this.submitPort = submitPort;
        this.historyPort = historyPort;
        this.progressPort = progressPort;
        this.keigoReportPort = keigoReportPort;
        this.createScenarioPort = createScenarioPort;
    }

    @GetMapping("/scenarios")
    @Operation(summary = "Get published scenario catalog with optional filters")
    public ResponseEntity<List<CulturalScenarioDto>> getCatalog(
            @RequestParam(required = false) String context,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String category) {
        WorkplaceContext ctx = parseEnum(context, WorkplaceContext.class);
        JapaneseLevel diff = parseEnum(difficulty, JapaneseLevel.class);
        ScenarioCategory cat = parseEnum(category, ScenarioCategory.class);
        return ResponseEntity.ok(catalogPort.getPublished(ctx, diff, cat));
    }

    @GetMapping("/scenarios/{id}")
    @Operation(summary = "Get scenario details by ID")
    public ResponseEntity<CulturalScenarioDto> getScenarioById(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID id) {
        return ResponseEntity.ok(startScenarioPort.startScenario(user.id(), id));
    }

    @PostMapping("/scenarios/{id}/attempt")
    @Operation(summary = "Submit a response to a scenario")
    public ResponseEntity<ScenarioAttemptDto> submitAttempt(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID id,
            @Valid @RequestBody SubmitScenarioResponseRequest request) {
        SubmitScenarioResponseCommand command = new SubmitScenarioResponseCommand(
                id, request.response(), request.selectedChoiceId(), request.timeSpentSeconds()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(submitPort.submit(user.id(), command));
    }

    @GetMapping("/history")
    @Operation(summary = "Get user's scenario attempt history")
    public ResponseEntity<List<ScenarioAttemptDto>> getHistory(
            @AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(historyPort.getUserHistory(user.id()));
    }

    @GetMapping("/progress")
    @Operation(summary = "Get user's cultural progress by category")
    public ResponseEntity<List<CulturalProgressDto>> getProgress(
            @AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(progressPort.getProgress(user.id()));
    }

    @GetMapping("/progress/keigo")
    @Operation(summary = "Get user's keigo violation report")
    public ResponseEntity<KeigoReportDto> getKeigoReport(
            @AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(keigoReportPort.getReport(user.id()));
    }

    @PostMapping("/scenarios")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Create a new scenario (admin/teacher only)")
    public ResponseEntity<CulturalScenarioDto> createScenario(
            @Valid @RequestBody CreateScenarioRequest request) {
        CreateScenarioCommand command = new CreateScenarioCommand(
                request.title(), request.titleJp(),
                request.situation(), request.situationJp(),
                parseEnum(request.context(), WorkplaceContext.class),
                parseEnum(request.relationship(), RelationshipType.class),
                parseEnum(request.mode(), ScenarioMode.class),
                parseEnum(request.category(), ScenarioCategory.class),
                parseEnum(request.expectedKeigoLevel(), KeigoLevel.class),
                parseEnum(request.difficulty(), JapaneseLevel.class),
                request.choices() != null ? request.choices().stream()
                        .map(c -> new ScenarioChoice(c.text(), c.textJp(), c.isOptimal(), c.culturalScore(), c.feedbackIfChosen()))
                        .toList() : List.of(),
                request.modelAnswer(), request.modelAnswerExplanation(),
                request.keyPhrases() != null ? request.keyPhrases() : List.of(),
                request.avoidPhrases() != null ? request.avoidPhrases() : List.of(),
                request.culturalNote(), request.xpReward()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(createScenarioPort.create(command));
    }

    @PutMapping("/scenarios/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Update an existing scenario (admin/teacher only)")
    public ResponseEntity<CulturalScenarioDto> updateScenario(
            @PathVariable UUID id,
            @Valid @RequestBody CreateScenarioRequest request) {
        CreateScenarioCommand command = new CreateScenarioCommand(
                request.title(), request.titleJp(),
                request.situation(), request.situationJp(),
                parseEnum(request.context(), WorkplaceContext.class),
                parseEnum(request.relationship(), RelationshipType.class),
                parseEnum(request.mode(), ScenarioMode.class),
                parseEnum(request.category(), ScenarioCategory.class),
                parseEnum(request.expectedKeigoLevel(), KeigoLevel.class),
                parseEnum(request.difficulty(), JapaneseLevel.class),
                request.choices() != null ? request.choices().stream()
                        .map(c -> new ScenarioChoice(c.text(), c.textJp(), c.isOptimal(), c.culturalScore(), c.feedbackIfChosen()))
                        .toList() : List.of(),
                request.modelAnswer(), request.modelAnswerExplanation(),
                request.keyPhrases() != null ? request.keyPhrases() : List.of(),
                request.avoidPhrases() != null ? request.avoidPhrases() : List.of(),
                request.culturalNote(), request.xpReward()
        );
        return ResponseEntity.ok(createScenarioPort.create(command));
    }

    private <T extends Enum<T>> T parseEnum(String value, Class<T> enumType) {
        if (value == null || value.isBlank()) return null;
        try { return Enum.valueOf(enumType, value.toUpperCase()); }
        catch (IllegalArgumentException e) { return null; }
    }
}
