package com.nihongodev.platform.infrastructure.web.controller;

import com.nihongodev.platform.application.command.CompleteReadingSessionCommand;
import com.nihongodev.platform.application.command.IngestContentCommand;
import com.nihongodev.platform.application.command.UpdateContentPreferencesCommand;
import com.nihongodev.platform.application.dto.*;
import com.nihongodev.platform.application.port.in.*;
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
@RequestMapping("/api/real-content")
@Tag(name = "Real Content Engine", description = "Read and learn from real Japanese tech content with annotations")
public class RealContentController {

    private final IngestContentPort ingestContentPort;
    private final AnnotateContentPort annotateContentPort;
    private final PublishContentPort publishContentPort;
    private final GetContentByIdPort getContentByIdPort;
    private final GetPersonalizedFeedPort getPersonalizedFeedPort;
    private final StartReadingSessionPort startReadingSessionPort;
    private final CompleteReadingSessionPort completeReadingSessionPort;
    private final GetReadingHistoryPort getReadingHistoryPort;
    private final SaveVocabularyFromContentPort saveVocabularyPort;
    private final GetContentPreferencesPort getPreferencesPort;
    private final UpdateContentPreferencesPort updatePreferencesPort;

    public RealContentController(IngestContentPort ingestContentPort,
                                  AnnotateContentPort annotateContentPort,
                                  PublishContentPort publishContentPort,
                                  GetContentByIdPort getContentByIdPort,
                                  GetPersonalizedFeedPort getPersonalizedFeedPort,
                                  StartReadingSessionPort startReadingSessionPort,
                                  CompleteReadingSessionPort completeReadingSessionPort,
                                  GetReadingHistoryPort getReadingHistoryPort,
                                  SaveVocabularyFromContentPort saveVocabularyPort,
                                  GetContentPreferencesPort getPreferencesPort,
                                  UpdateContentPreferencesPort updatePreferencesPort) {
        this.ingestContentPort = ingestContentPort;
        this.annotateContentPort = annotateContentPort;
        this.publishContentPort = publishContentPort;
        this.getContentByIdPort = getContentByIdPort;
        this.getPersonalizedFeedPort = getPersonalizedFeedPort;
        this.startReadingSessionPort = startReadingSessionPort;
        this.completeReadingSessionPort = completeReadingSessionPort;
        this.getReadingHistoryPort = getReadingHistoryPort;
        this.saveVocabularyPort = saveVocabularyPort;
        this.getPreferencesPort = getPreferencesPort;
        this.updatePreferencesPort = updatePreferencesPort;
    }

    @PostMapping("/ingest")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Ingest new content from external source")
    public ResponseEntity<RealContentDto> ingestContent(
            @Valid @RequestBody IngestContentRequest request) {
        var command = new IngestContentCommand(
            request.title(), request.body(), request.sourceUrl(),
            request.source(), request.domain(), request.authorName()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ingestContentPort.execute(command));
    }

    @PostMapping("/{id}/annotate")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Run annotation engine on content")
    public ResponseEntity<RealContentDto> annotateContent(@PathVariable UUID id) {
        return ResponseEntity.ok(annotateContentPort.execute(id));
    }

    @PostMapping("/{id}/publish")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Publish annotated content for learners")
    public ResponseEntity<RealContentDto> publishContent(@PathVariable UUID id) {
        return ResponseEntity.ok(publishContentPort.execute(id));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get content by ID with annotations")
    public ResponseEntity<RealContentDto> getContent(@PathVariable UUID id) {
        return ResponseEntity.ok(getContentByIdPort.execute(id));
    }

    @GetMapping("/feed")
    @Operation(summary = "Get personalized content feed based on preferences and level")
    public ResponseEntity<ContentFeedDto> getPersonalizedFeed(
            @AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(getPersonalizedFeedPort.execute(user.id()));
    }

    @PostMapping("/{contentId}/read")
    @Operation(summary = "Start a reading session for content")
    public ResponseEntity<ContentReadingSessionDto> startReadingSession(
            @PathVariable UUID contentId,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(startReadingSessionPort.execute(user.id(), contentId));
    }

    @PostMapping("/sessions/{sessionId}/complete")
    @Operation(summary = "Complete a reading session with results")
    public ResponseEntity<ContentReadingSessionDto> completeReadingSession(
            @PathVariable UUID sessionId,
            @Valid @RequestBody CompleteReadingSessionRequest request,
            @AuthenticationPrincipal AuthenticatedUser user) {
        var command = new CompleteReadingSessionCommand(
            user.id(), sessionId, request.readingTimeSeconds(),
            request.annotationsViewed(), request.vocabularyLookedUp(),
            request.comprehensionScore()
        );
        return ResponseEntity.ok(completeReadingSessionPort.execute(command));
    }

    @PostMapping("/sessions/{sessionId}/vocabulary")
    @Operation(summary = "Save a vocabulary item from a reading session")
    public ResponseEntity<Void> saveVocabulary(
            @PathVariable UUID sessionId,
            @Valid @RequestBody SaveVocabularyRequest request,
            @AuthenticationPrincipal AuthenticatedUser user) {
        saveVocabularyPort.execute(user.id(), sessionId, request.annotationId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/history")
    @Operation(summary = "Get reading session history")
    public ResponseEntity<List<ContentReadingSessionDto>> getReadingHistory(
            @AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(getReadingHistoryPort.execute(user.id()));
    }

    @GetMapping("/preferences")
    @Operation(summary = "Get content preferences")
    public ResponseEntity<UserContentPreferenceDto> getPreferences(
            @AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(getPreferencesPort.execute(user.id()));
    }

    @PutMapping("/preferences")
    @Operation(summary = "Update content preferences")
    public ResponseEntity<UserContentPreferenceDto> updatePreferences(
            @Valid @RequestBody UpdateContentPreferencesRequest request,
            @AuthenticationPrincipal AuthenticatedUser user) {
        var command = new UpdateContentPreferencesCommand(
            user.id(), request.preferredDomains(), request.currentLevel(),
            request.maxDifficulty(), request.preferredReadingMinutes(),
            request.preferredSources()
        );
        return ResponseEntity.ok(updatePreferencesPort.execute(command));
    }
}
