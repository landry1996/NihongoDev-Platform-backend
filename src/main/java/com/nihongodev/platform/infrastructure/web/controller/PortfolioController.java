package com.nihongodev.platform.infrastructure.web.controller;

import com.nihongodev.platform.application.command.CreatePublicProfileCommand;
import com.nihongodev.platform.application.command.UpdatePublicProfileCommand;
import com.nihongodev.platform.application.dto.BadgeDto;
import com.nihongodev.platform.application.dto.PublicProfileDto;
import com.nihongodev.platform.application.dto.UserBadgeDto;
import com.nihongodev.platform.application.port.in.*;
import com.nihongodev.platform.domain.model.BadgeCategory;
import com.nihongodev.platform.infrastructure.security.AuthenticatedUser;
import com.nihongodev.platform.infrastructure.web.request.CreatePublicProfileRequest;
import com.nihongodev.platform.infrastructure.web.request.UpdatePublicProfileRequest;
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
@RequestMapping("/api/portfolio")
@Tag(name = "Portfolio", description = "Public profile and badge management for learners")
public class PortfolioController {

    private final CreatePublicProfilePort createProfilePort;
    private final UpdatePublicProfilePort updateProfilePort;
    private final GetPublicProfilePort getProfilePort;
    private final GetUserBadgesPort getUserBadgesPort;
    private final GetBadgeCatalogPort getBadgeCatalogPort;
    private final ShowcaseBadgePort showcaseBadgePort;
    private final ToggleOpenToWorkPort toggleOpenToWorkPort;

    public PortfolioController(CreatePublicProfilePort createProfilePort,
                                UpdatePublicProfilePort updateProfilePort,
                                GetPublicProfilePort getProfilePort,
                                GetUserBadgesPort getUserBadgesPort,
                                GetBadgeCatalogPort getBadgeCatalogPort,
                                ShowcaseBadgePort showcaseBadgePort,
                                ToggleOpenToWorkPort toggleOpenToWorkPort) {
        this.createProfilePort = createProfilePort;
        this.updateProfilePort = updateProfilePort;
        this.getProfilePort = getProfilePort;
        this.getUserBadgesPort = getUserBadgesPort;
        this.getBadgeCatalogPort = getBadgeCatalogPort;
        this.showcaseBadgePort = showcaseBadgePort;
        this.toggleOpenToWorkPort = toggleOpenToWorkPort;
    }

    @PostMapping("/profile")
    @Operation(summary = "Create public profile")
    public ResponseEntity<PublicProfileDto> createProfile(
            @Valid @RequestBody CreatePublicProfileRequest request,
            @AuthenticationPrincipal AuthenticatedUser user) {
        var command = new CreatePublicProfileCommand(
            user.id(), request.displayName(), request.slug(),
            request.bio(), request.currentLevel(), request.visibility(),
            request.openToWork(), request.preferredRole(), request.location(),
            request.highlightedSkills()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(createProfilePort.execute(command));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update public profile")
    public ResponseEntity<PublicProfileDto> updateProfile(
            @Valid @RequestBody UpdatePublicProfileRequest request,
            @AuthenticationPrincipal AuthenticatedUser user) {
        var command = new UpdatePublicProfileCommand(
            user.id(), request.displayName(), request.bio(),
            request.currentLevel(), request.visibility(),
            request.openToWork(), request.preferredRole(), request.location(),
            request.highlightedSkills()
        );
        return ResponseEntity.ok(updateProfilePort.execute(command));
    }

    @GetMapping("/profile")
    @Operation(summary = "Get own public profile")
    public ResponseEntity<PublicProfileDto> getMyProfile(
            @AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(getProfilePort.getByUserId(user.id()));
    }

    @PostMapping("/profile/toggle-open-to-work")
    @Operation(summary = "Toggle open to work status")
    public ResponseEntity<PublicProfileDto> toggleOpenToWork(
            @AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(toggleOpenToWorkPort.execute(user.id()));
    }

    @GetMapping("/badges")
    @Operation(summary = "Get user's earned badges")
    public ResponseEntity<List<UserBadgeDto>> getMyBadges(
            @AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(getUserBadgesPort.execute(user.id()));
    }

    @GetMapping("/badges/catalog")
    @Operation(summary = "Get all available badges")
    public ResponseEntity<List<BadgeDto>> getBadgeCatalog(
            @RequestParam(required = false) BadgeCategory category) {
        if (category != null) {
            return ResponseEntity.ok(getBadgeCatalogPort.getByCategory(category));
        }
        return ResponseEntity.ok(getBadgeCatalogPort.getAll());
    }

    @PostMapping("/badges/{badgeId}/showcase")
    @Operation(summary = "Showcase a badge on public profile")
    public ResponseEntity<Void> showcaseBadge(
            @PathVariable UUID badgeId,
            @AuthenticationPrincipal AuthenticatedUser user) {
        showcaseBadgePort.showcase(user.id(), badgeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/badges/{badgeId}/showcase")
    @Operation(summary = "Remove badge from public profile showcase")
    public ResponseEntity<Void> unshowcaseBadge(
            @PathVariable UUID badgeId,
            @AuthenticationPrincipal AuthenticatedUser user) {
        showcaseBadgePort.unshowcase(user.id(), badgeId);
        return ResponseEntity.ok().build();
    }
}
