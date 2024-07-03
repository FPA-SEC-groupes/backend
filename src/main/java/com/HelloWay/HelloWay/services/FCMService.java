package com.HelloWay.HelloWay.services;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import org.springframework.stereotype.Service;

@Service
public class FCMService {

    public void sendNotification(String token, String title, String body) {
        // Create a Notification object using the builder pattern
        Notification notification = Notification.builder()
            .setTitle(title)
            .setBody(body)
            .build();

        // Build the message with the notification object
        Message message = Message.builder()
            .setNotification(notification)
            .setToken(token)
            .build();

        try {
            // Send the message and capture the response
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
