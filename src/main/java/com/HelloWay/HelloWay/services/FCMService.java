package com.HelloWay.HelloWay.services;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

@Service
public class FCMService {

    public void sendNotification(String token, String title, String body) {
        if (token == null || token.isEmpty()) {
            System.err.println("❌ FCM Error: Token is null or empty. Notification not sent.");
            return;
        }

        // Create a Notification object for display notifications
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        // Build the message with the notification and data payload
        Message message = Message.builder()
                .setNotification(notification) // Display notification
                .putData("title", title) // Data payload for background handling
                .putData("body", body)
                .setToken(token)
                .build();

        try {
            // Send the message and capture the response
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("✅ Successfully sent FCM message: " + response);
        } catch (Exception e) {
            System.err.println("❌ FCM Error: Failed to send notification.");
            e.printStackTrace(); // Print detailed error
        }
    }
}
