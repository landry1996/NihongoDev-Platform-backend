package com.nihongodev.platform.infrastructure.web.controller;

import com.nihongodev.platform.application.command.CreateCodeExerciseCommand;
import com.nihongodev.platform.application.command.SubmitCodeExerciseResponseCommand;
import com.nihongodev.platform.application.command.ValidateCommitMessageCommand;
import com.nihongodev.platform.application.dto.*;
import com.nihongodev.platform.application.port.in.*;
import com.nihongodev.platform.domain.model.*;
import com.nihongodev.platform.infrastructure.security.AuthenticatedUser;
import com.nihongodev.platform.infrastructure.web.request.*;
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
@RequestMapping("/api/code-japanese")
@Tag(name = "Code in Japanese", description = "Train developers to write code reviews, PRs, and commits in Japanese")
public class CodeJapaneseController {

    private final GetExerciseCatalogPort exerciseCatalogPort;
    private final SubmitCodeExerciseResponsePort submitResponsePort;
    private final ValidateCommitMessagePort validateCommitPort;
    private final GetCodeExerciseHistoryPort historyPort;
    private final GetCodeJapaneseProgressPort progressPort;
    private final GetViolationReportPort violationReportPort;
    private final CreateCodeExercisePort createExercisePort;

    public CodeJapaneseController(GetExerciseCatalogPort exerciseCatalogPort,
                                  SubmitCodeExerciseResponsePort submitResponsePort,
                                  ValidateCommitMessagePort validateCommitPort,
                                  GetCodeExerciseHistoryPort historyPort,
                                  GetCodeJapaneseProgressPort progressPort,
                                  GetViolationReportPort violationReportPort,
                                  CreateCodeExercisePort createExercisePort) {
        this.exerciseCatalogPort = exerciseCatalogPort;
        this.submitResponsePort = submitResponsePort;
        this.validateCommitPort = validateCommitPort;
        this.historyPort = historyPort;
        this.progressPort = progressPort;
        this.violationReportPort = violationReportPort;
        this.createExercisePort = createExercisePort;
    }

    @GetMapping("/exercises")
    @Operation(summary = "Get exercise catalog filtered by type, difficulty, and context")
    public ResponseEntity<List<CodeReviewExerciseDto>> getExercises(
            @RequestParam(required = false) ExerciseType type,
            @RequestParam(required = false) JapaneseLevel difficulty,
            @RequestParam(required = false) CodeContext context) {
        return ResponseEntity.ok(exerciseCatalogPort.getPublished(type, difficulty, context));
    }

    @GetMapping("/exercises/{id}")
    @Operation(summary = "Get exercise details by ID")
    public ResponseEntity<CodeReviewExerciseDto> getExercise(@PathVariable UUID id) {
        return ResponseEntity.ok(exerciseCatalogPort.getById(id));
    }

    @PostMapping("/exercises/{id}/attempt")
    @Operation(summary = "Submit a response for an exercise")
    public ResponseEntity<CodeExerciseAttemptDto> submitAttempt(
            @PathVariable UUID id,
            @Valid @RequestBody SubmitCodeExerciseRequest request,
            @AuthenticationPrincipal AuthenticatedUser user) {
        var command = new SubmitCodeExerciseResponseCommand(
            user.id(), id, request.response(), request.timeSpentSeconds()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(submitResponsePort.execute(command));
    }

    @PostMapping("/commit/validate")
    @Operation(summary = "Validate a commit message (standalone)")
    public ResponseEntity<CommitMessageAnalysisDto> validateCommit(
            @Valid @RequestBody ValidateCommitMessageRequest request) {
        var command = new ValidateCommitMessageCommand(
            request.commitMessage(), request.expectedPrefix(),
            request.requireTaigenDome(), request.requireScope(), request.expectedScope()
        );
        return ResponseEntity.ok(validateCommitPort.execute(command));
    }

    @GetMapping("/history")
    @Operation(summary = "Get exercise attempt history")
    public ResponseEntity<List<CodeExerciseAttemptDto>> getHistory(
            @AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(historyPort.getByUserId(user.id()));
    }

    @GetMapping("/progress")
    @Operation(summary = "Get progress by exercise type")
    public ResponseEntity<List<CodeJapaneseProgressDto>> getProgress(
            @AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(progressPort.getByUserId(user.id()));
    }

    @GetMapping("/progress/violations")
    @Operation(summary = "Get recurring violations report")
    public ResponseEntity<ViolationReportDto> getViolationReport(
            @AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(violationReportPort.getByUserId(user.id()));
    }

    @PostMapping("/exercises")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Create a new exercise (ADMIN/TEACHER only)")
    public ResponseEntity<CodeReviewExerciseDto> createExercise(
            @Valid @RequestBody CreateCodeExerciseRequest request) {
        var command = new CreateCodeExerciseCommand(
            request.title(), request.titleJp(), request.type(), request.codeContext(),
            request.difficulty(), request.codeSnippet(), request.codeLanguage(),
            request.scenario(), request.scenarioJp(), request.expectedReviewLevel(),
            request.technicalIssues(), request.modelAnswer(), request.modelAnswerExplanation(),
            request.keyPhrases(), request.avoidPhrases(), request.technicalTermsJp(),
            request.prTemplate(), request.commitRule(), request.culturalNote(), request.xpReward()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(createExercisePort.execute(command));
    }

    @PutMapping("/exercises/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Update an exercise (ADMIN/TEACHER only)")
    public ResponseEntity<CodeReviewExerciseDto> updateExercise(
            @PathVariable UUID id,
            @Valid @RequestBody CreateCodeExerciseRequest request) {
        var command = new CreateCodeExerciseCommand(
            request.title(), request.titleJp(), request.type(), request.codeContext(),
            request.difficulty(), request.codeSnippet(), request.codeLanguage(),
            request.scenario(), request.scenarioJp(), request.expectedReviewLevel(),
            request.technicalIssues(), request.modelAnswer(), request.modelAnswerExplanation(),
            request.keyPhrases(), request.avoidPhrases(), request.technicalTermsJp(),
            request.prTemplate(), request.commitRule(), request.culturalNote(), request.xpReward()
        );
        return ResponseEntity.ok(createExercisePort.execute(command));
    }
}
