package com.ecommerce.util;

import org.springframework.http.MediaType;

public enum ImagesFormat {
    JPEG(MediaType.IMAGE_JPEG.toString()), PNG(MediaType.IMAGE_PNG.toString()), WEBP("image/webp"), JPG("image/jpg");
    private final String mediaType;

    ImagesFormat(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getMediaType() {
        return mediaType;
    }
}
