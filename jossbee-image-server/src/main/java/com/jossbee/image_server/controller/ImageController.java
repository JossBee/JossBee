package com.jossbee.image_server.controller;

import com.jossbee.image_server.image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile multipartImage, @RequestParam("width") int width,
                                              @RequestParam("height") int height) {
        return ResponseEntity.ok(imageService.uploadImage(multipartImage, width, height));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteImage(@RequestParam("imageUrl") String imageUrl) {
        imageService.deleteImage(imageUrl);
        return ResponseEntity.ok("Image deleted successfully");
    }
}
