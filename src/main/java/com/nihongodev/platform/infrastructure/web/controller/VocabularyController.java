package com.nihongodev.platform.infrastructure.web.controller;

import com.nihongodev.platform.application.command.*;
import com.nihongodev.platform.application.dto.VocabularyDto;
import com.nihongodev.platform.application.dto.VocabularyMasteryDto;
import com.nihongodev.platform.application.dto.VocabularyQuizDto;
import com.nihongodev.platform.application.port.in.*;
import com.nihongodev.platform.domain.model.VocabularyCategory;
import com.nihongodev.platform.domain.model.VocabularyLevel;
import com.nihongodev.platform.infrastructure.security.AuthenticatedUser;
import com.nihongodev.platform.infrastructure.web.request.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vocabulary")
@Tag(name = "Vocabulary", description = "IT Japanese vocabulary with SRS, search, and quiz generation")
public class VocabularyController {

    private final CreateVocabularyPort createVocabularyPort;
    private final UpdateVocabularyPort updateVocabularyPort;
    private final DeleteVocabularyPort deleteVocabularyPort;
    private final SearchVocabularyPort searchVocabularyPort;
    private final ReviewVocabularyPort reviewVocabularyPort;
    private final GenerateVocabularyQuizPort generateQuizPort;

    public VocabularyController(CreateVocabularyPort createVocabularyPort,
                                UpdateVocabularyPort updateVocabularyPort,
                                DeleteVocabularyPort deleteVocabularyPort,
                                SearchVocabularyPort searchVocabularyPort,
                                ReviewVocabularyPort reviewVocabularyPort,
                                GenerateVocabularyQuizPort generateQuizPort) {
        this.createVocabularyPort = createVocabularyPort;
        this.updateVocabularyPort = updateVocabularyPort;
        this.deleteVocabularyPort = deleteVocabularyPort;
        this.searchVocabularyPort = searchVocabularyPort;
        this.reviewVocabularyPort = reviewVocabularyPort;
        this.generateQuizPort = generateQuizPort;
    }

    @PostMapping
    @Operation(summary = "Create a vocabulary word")
    public ResponseEntity<VocabularyDto> create(@Valid @RequestBody CreateVocabularyRequest request) {
        CreateVocabularyCommand command = new CreateVocabularyCommand(
                request.french(), request.english(), request.japanese(), request.romaji(),
                request.example(), request.codeExample(), parseCategory(request.category()),
                parseLevel(request.level()), request.domain(), request.tags(), request.lessonId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(createVocabularyPort.create(command));
    }

    @PostMapping("/batch")
    @Operation(summary = "Batch import vocabulary words")
    public ResponseEntity<List<VocabularyDto>> batchImport(@Valid @RequestBody List<CreateVocabularyRequest> requests) {
        List<CreateVocabularyCommand> commands = requests.stream()
                .map(r -> new CreateVocabularyCommand(
                        r.french(), r.english(), r.japanese(), r.romaji(),
                        r.example(), r.codeExample(), parseCategory(r.category()),
                        parseLevel(r.level()), r.domain(), r.tags(), r.lessonId()
                ))
                .toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(createVocabularyPort.batchImport(commands));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a vocabulary word")
    public ResponseEntity<VocabularyDto> update(@PathVariable UUID id, @Valid @RequestBody UpdateVocabularyRequest request) {
        UpdateVocabularyCommand command = new UpdateVocabularyCommand(
                id, request.french(), request.english(), request.japanese(), request.romaji(),
                request.example(), request.codeExample(),
                request.category() != null ? parseCategory(request.category()) : null,
                request.level() != null ? parseLevel(request.level()) : null,
                request.domain(), request.tags(), request.lessonId()
        );
        return ResponseEntity.ok(updateVocabularyPort.update(command));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a vocabulary word")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteVocabularyPort.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get vocabulary word by ID")
    public ResponseEntity<VocabularyDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(searchVocabularyPort.getById(id));
    }

    @GetMapping
    @Operation(summary = "Search vocabulary with filters")
    public ResponseEntity<List<VocabularyDto>> search(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) UUID lessonId,
            @RequestParam(required = false) String tag) {

        SearchVocabularyCommand command = new SearchVocabularyCommand(
                query,
                category != null ? parseCategory(category) : null,
                level != null ? parseLevel(level) : null,
                lessonId,
                tag
        );
        return ResponseEntity.ok(searchVocabularyPort.search(command));
    }

    @GetMapping("/lesson/{lessonId}")
    @Operation(summary = "Get vocabulary by lesson")
    public ResponseEntity<List<VocabularyDto>> getByLesson(@PathVariable UUID lessonId) {
        return ResponseEntity.ok(searchVocabularyPort.getByLessonId(lessonId));
    }

    @PostMapping("/review")
    @Operation(summary = "Record a vocabulary review (SRS)")
    public ResponseEntity<VocabularyMasteryDto> review(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewVocabularyPort.recordReview(user.id(), request.vocabularyId(), request.correct()));
    }

    @GetMapping("/review/due")
    @Operation(summary = "Get vocabulary due for review")
    public ResponseEntity<List<VocabularyMasteryDto>> getDueForReview(@AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(reviewVocabularyPort.getDueForReview(user.id()));
    }

    @GetMapping("/mastery")
    @Operation(summary = "Get user mastery statistics")
    public ResponseEntity<List<VocabularyMasteryDto>> getMasteryStats(@AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(reviewVocabularyPort.getUserMasteryStats(user.id()));
    }

    @PostMapping("/quiz/generate")
    @Operation(summary = "Generate a vocabulary quiz")
    public ResponseEntity<VocabularyQuizDto> generateQuiz(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody GenerateQuizRequest request) {
        GenerateVocabularyQuizCommand command = new GenerateVocabularyQuizCommand(
                request.quizType(),
                request.category() != null ? parseCategory(request.category()) : null,
                request.level() != null ? parseLevel(request.level()) : null,
                request.wordCount()
        );
        return ResponseEntity.ok(generateQuizPort.generate(user.id(), command));
    }

    private VocabularyCategory parseCategory(String category) {
        try { return VocabularyCategory.valueOf(category.toUpperCase()); }
        catch (IllegalArgumentException e) { return VocabularyCategory.JAVA; }
    }

    private VocabularyLevel parseLevel(String level) {
        if (level == null || level.isBlank()) return VocabularyLevel.BEGINNER;
        try { return VocabularyLevel.valueOf(level.toUpperCase()); }
        catch (IllegalArgumentException e) { return VocabularyLevel.BEGINNER; }
    }
}
