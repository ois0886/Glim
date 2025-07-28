package com.lovedbug.geulgwi.external.image;

import org.springframework.web.multipart.MultipartFile;

public interface ImageHandler {

    ImageMetaData saveImage(MultipartFile image);

    default String getExtension(MultipartFile image) {
        return image.getOriginalFilename()
            .substring(image.getOriginalFilename().lastIndexOf("."));
    }
}

