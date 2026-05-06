package com.nihongodev.platform.infrastructure.web.controller;

import com.nihongodev.platform.application.command.ChangePasswordCommand;
import com.nihongodev.platform.application.command.UpdateProfileCommand;
import com.nihongodev.platform.application.dto.UserDto;
import com.nihongodev.platform.application.port.in.ChangePasswordPort;
import com.nihongodev.platform.application.port.in.GetUserProfilePort;
import com.nihongodev.platform.application.port.in.UpdateUserProfilePort;
import com.nihongodev.platform.domain.model.JapaneseLevel;
import com.nihongodev.platform.infrastructure.security.AuthenticatedUser;
import com.nihongodev.platform.infrastructure.web.request.ChangePasswordRequest;
import com.nihongodev.platform.infrastructure.web.request.UpdateProfileRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User profile management")
public class UserController {

    private final GetUserProfilePort getUserProfilePort;
    private final UpdateUserProfilePort updateUserProfilePort;
    private final ChangePasswordPort changePasswordPort;

    public UserController(GetUserProfilePort getUserProfilePort,
                          UpdateUserProfilePort updateUserProfilePort,
                          ChangePasswordPort changePasswordPort) {
        this.getUserProfilePort = getUserProfilePort;
        this.updateUserProfilePort = updateUserProfilePort;
        this.changePasswordPort = changePasswordPort;
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<UserDto> getMyProfile(@AuthenticationPrincipal AuthenticatedUser user) {
        UserDto profile = getUserProfilePort.getProfile(user.id());
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/me")
    @Operation(summary = "Update current user profile")
    public ResponseEntity<UserDto> updateMyProfile(@AuthenticationPrincipal AuthenticatedUser user,
                                                   @Valid @RequestBody UpdateProfileRequest request) {
        JapaneseLevel level = null;
        if (request.japaneseLevel() != null) {
            try { level = JapaneseLevel.valueOf(request.japaneseLevel().toUpperCase()); }
            catch (IllegalArgumentException ignored) {}
        }

        UpdateProfileCommand command = new UpdateProfileCommand(
                user.id(), request.firstName(), request.lastName(), level, request.objective()
        );
        UserDto updated = updateUserProfilePort.updateProfile(command);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/me/change-password")
    @Operation(summary = "Change current user password")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal AuthenticatedUser user,
                                               @Valid @RequestBody ChangePasswordRequest request) {
        ChangePasswordCommand command = new ChangePasswordCommand(
                user.id(), request.currentPassword(), request.newPassword()
        );
        changePasswordPort.changePassword(command);
        return ResponseEntity.noContent().build();
    }
}
