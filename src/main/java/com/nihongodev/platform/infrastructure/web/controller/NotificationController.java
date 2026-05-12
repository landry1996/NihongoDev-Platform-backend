package com.nihongodev.platform.infrastructure.web.controller;

import com.nihongodev.platform.application.dto.NotificationDto;
import com.nihongodev.platform.application.port.in.GetNotificationsPort;
import com.nihongodev.platform.application.port.in.MarkNotificationReadPort;
import com.nihongodev.platform.infrastructure.security.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "User notification management")
public class NotificationController {

    private final GetNotificationsPort getNotificationsPort;
    private final MarkNotificationReadPort markNotificationReadPort;

    public NotificationController(GetNotificationsPort getNotificationsPort,
                                  MarkNotificationReadPort markNotificationReadPort) {
        this.getNotificationsPort = getNotificationsPort;
        this.markNotificationReadPort = markNotificationReadPort;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all notifications")
    public ResponseEntity<List<NotificationDto>> getAll(@AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(getNotificationsPort.getAll(user.id()));
    }

    @GetMapping("/unread")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get unread notifications")
    public ResponseEntity<List<NotificationDto>> getUnread(@AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(getNotificationsPort.getUnread(user.id()));
    }

    @GetMapping("/unread/count")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Count unread notifications")
    public ResponseEntity<Map<String, Long>> countUnread(@AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(Map.of("count", getNotificationsPort.countUnread(user.id())));
    }

    @PatchMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Mark notification as read")
    public ResponseEntity<Void> markAsRead(@PathVariable UUID id, @AuthenticationPrincipal AuthenticatedUser user) {
        markNotificationReadPort.markAsRead(id, user.id());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/read-all")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Mark all notifications as read")
    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal AuthenticatedUser user) {
        markNotificationReadPort.markAllAsRead(user.id());
        return ResponseEntity.noContent().build();
    }
}
