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
    private final FCMService fcmService;
    public NotificationService(NotificationRepository notificationRepository, FCMService fcmService) {
        this.notificationRepository = notificationRepository;
        this.fcmService=fcmService;
    }

    public Notification createNotification(String title, String message, User user) {
        try{
            Notification notification = new Notification();
        notification.setNotificationTitle(title);
        notification.setMessage(message);
        notification.setRecipient(user);
        notification.setSeen(false);
        notification.setCreationDate(LocalDateTime.now());
        notificationRepository.save(notification);
        fcmService.sendNotification(user.getToken(),title,message);
        return notification;
        }catch(Exception e){
            Notification notification = new Notification();
            notification.setNotificationTitle("error");
            notification.setMessage(e.getMessage());
            notification.setRecipient(user);
            notification.setSeen(false);
            notification.setCreationDate(LocalDateTime.now());
            notificationRepository.save(notification);
            return notification;
        }
        
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
