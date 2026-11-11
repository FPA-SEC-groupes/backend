package com.HelloWay.HelloWay.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Notifications push : la clé du compte de service Firebase n'est plus dans le code.
 * Chemin donné par FIREBASE_CREDENTIALS (fichier hors Git). Sans fichier, l'API démarre
 * et les notifications push sont simplement désactivées.
 */
@Configuration
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${firebase.credentials-path:}")
    private String credentialsPath;

    @PostConstruct
    public void initializeFirebase() throws IOException {
        if (credentialsPath == null || credentialsPath.isBlank()) {
            logger.warn("FIREBASE_CREDENTIALS non défini : notifications push désactivées.");
            return;
        }
        Path path = Paths.get(credentialsPath);
        if (!Files.exists(path)) {
            logger.warn("Fichier Firebase introuvable ({}) : notifications push désactivées.", path);
            return;
        }
        if (!FirebaseApp.getApps().isEmpty()) {
            return;
        }
        try (InputStream serviceAccount = new FileInputStream(path.toFile())) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options);
        }
    }
}
