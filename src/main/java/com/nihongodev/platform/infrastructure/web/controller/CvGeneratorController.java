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
