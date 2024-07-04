package com.HelloWay.HelloWay.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {
    @Bean
public FirebaseApp initializeFirebase() throws IOException {
    // Load the Firebase config file from the classpath
    ClassPathResource resource = new ClassPathResource("helloway-fd5cd-firebase-adminsdk-fq74d-368296d91e.json");
    InputStream serviceAccount = resource.getInputStream();

    // Initialize Firebase
    FirebaseOptions options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .build();

    return FirebaseApp.initializeApp(options);
}
}
