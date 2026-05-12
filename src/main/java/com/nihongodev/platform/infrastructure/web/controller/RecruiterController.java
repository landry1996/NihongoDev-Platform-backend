package com.nihongodev.platform.infrastructure.web.controller;

import com.nihongodev.platform.application.dto.PublicProfileDto;
import com.nihongodev.platform.application.dto.RecruiterSearchResultDto;
import com.nihongodev.platform.application.port.in.GetPublicProfilePort;
import com.nihongodev.platform.application.port.in.SearchProfilesPort;
import com.nihongodev.platform.domain.model.JapaneseLevel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recruiter")
@PreAuthorize("hasRole('RECRUITER')")
@Tag(name = "Recruiter", description = "Recruiter-facing search and profile viewing")
public class RecruiterController {

    private final SearchProfilesPort searchProfilesPort;
    private final GetPublicProfilePort getProfilePort;

    public RecruiterController(SearchProfilesPort searchProfilesPort,
                                GetPublicProfilePort getProfilePort) {
        this.searchProfilesPort = searchProfilesPort;
        this.getProfilePort = getProfilePort;
    }

    @GetMapping("/search")
    @Operation(summary = "Search developer profiles by criteria")
    public ResponseEntity<RecruiterSearchResultDto> searchProfiles(
            @RequestParam(required = false) JapaneseLevel minLevel,
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) Boolean openToWork,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(
            searchProfilesPort.search(minLevel, skill, openToWork, page, pageSize));
    }

    @GetMapping("/profiles/{slug}")
    @Operation(summary = "View a developer's public profile")
    public ResponseEntity<PublicProfileDto> getProfile(@PathVariable String slug) {
        return ResponseEntity.ok(getProfilePort.getBySlug(slug, true));
    }
}
