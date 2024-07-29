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

    public Notification createNotification(String title, String message,List<String> parames, User user) {
        try{
        Notification notification = new Notification();
        Locale userLocale = new Locale(user.getPreferredLanguage());
        String[] paramsArray = parames.toArray(new String[0]);
        String title1 = messageSource.getMessage("ntComandeUpdate", null, userLocale);
        String message1 = messageSource.getMessage("ntComandeUpdate", null, userLocale);
        String formattedMessage = MessageFormat.format(message1, (Object[]) paramsArray);
        notification.setNotificationTitleKey(title);
        notification.setMessageKey(message);
        notification.setMessageParameters(parames);
        notification.setNotificationTitle(title1);
        notification.setMessage(message1);
        notification.setRecipient(user);
        notification.setSeen(false);
        notification.setCreationDate(LocalDateTime.now());
        notificationRepository.save(notification);
        fcmService.sendNotification(user.getToken(),title1,formattedMessage);
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
