package com.jossbee.image_server.service;

import com.jossbee.image_server.exception.ServiceException;
import com.jossbee.image_server.image.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.UUID;

import static org.imgscalr.Scalr.Method.ULTRA_QUALITY;
import static org.imgscalr.Scalr.Mode.AUTOMATIC;
import static org.imgscalr.Scalr.OP_ANTIALIAS;
import static software.amazon.awssdk.services.s3.model.ObjectCannedACL.PUBLIC_READ;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    @Value("${s3.bucketName}")
    private String bucketName;

    private final S3Client s3Client;

    public String uploadImage(MultipartFile multipartImage, int width, int height) {
        File imageAfterProcessing = convertMultipartFileToFile(multipartImage);

        try {
            imageAfterProcessing = resizedImage(imageAfterProcessing, multipartImage.getOriginalFilename(),
                    width, height);
        } catch (IOException e) {
            log.error("Error while processing image, error is: {}", e.getMessage(), e);
        }

        return uploadImageToBucket(imageAfterProcessing, multipartImage.getOriginalFilename());
    }

    public void deleteImage(String imageUrl) {

        URI uri;

        try {
            uri = new URI(imageUrl);
        } catch (URISyntaxException e) {
            throw new ServiceException("Failed to parse the uri, error occurred. Error is: " + e.getMessage());
        }

        String key = uri.getPath();
        if (key.startsWith("/")) {
            key = key.substring(1);
        }

        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build());
    }

    private String uploadImageToBucket(File imageFile, String originalFilename) {
        UUID uuid = UUID.randomUUID();
        String imageFileNewName = uuid + originalFilename;

        String contentType = determineContentType(originalFilename);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(imageFileNewName)
                .acl(PUBLIC_READ)
                .contentType(contentType)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromFile(imageFile));

        return s3Client.utilities()
                .getUrl(GetUrlRequest.builder()
                        .bucket(bucketName)
                        .key(imageFileNewName)
                        .build())
                .toString();
    }

    private String determineContentType(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            default -> throw new ServiceException("Unsupported image format: " + extension);
        };
    }

    private File convertMultipartFileToFile(MultipartFile file) {
        File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));

        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            log.error("Failed to convert multipart file, error occurred. Error is: {}", e.getMessage(), e);
        }

        return convertedFile;
    }

    private File resizedImage(File imageFile, String originalFileName, int width, int height) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(imageFile);

        BufferedImage resizedImage = Scalr.resize(bufferedImage, ULTRA_QUALITY, AUTOMATIC, width, height, OP_ANTIALIAS);

        return convertBufferedImageToFile(resizedImage, originalFileName);
    }

    private File convertBufferedImageToFile(BufferedImage resizedImage, String originalFileName) {
        File outputFile = null;

        try {
            outputFile = new File(originalFileName);
            ImageIO.write(resizedImage, "png", outputFile);
        } catch (IOException e) {
            log.error("Error converting buffered image", e);
        }

        return outputFile;
    }
}