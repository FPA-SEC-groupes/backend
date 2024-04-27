package com.HelloWay.HelloWay.services;

import com.HelloWay.HelloWay.entities.Notification;
import com.HelloWay.HelloWay.entities.User;
import com.HelloWay.HelloWay.repos.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification createNotification(String title, String message, User user) {
        Notification notification = new Notification();
        notification.setNotificationTitle(title);
        notification.setMessage(message);
        notification.setRecipient(user);
        notification.setSeen(false);
        notification.setCreationDate(LocalDateTime.now());
        notificationRepository.save(notification);
        return notification;
    }

    public List<Notification> getNotificationsForRecipient(Long userId) {
        return notificationRepository.findByRecipientId(userId);
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    public Optional<Notification> getNotificationById(Long notificationId) {
        return notificationRepository.findById(notificationId);
    }

    public Notification updateNotification(Long notificationId, String title, String message, boolean seen) {
        Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);
        if (optionalNotification.isPresent()) {
            Notification notification = optionalNotification.get();
            notification.setNotificationTitle(title);
            notification.setMessage(message);
            notification.setSeen(seen);
            notificationRepository.save(notification);
            return notification;
        }
        return null; // Or throw an exception if the notification doesn't exist.
    }
}
