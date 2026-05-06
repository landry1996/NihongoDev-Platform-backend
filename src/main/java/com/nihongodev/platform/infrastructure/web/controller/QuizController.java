package com.nihongodev.platform.infrastructure.web.controller;

import com.nihongodev.platform.application.command.AddQuestionCommand;
import com.nihongodev.platform.application.command.CreateQuizCommand;
import com.nihongodev.platform.application.command.SubmitAnswerCommand;
import com.nihongodev.platform.application.dto.*;
import com.nihongodev.platform.application.port.in.*;
import com.nihongodev.platform.domain.model.DifficultyLevel;
import com.nihongodev.platform.domain.model.QuestionType;
import com.nihongodev.platform.domain.model.QuizMode;
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
@RequestMapping("/api/quizzes")
@Tag(name = "Quiz", description = "Quiz management with adaptive difficulty, multi-mode, and streak system")
public class QuizController {

    private final CreateQuizPort createQuizPort;
    private final GetQuizPort getQuizPort;
    private final StartQuizPort startQuizPort;
    private final SubmitAnswerPort submitAnswerPort;
    private final CompleteQuizPort completeQuizPort;
    private final GetQuizHistoryPort getQuizHistoryPort;

    public QuizController(CreateQuizPort createQuizPort,
                          GetQuizPort getQuizPort,
                          StartQuizPort startQuizPort,
                          SubmitAnswerPort submitAnswerPort,
                          CompleteQuizPort completeQuizPort,
                          GetQuizHistoryPort getQuizHistoryPort) {
        this.createQuizPort = createQuizPort;
        this.getQuizPort = getQuizPort;
        this.startQuizPort = startQuizPort;
        this.submitAnswerPort = submitAnswerPort;
        this.completeQuizPort = completeQuizPort;
        this.getQuizHistoryPort = getQuizHistoryPort;
    }

    @PostMapping
    @Operation(summary = "Create a new quiz")
    public ResponseEntity<QuizDto> create(@Valid @RequestBody CreateQuizRequest request) {
        CreateQuizCommand command = new CreateQuizCommand(
                request.lessonId(), request.title(), request.description(),
                request.level(), parseMode(request.mode()),
                request.timeLimitSeconds(), request.maxAttempts(), request.passingScore()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(createQuizPort.create(command));
    }

    @PostMapping("/questions")
    @Operation(summary = "Add a question to a quiz")
    public ResponseEntity<QuestionDto> addQuestion(@Valid @RequestBody AddQuestionRequest request) {
        AddQuestionCommand command = new AddQuestionCommand(
                request.quizId(), request.content(), request.correctAnswer(),
                request.explanation(), parseQuestionType(request.questionType()),
                parseDifficulty(request.difficultyLevel()), request.options(),
                request.points(), request.timeLimitSeconds(), request.orderIndex()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(createQuizPort.addQuestion(command));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get quiz by ID with questions")
    public ResponseEntity<QuizDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(getQuizPort.getById(id));
    }

    @GetMapping("/lesson/{lessonId}")
    @Operation(summary = "Get quizzes by lesson")
    public ResponseEntity<List<QuizDto>> getByLesson(@PathVariable UUID lessonId) {
        return ResponseEntity.ok(getQuizPort.getByLessonId(lessonId));
    }

    @GetMapping("/published")
    @Operation(summary = "Get all published quizzes")
    public ResponseEntity<List<QuizDto>> getPublished() {
        return ResponseEntity.ok(getQuizPort.getPublished());
    }

    @PostMapping("/{quizId}/start")
    @Operation(summary = "Start a quiz attempt")
    public ResponseEntity<QuizAttemptDto> start(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID quizId,
            @RequestBody(required = false) StartQuizRequest request) {
        QuizMode mode = request != null ? parseMode(request.mode()) : null;
        return ResponseEntity.status(HttpStatus.CREATED).body(startQuizPort.start(user.id(), quizId, mode));
    }

    @PostMapping("/attempts/{attemptId}/answer")
    @Operation(summary = "Submit an answer for a quiz attempt")
    public ResponseEntity<AnswerResultDto> submitAnswer(
            @PathVariable UUID attemptId,
            @Valid @RequestBody SubmitAnswerRequest request) {
        SubmitAnswerCommand command = new SubmitAnswerCommand(
                attemptId, request.questionId(), request.userAnswer(), request.timeSpentSeconds()
        );
        return ResponseEntity.ok(submitAnswerPort.submit(command));
    }

    @PostMapping("/attempts/{attemptId}/complete")
    @Operation(summary = "Complete a quiz attempt and get results")
    public ResponseEntity<QuizResultDto> complete(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID attemptId) {
        return ResponseEntity.ok(completeQuizPort.complete(user.id(), attemptId));
    }

    @GetMapping("/history")
    @Operation(summary = "Get user's quiz history")
    public ResponseEntity<List<QuizResultDto>> getHistory(@AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(getQuizHistoryPort.getUserHistory(user.id()));
    }

    @GetMapping("/results/{attemptId}")
    @Operation(summary = "Get result by attempt ID")
    public ResponseEntity<QuizResultDto> getResultByAttempt(@PathVariable UUID attemptId) {
        return ResponseEntity.ok(getQuizHistoryPort.getResultByAttempt(attemptId));
    }

    private QuizMode parseMode(String mode) {
        if (mode == null || mode.isBlank()) return null;
        try { return QuizMode.valueOf(mode.toUpperCase()); }
        catch (IllegalArgumentException e) { return QuizMode.CLASSIC; }
    }

    private QuestionType parseQuestionType(String type) {
        if (type == null || type.isBlank()) return QuestionType.MULTIPLE_CHOICE;
        try { return QuestionType.valueOf(type.toUpperCase()); }
        catch (IllegalArgumentException e) { return QuestionType.MULTIPLE_CHOICE; }
    }

    private DifficultyLevel parseDifficulty(String difficulty) {
        if (difficulty == null || difficulty.isBlank()) return DifficultyLevel.MEDIUM;
        try { return DifficultyLevel.valueOf(difficulty.toUpperCase()); }
        catch (IllegalArgumentException e) { return DifficultyLevel.MEDIUM; }
    }
}
