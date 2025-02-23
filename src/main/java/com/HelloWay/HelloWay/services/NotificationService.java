package com.HelloWay.HelloWay.services;

import com.HelloWay.HelloWay.entities.Notification;
import com.HelloWay.HelloWay.entities.User;
import com.HelloWay.HelloWay.payload.request.NotificationDTO;
import com.HelloWay.HelloWay.repos.NotificationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final FCMService fcmService;
    @Autowired
    private MessageSource messageSource;
    public NotificationService(NotificationRepository notificationRepository, FCMService fcmService) {
        this.notificationRepository = notificationRepository;
        this.fcmService=fcmService;
    }

    public Notification createNotification(String titleKey, String messageKey, List<String> params, User user) {
        Notification notification = new Notification();
        Locale userLocale = new Locale(user.getPreferredLanguage());
        String[] paramsArray = params.toArray(new String[0]);

        String translatedTitle;
        String translatedMessageTemplate;
        String formattedMessage;

        try {
            // Try to get translations from message properties
            translatedTitle = messageSource.getMessage(titleKey, null, userLocale);
            translatedMessageTemplate = messageSource.getMessage(messageKey, null, userLocale);
        } catch (Exception e) {
            // If translation fails, use the message key itself
            translatedTitle = titleKey;
            translatedMessageTemplate = messageKey;
        }

        // Format message if parameters exist
        try {
            formattedMessage = MessageFormat.format(translatedMessageTemplate, (Object[]) paramsArray);
        } catch (Exception e) {
            formattedMessage = translatedMessageTemplate; // Use unformatted if error occurs
        }

        // Set notification data
        notification.setNotificationTitleKey(titleKey);
        notification.setMessageKey(messageKey);
        notification.setMessageParameters(params);
        notification.setNotificationTitle(translatedTitle);
        notification.setMessage(formattedMessage);
        notification.setRecipient(user);
        notification.setSeen(false);
        notification.setCreationDate(LocalDateTime.now());
        notificationRepository.save(notification);

        // Send notification via FCM
        fcmService.sendNotification(user.getToken(), translatedTitle, formattedMessage);

        return notification;
    }
    // public List<Notification> getNotificationsForRecipient(Long userId) {
    //     return notificationRepository.findByRecipientId(userId);
    // }
    public List<NotificationDTO> getNotificationsForRecipient(Long userId) {
        List<Notification> notifications = notificationRepository.findByRecipientId(userId);
        return notifications.stream().map(notification -> {
            User recipient = notification.getRecipient();
            Locale userLocale = new Locale(recipient.getPreferredLanguage());
            String title = messageSource.getMessage(notification.getNotificationTitleKey(), null, userLocale);
            String message = messageSource.getMessage(notification.getMessageKey(), notification.getMessageParameters().toArray(), userLocale);
            NotificationDTO dto = new NotificationDTO();
            dto.setId(notification.getId());
            dto.setNotificationTitle(title);
            dto.setMessage(message);
            dto.setCreationDate(notification.getCreationDate());
            dto.setSeen(notification.isSeen());
            return dto;
        }).collect(Collectors.toList());
    }
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }
    public void markAllNotificationsAsSeen(Long userId) {
        List<Notification> notifications = notificationRepository.findByRecipientId(userId);
        for (Notification notification : notifications) {
            notification.setSeen(true);
        }
        notificationRepository.saveAll(notifications);
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
