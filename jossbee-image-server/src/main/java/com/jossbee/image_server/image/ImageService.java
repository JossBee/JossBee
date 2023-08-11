package com.jossbee.image_server.image;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    String uploadImage(MultipartFile multipartImage, int width, int height);

    void deleteImage(String imageUrl);
}
