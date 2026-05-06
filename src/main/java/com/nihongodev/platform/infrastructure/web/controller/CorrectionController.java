package com.nihongodev.platform.infrastructure.web.controller;

import com.nihongodev.platform.application.command.CorrectTextCommand;
import com.nihongodev.platform.application.dto.CorrectionSessionDto;
import com.nihongodev.platform.application.dto.WeaknessReportDto;
import com.nihongodev.platform.application.port.in.CorrectTextPort;
import com.nihongodev.platform.application.port.in.GetCorrectionHistoryPort;
import com.nihongodev.platform.application.port.in.GetWeaknessReportPort;
import com.nihongodev.platform.infrastructure.security.AuthenticatedUser;
import com.nihongodev.platform.infrastructure.web.request.CorrectTextRequest;
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
@RequestMapping("/api/corrections")
@Tag(name = "Correction", description = "Intelligent text correction with multi-dimensional scoring and weakness tracking")
public class CorrectionController {

    private final CorrectTextPort correctTextPort;
    private final GetCorrectionHistoryPort getHistoryPort;
    private final GetWeaknessReportPort getWeaknessReportPort;

    public CorrectionController(CorrectTextPort correctTextPort,
                                GetCorrectionHistoryPort getHistoryPort,
                                GetWeaknessReportPort getWeaknessReportPort) {
        this.correctTextPort = correctTextPort;
        this.getHistoryPort = getHistoryPort;
        this.getWeaknessReportPort = getWeaknessReportPort;
    }

    @PostMapping
    @Operation(summary = "Submit text for correction and scoring")
    public ResponseEntity<CorrectionSessionDto> correctText(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody CorrectTextRequest request) {
        CorrectTextCommand command = new CorrectTextCommand(
                request.text(), request.textType(), request.targetLevel()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(correctTextPort.correct(user.id(), command));
    }

    @GetMapping("/history")
    @Operation(summary = "Get user's correction history")
    public ResponseEntity<List<CorrectionSessionDto>> getHistory(
            @AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(getHistoryPort.getUserHistory(user.id()));
    }

    @GetMapping("/{sessionId}")
    @Operation(summary = "Get a specific correction session")
    public ResponseEntity<CorrectionSessionDto> getSession(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID sessionId) {
        return ResponseEntity.ok(getHistoryPort.getSessionById(user.id(), sessionId));
    }

    @GetMapping("/weakness-report")
    @Operation(summary = "Get user's weakness patterns report")
    public ResponseEntity<WeaknessReportDto> getWeaknessReport(
            @AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(getWeaknessReportPort.getReport(user.id()));
    }
}
