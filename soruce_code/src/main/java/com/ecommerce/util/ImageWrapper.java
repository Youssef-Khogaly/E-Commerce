package com.ecommerce.util;

import com.ecommerce.Exception.InvalidImageException;
import org.apache.tika.mime.MimeType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class ImageWrapper {
    private final String originalName;
    private final byte[] bytes;
    private  final String contentType;
    private  final String extension;
    public ImageWrapper(String originalName, byte[] bytes) {
        this.bytes = bytes;
        this.originalName = originalName;
        MimeType mediaType = ImageUtil.getMediaType(bytes);
        this.contentType = mediaType.getName();
        this.extension = mediaType.getExtension();
    }

    public ImageWrapper(String originalName, byte[] bytes, String contentType, String extension) {
        this.originalName = originalName;
        this.bytes = bytes;
        this.contentType = contentType;
        this.extension = extension;
    }

    public String getOriginalName() {
        return originalName;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public String getContentType() {
        return contentType;
    }

    public String getExtension() {
        return extension;
    }


    public long getSize(){
        return bytes.length;
    }

    public static ImageWrapper fromMultiPart(MultipartFile multipartFile)  {
        try{
            return new ImageWrapper(multipartFile.getOriginalFilename(),multipartFile.getBytes());
        } catch (IOException e) {
            throw new InvalidImageException("invalid image bytes");
        }
    }
}
