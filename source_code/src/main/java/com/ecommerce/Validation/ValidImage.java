package com.ecommerce.Validation;

import com.ecommerce.util.ImagesFormat;
import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD,ElementType.PARAMETER,ElementType.CONSTRUCTOR,ElementType.TYPE_PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ImageValidator.class)
public @interface ValidImage {

    /*max size in bytes*/
    ImagesFormat[]allowedFormat() default {ImagesFormat.JPEG,ImagesFormat.JPG,ImagesFormat.PNG,ImagesFormat.WEBP};


}
