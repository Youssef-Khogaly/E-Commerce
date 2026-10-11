package com.ecommerce.util;

import com.ecommerce.Exception.InvalidImageException;
import org.apache.tika.Tika;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageUtil {

    private static final Tika tika = new Tika();

    static String getImageExtention(byte[] image){
        if(image == null || image.length == 0)
            throw new InvalidImageException("empty or  null image");
        String mediaType = tika.detect(image);
        try{
            MimeType type = MimeTypes.getDefaultMimeTypes().forName(mediaType);
            return  type.getExtension();

        } catch (MimeTypeException e) {
            throw new InvalidImageException("invalid image type");
        }
    }

    static MimeType getMediaType(byte[]image){
        if(image == null || image.length == 0)
            throw new InvalidImageException("empty or  null image");
        String mediaType = tika.detect(image);
        try{
            return MimeTypes.getDefaultMimeTypes().forName(mediaType);

        } catch (MimeTypeException e) {
            throw new InvalidImageException("invalid image type");
        }
    }
    static public ImageWrapper convertToWebp(ImageWrapper wrapper) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(wrapper.getBytes()));
        if(bufferedImage == null){
            throw new InvalidImageException("unsupported image format to convert webp");
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream((int) wrapper.getSize());
        ImageIO.write(bufferedImage,"webp",outputStream);

        return new ImageWrapper(wrapper.getOriginalName(), outputStream.toByteArray(),ImagesFormat.WEBP.getMediaType(),".webp");
    }
}
