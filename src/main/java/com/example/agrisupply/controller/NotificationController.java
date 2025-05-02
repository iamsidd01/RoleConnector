package com.example.agrisupply.controller;

import com.example.agrisupply.model.Notification;
import com.example.agrisupply.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()") // Accessible by any authenticated user
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<Notification>> getMyNotifications() {
        List<Notification> notifications = notificationService.getMyNotifications();
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getMyUnreadNotifications() {
        List<Notification> notifications = notificationService.getMyUnreadNotifications();
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable String notificationId) {
        // Exception handling for NotFound, Forbidden is typically done via a global handler
        notificationService.markNotificationAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

     @PostMapping("/read-all")
    public ResponseEntity<Void> markAllNotificationsAsRead() {
        notificationService.markAllMyNotificationsAsRead();
        return ResponseEntity.ok().build();
    }
}
