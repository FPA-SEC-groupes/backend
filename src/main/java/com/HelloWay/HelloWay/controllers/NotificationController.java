package com.HelloWay.HelloWay.controllers;


import com.HelloWay.HelloWay.entities.Notification;
import com.HelloWay.HelloWay.entities.User;
import com.HelloWay.HelloWay.payload.request.NotificationDTO;
import com.HelloWay.HelloWay.services.NotificationService;
import com.HelloWay.HelloWay.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {


    @Autowired
    NotificationService notificationService;

    @Autowired
    private  UserService userService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('PROVIDER', 'USER', 'GUEST')")
    public ResponseEntity<Notification> createNotification(@RequestParam String title,
                                                           @RequestParam String message,
                                                           @RequestParam Long userId) {
        List<String>parames= new ArrayList<>();
        User user = userService.findUserById(userId);
        Notification notification = notificationService.createNotification(title, message,parames, user);
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
    public List<NotificationDTO> getNotificationsForProvider(@PathVariable Long userId) {
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
    @PutMapping("/providers/{userId}/mark-seen")
@PreAuthorize("hasAnyRole('PROVIDER', 'USER', 'GUEST', 'WAITER')")
public ResponseEntity<?> markAllNotificationsAsSeen(@PathVariable Long userId) {
    try {
        notificationService.markAllNotificationsAsSeen(userId);
        return ResponseEntity.ok("All notifications marked as seen");
    } catch (Exception e) {
        return ResponseEntity.internalServerError().body("Error marking notifications as seen: " + e.getMessage());
    }
}

}
