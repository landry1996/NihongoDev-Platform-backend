package com.nihongodev.platform.infrastructure.web.controller;

import com.nihongodev.platform.application.dto.PlatformAnalyticsDto;
import com.nihongodev.platform.application.dto.TopUserDto;
import com.nihongodev.platform.application.port.in.GetPlatformAnalyticsPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/analytics")
@Tag(name = "Analytics", description = "Platform analytics for administrators")
public class AnalyticsController {

    private final GetPlatformAnalyticsPort getPlatformAnalyticsPort;

    public AnalyticsController(GetPlatformAnalyticsPort getPlatformAnalyticsPort) {
        this.getPlatformAnalyticsPort = getPlatformAnalyticsPort;
    }

    @GetMapping("/overview")
    @Operation(summary = "Get platform overview analytics")
    public ResponseEntity<PlatformAnalyticsDto> getOverview() {
        return ResponseEntity.ok(getPlatformAnalyticsPort.getOverview());
    }

    @GetMapping("/top-users")
    @Operation(summary = "Get top users by XP")
    public ResponseEntity<List<TopUserDto>> getTopUsers(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(getPlatformAnalyticsPort.getTopUsers(limit));
    }
}
