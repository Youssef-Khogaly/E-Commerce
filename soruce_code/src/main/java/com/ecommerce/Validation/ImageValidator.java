package com.ecommerce.Validation;

import com.ecommerce.Exception.InvalidImageException;
import com.ecommerce.util.ImageWrapper;
import com.ecommerce.util.ImagesFormat;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class ImageValidator implements ConstraintValidator<ValidImage, ImageWrapper> {
    private ImagesFormat[]allowed;
    @Override
    public void initialize(ValidImage constraintAnnotation) {
        allowed = constraintAnnotation.allowedFormat();
    }

    @Override
    public boolean isValid(ImageWrapper value, ConstraintValidatorContext context) {
        if(value == null || value.getSize() == 0 )
            throw new InvalidImageException("empty or null image file");

        for(ImagesFormat format : allowed){
            if(!value.getContentType().equalsIgnoreCase(format.getMediaType())){
                throw new InvalidImageException("Invalid Image Format, name:"+ value.getOriginalName()+ ", supported formats:"+ Arrays.toString(allowed));
            }
        }
        return  true;
    }


    public boolean isValid(ImageWrapper value, ImagesFormat... allowedFormats) {
        if(value == null || value.getSize() == 0 )
            throw new InvalidImageException("empty or null image file");
        if(allowedFormats == null || allowedFormats.length == 0)
            throw new IllegalArgumentException("empty allowed format for images");
        for(ImagesFormat format : allowedFormats){
            if(value.getContentType().equalsIgnoreCase(format.getMediaType())){
                return true;
            }
        }
        throw new InvalidImageException("Invalid Image Format, name:"+ value.getOriginalName()+ ", supported formats:"+ Arrays.toString(allowed));
    }
}
