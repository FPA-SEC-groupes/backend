package com.HelloWay.HelloWay.services;

import com.HelloWay.HelloWay.entities.Image;
import com.HelloWay.HelloWay.repos.ImageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@AllArgsConstructor
public class ImageServiceImp implements ImageService {
    private final ImageRepository imageRepository;

    // Complete this method :: done
    @Override
    public Image getImage(String id) {
        return imageRepository.findById(id).orElse(null);
    }



    @Override
    public Image addImage(MultipartFile image) throws IOException {
        String fileName = StringUtils.cleanPath(image.getOriginalFilename());
        String fileType = image.getContentType();
        byte[] data = image.getBytes();
        Image img = new Image(null, fileName, fileType, data);
        return imageRepository.save(img);
    }

    @Override
    public Image addImageLa(Image image) {
        return imageRepository.save(image);
    }
}
