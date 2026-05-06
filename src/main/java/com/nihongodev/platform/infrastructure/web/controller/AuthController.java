package com.nihongodev.platform.infrastructure.web.controller;

import com.nihongodev.platform.application.command.LoginCommand;
import com.nihongodev.platform.application.command.RegisterCommand;
import com.nihongodev.platform.application.dto.AuthResponse;
import com.nihongodev.platform.application.port.in.LoginPort;
import com.nihongodev.platform.application.port.in.LogoutPort;
import com.nihongodev.platform.application.port.in.RefreshTokenPort;
import com.nihongodev.platform.application.port.in.RegisterUserPort;
import com.nihongodev.platform.domain.model.JapaneseLevel;
import com.nihongodev.platform.domain.model.Role;
import com.nihongodev.platform.infrastructure.security.AuthenticatedUser;
import com.nihongodev.platform.infrastructure.web.request.LoginRequest;
import com.nihongodev.platform.infrastructure.web.request.RefreshTokenRequest;
import com.nihongodev.platform.infrastructure.web.request.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Registration, login, token refresh, and logout")
public class AuthController {

    private final RegisterUserPort registerUserPort;
    private final LoginPort loginPort;
    private final RefreshTokenPort refreshTokenPort;
    private final LogoutPort logoutPort;

    public AuthController(RegisterUserPort registerUserPort,
                          LoginPort loginPort,
                          RefreshTokenPort refreshTokenPort,
                          LogoutPort logoutPort) {
        this.registerUserPort = registerUserPort;
        this.loginPort = loginPort;
        this.refreshTokenPort = refreshTokenPort;
        this.logoutPort = logoutPort;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterCommand command = new RegisterCommand(
                request.firstName(),
                request.lastName(),
                request.email(),
                request.password(),
                parseRole(request.role()),
                parseLevel(request.japaneseLevel()),
                request.objective()
        );
        AuthResponse response = registerUserPort.register(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Login with email and password")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginCommand command = new LoginCommand(request.email(), request.password());
        AuthResponse response = loginPort.login(command);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh access token")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = refreshTokenPort.refresh(request.refreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout and invalidate refresh tokens")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal AuthenticatedUser user) {
        logoutPort.logout(user.id());
        return ResponseEntity.noContent().build();
    }

    private Role parseRole(String role) {
        if (role == null || role.isBlank()) return Role.LEARNER;
        try { return Role.valueOf(role.toUpperCase()); }
        catch (IllegalArgumentException e) { return Role.LEARNER; }
    }

    private JapaneseLevel parseLevel(String level) {
        if (level == null || level.isBlank()) return JapaneseLevel.BEGINNER;
        try { return JapaneseLevel.valueOf(level.toUpperCase()); }
        catch (IllegalArgumentException e) { return JapaneseLevel.BEGINNER; }
    }
}
