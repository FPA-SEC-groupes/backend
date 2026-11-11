package com.HelloWay.HelloWay.config;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUploadUtil {
     
    public static void saveFile(String uploadDir, String fileName,
            MultipartFile multipartFile)  {
        Path uploadPath = Paths.get(uploadDir);
         
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new RuntimeException("Could not create Folders: " + fileName, e);
            }
        }
         
        // Refuse les noms de fichiers qui sortent du dossier (../, chemins absolus...)
        Path base = uploadPath.toAbsolutePath().normalize();
        Path filePath = base.resolve(fileName).normalize();
        if (!base.equals(filePath.getParent())) {
            throw new IllegalArgumentException("Invalid file name: " + fileName);
        }
        String type = multipartFile.getContentType();
        if (type == null || !type.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed.");
        }

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {        
            throw new RuntimeException("Could not save image file: " + fileName, ioe);
        }      
    }
}