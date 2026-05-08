package com.nihongodev.platform.infrastructure.web.controller;

import com.nihongodev.platform.application.command.CreateLessonCommand;
import com.nihongodev.platform.application.command.UpdateLessonCommand;
import com.nihongodev.platform.application.dto.LessonDto;
import com.nihongodev.platform.application.port.in.*;
import com.nihongodev.platform.domain.model.LessonLevel;
import com.nihongodev.platform.domain.model.LessonType;
import com.nihongodev.platform.infrastructure.security.AuthenticatedUser;
import com.nihongodev.platform.infrastructure.web.request.CreateLessonRequest;
import com.nihongodev.platform.infrastructure.web.request.UpdateLessonRequest;
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
@RequestMapping("/api/lessons")
@Tag(name = "Lessons", description = "Lesson management and completion")
public class LessonController {

    private final CreateLessonPort createLessonPort;
    private final UpdateLessonPort updateLessonPort;
    private final GetLessonPort getLessonPort;
    private final CompleteLessonPort completeLessonPort;
    private final DeleteLessonPort deleteLessonPort;

    public LessonController(CreateLessonPort createLessonPort,
                            UpdateLessonPort updateLessonPort,
                            GetLessonPort getLessonPort,
                            CompleteLessonPort completeLessonPort,
                            DeleteLessonPort deleteLessonPort) {
        this.createLessonPort = createLessonPort;
        this.updateLessonPort = updateLessonPort;
        this.getLessonPort = getLessonPort;
        this.completeLessonPort = completeLessonPort;
        this.deleteLessonPort = deleteLessonPort;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Create a new lesson")
    public ResponseEntity<LessonDto> create(@Valid @RequestBody CreateLessonRequest request) {
        CreateLessonCommand command = new CreateLessonCommand(
                request.title(),
                request.description(),
                parseType(request.type()),
                parseLevel(request.level()),
                request.content(),
                request.orderIndex()
        );
        LessonDto lesson = createLessonPort.create(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(lesson);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Update a lesson")
    public ResponseEntity<LessonDto> update(@PathVariable UUID id, @Valid @RequestBody UpdateLessonRequest request) {
        UpdateLessonCommand command = new UpdateLessonCommand(
                id,
                request.title(),
                request.description(),
                request.type() != null ? parseType(request.type()) : null,
                request.level() != null ? parseLevel(request.level()) : null,
                request.content(),
                request.orderIndex()
        );
        LessonDto lesson = updateLessonPort.update(command);
        return ResponseEntity.ok(lesson);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get lesson by ID")
    public ResponseEntity<LessonDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(getLessonPort.getById(id));
    }

    @GetMapping
    @Operation(summary = "Get lessons with optional filters")
    public ResponseEntity<List<LessonDto>> getLessons(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String level) {

        List<LessonDto> lessons;
        if (type != null && level != null) {
            lessons = getLessonPort.getByTypeAndLevel(parseType(type), parseLevel(level));
        } else if (type != null) {
            lessons = getLessonPort.getByType(parseType(type));
        } else if (level != null) {
            lessons = getLessonPort.getByLevel(parseLevel(level));
        } else {
            lessons = getLessonPort.getPublished();
        }
        return ResponseEntity.ok(lessons);
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "Mark a lesson as completed by the current user")
    public ResponseEntity<Void> complete(@PathVariable UUID id, @AuthenticationPrincipal AuthenticatedUser user) {
        completeLessonPort.complete(user.id(), id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Delete a lesson")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteLessonPort.delete(id);
        return ResponseEntity.noContent().build();
    }

    private LessonType parseType(String type) {
        try { return LessonType.valueOf(type.toUpperCase()); }
        catch (IllegalArgumentException e) { return LessonType.VOCABULARY; }
    }

    private LessonLevel parseLevel(String level) {
        try { return LessonLevel.valueOf(level.toUpperCase()); }
        catch (IllegalArgumentException e) { return LessonLevel.BEGINNER; }
    }
}
