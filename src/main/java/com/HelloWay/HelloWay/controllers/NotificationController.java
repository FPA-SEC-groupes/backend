package com.HelloWay.HelloWay.controllers;


import com.HelloWay.HelloWay.entities.Notification;
import com.HelloWay.HelloWay.entities.User;
import com.HelloWay.HelloWay.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {


    @Autowired
    NotificationService notificationService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('PROVIDER', 'USER', 'GUEST')")
    public ResponseEntity<Notification> createNotification(@RequestParam String title,
                                                           @RequestParam String message,
                                                           @RequestBody User user) {
        Notification notification = notificationService.createNotification(title, message, user);
        return ResponseEntity.ok(notification);
    }
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('PROVIDER', 'USER', 'GUEST')")
    public List<Notification> getAllNotifications() {
        return notificationService.getAllNotifications();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROVIDER', 'USER', 'GUEST')")
    public void deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
    }

    @GetMapping("/providers/{userId}/notifications")
    @PreAuthorize("hasAnyRole('PROVIDER', 'USER', 'GUEST','WAITER')")
    public List<Notification> getNotificationsForProvider(@PathVariable Long userId) {
        return notificationService.getNotificationsForRecipient(userId);
    }

    @PutMapping("/{notificationId}")
    @PreAuthorize("hasAnyRole('PROVIDER','WAITER','notification')")
    public ResponseEntity<Notification> updateNotification(
            @PathVariable Long notificationId,
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam boolean seen
    ) {
        Notification updatedNotification = notificationService.updateNotification(notificationId, title, message, seen);
        if (updatedNotification != null) {
            return ResponseEntity.ok(updatedNotification);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
