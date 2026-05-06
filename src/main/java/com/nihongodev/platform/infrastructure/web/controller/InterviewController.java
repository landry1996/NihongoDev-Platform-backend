package com.nihongodev.platform.infrastructure.web.controller;

import com.nihongodev.platform.application.command.StartInterviewCommand;
import com.nihongodev.platform.application.command.SubmitInterviewAnswerCommand;
import com.nihongodev.platform.application.dto.InterviewAnswerResultDto;
import com.nihongodev.platform.application.dto.InterviewQuestionDto;
import com.nihongodev.platform.application.dto.InterviewSessionDto;
import com.nihongodev.platform.application.port.in.*;
import com.nihongodev.platform.domain.model.InterviewDifficulty;
import com.nihongodev.platform.domain.model.InterviewType;
import com.nihongodev.platform.infrastructure.security.AuthenticatedUser;
import com.nihongodev.platform.infrastructure.web.request.StartInterviewRequest;
import com.nihongodev.platform.infrastructure.web.request.SubmitInterviewAnswerRequest;
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
@RequestMapping("/api/interviews")
@Tag(name = "Interview", description = "Interview simulation with adaptive evaluation and multi-dimensional scoring")
public class InterviewController {

    private final StartInterviewPort startInterviewPort;
    private final GetNextQuestionPort getNextQuestionPort;
    private final SubmitInterviewAnswerPort submitAnswerPort;
    private final CompleteInterviewPort completeInterviewPort;
    private final GetInterviewHistoryPort getHistoryPort;

    public InterviewController(StartInterviewPort startInterviewPort,
                               GetNextQuestionPort getNextQuestionPort,
                               SubmitInterviewAnswerPort submitAnswerPort,
                               CompleteInterviewPort completeInterviewPort,
                               GetInterviewHistoryPort getHistoryPort) {
        this.startInterviewPort = startInterviewPort;
        this.getNextQuestionPort = getNextQuestionPort;
        this.submitAnswerPort = submitAnswerPort;
        this.completeInterviewPort = completeInterviewPort;
        this.getHistoryPort = getHistoryPort;
    }

    @PostMapping("/start")
    @Operation(summary = "Start a new interview session")
    public ResponseEntity<InterviewSessionDto> start(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody StartInterviewRequest request) {
        StartInterviewCommand command = new StartInterviewCommand(
                parseInterviewType(request.interviewType()),
                parseDifficulty(request.difficulty())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(startInterviewPort.start(user.id(), command));
    }

    @GetMapping("/{sessionId}/next-question")
    @Operation(summary = "Get the next question in the session")
    public ResponseEntity<InterviewQuestionDto> getNextQuestion(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID sessionId) {
        return ResponseEntity.ok(getNextQuestionPort.getNextQuestion(user.id(), sessionId));
    }

    @PostMapping("/{sessionId}/answer")
    @Operation(summary = "Submit an answer for the current question")
    public ResponseEntity<InterviewAnswerResultDto> submitAnswer(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID sessionId,
            @Valid @RequestBody SubmitInterviewAnswerRequest request) {
        SubmitInterviewAnswerCommand command = new SubmitInterviewAnswerCommand(
                sessionId, request.questionId(), request.answerText(), request.timeSpentSeconds()
        );
        return ResponseEntity.ok(submitAnswerPort.submit(user.id(), command));
    }

    @PostMapping("/{sessionId}/complete")
    @Operation(summary = "Complete the interview session and get final results")
    public ResponseEntity<InterviewSessionDto> complete(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID sessionId) {
        return ResponseEntity.ok(completeInterviewPort.complete(user.id(), sessionId));
    }

    @GetMapping("/history")
    @Operation(summary = "Get user's interview history")
    public ResponseEntity<List<InterviewSessionDto>> getHistory(
            @AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(getHistoryPort.getUserHistory(user.id()));
    }

    @GetMapping("/{sessionId}")
    @Operation(summary = "Get interview session details")
    public ResponseEntity<InterviewSessionDto> getSession(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID sessionId) {
        return ResponseEntity.ok(getHistoryPort.getSessionById(user.id(), sessionId));
    }

    private InterviewType parseInterviewType(String type) {
        if (type == null || type.isBlank()) return InterviewType.HR_JAPANESE;
        try { return InterviewType.valueOf(type.toUpperCase()); }
        catch (IllegalArgumentException e) { return InterviewType.HR_JAPANESE; }
    }

    private InterviewDifficulty parseDifficulty(String difficulty) {
        if (difficulty == null || difficulty.isBlank()) return InterviewDifficulty.BEGINNER;
        try { return InterviewDifficulty.valueOf(difficulty.toUpperCase()); }
        catch (IllegalArgumentException e) { return InterviewDifficulty.BEGINNER; }
    }
}
