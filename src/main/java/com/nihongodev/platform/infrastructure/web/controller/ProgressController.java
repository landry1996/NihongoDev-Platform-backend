package com.nihongodev.platform.infrastructure.web.controller;

import com.nihongodev.platform.application.dto.*;
import com.nihongodev.platform.application.port.in.*;
import com.nihongodev.platform.domain.model.ModuleType;
import com.nihongodev.platform.infrastructure.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/progress")
@Tag(name = "Progress", description = "User progress tracking and statistics")
public class ProgressController {

    private final GetUserProgressPort getUserProgressPort;
    private final GetModuleProgressPort getModuleProgressPort;
    private final GetUserActivityHistoryPort getActivityHistoryPort;
    private final GetUserStatisticsPort getUserStatisticsPort;

    public ProgressController(GetUserProgressPort getUserProgressPort,
                              GetModuleProgressPort getModuleProgressPort,
                              GetUserActivityHistoryPort getActivityHistoryPort,
                              GetUserStatisticsPort getUserStatisticsPort) {
        this.getUserProgressPort = getUserProgressPort;
        this.getModuleProgressPort = getModuleProgressPort;
        this.getActivityHistoryPort = getActivityHistoryPort;
        this.getUserStatisticsPort = getUserStatisticsPort;
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user's global progress")
    public ResponseEntity<UserProgressDto> getMyProgress(@AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(getUserProgressPort.execute(user.id()));
    }

    @GetMapping("/me/modules")
    @Operation(summary = "Get progress for all modules")
    public ResponseEntity<List<ModuleProgressDto>> getMyModules(@AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(getModuleProgressPort.getAll(user.id()));
    }

    @GetMapping("/me/modules/{type}")
    @Operation(summary = "Get progress for a specific module")
    public ResponseEntity<ModuleProgressDto> getMyModuleByType(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable String type) {
        ModuleType moduleType = ModuleType.valueOf(type.toUpperCase());
        return ResponseEntity.ok(getModuleProgressPort.getByType(user.id(), moduleType));
    }

    @GetMapping("/me/activities")
    @Operation(summary = "Get learning activity history (paginated)")
    public ResponseEntity<Page<LearningActivityDto>> getMyActivities(
            @AuthenticationPrincipal AuthenticatedUser user,
            Pageable pageable) {
        return ResponseEntity.ok(getActivityHistoryPort.execute(user.id(), pageable));
    }

    @GetMapping("/me/statistics")
    @Operation(summary = "Get calculated statistics, trends, and recommendations")
    public ResponseEntity<UserStatisticsDto> getMyStatistics(@AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(getUserStatisticsPort.execute(user.id()));
    }

    @GetMapping("/me/weak-areas")
    @Operation(summary = "Get identified weak areas")
    public ResponseEntity<List<WeakAreaDto>> getMyWeakAreas(@AuthenticationPrincipal AuthenticatedUser user) {
        UserStatisticsDto stats = getUserStatisticsPort.execute(user.id());
        return ResponseEntity.ok(stats.weakAreas());
    }

    @GetMapping("/me/recommendations")
    @Operation(summary = "Get personalized learning recommendations")
    public ResponseEntity<List<RecommendationDto>> getMyRecommendations(@AuthenticationPrincipal AuthenticatedUser user) {
        UserStatisticsDto stats = getUserStatisticsPort.execute(user.id());
        return ResponseEntity.ok(stats.recommendations());
    }
}
