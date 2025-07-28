package com.lovedbug.geulgwi.external.image;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class LocalImageHandler implements ImageHandler {

    private final String STORAGE_ROOT = "/uploads/images";

    @Override
    public ImageMetaData saveImage(MultipartFile image) {
        try {
            String extension = getExtension(image);
            String imageName = UUID.randomUUID().toString().concat(extension);
            Path dirPath = Paths.get(STORAGE_ROOT);

            if (Files.notExists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            Path fullPath = dirPath.resolve(imageName);
            image.transferTo(fullPath);

            return  new ImageMetaData(STORAGE_ROOT, imageName);
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 실패, 이미지 이름 = " + image.getOriginalFilename());
        }
    }
}
