package com.HelloWay.HelloWay.services;

import com.HelloWay.HelloWay.entities.Image;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {
    Image getImage(String id);

    Image addImage(MultipartFile image) throws IOException;
    Image addImageLa(Image image);
}
