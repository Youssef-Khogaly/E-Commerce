package com.ecommerce.Controllers;

import com.ecommerce.Validation.ImageValidator;
import com.ecommerce.services.ImageService;
import com.ecommerce.services.interfaces.IImageService;
import com.ecommerce.util.ImageWrapper;
import com.ecommerce.util.ImagesFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
@AllArgsConstructor
@Validated
public class ImagesController {

    private final IImageService imageService;
    private final ImageValidator imageValidator;


    @GetMapping("/{id}")
    public ResponseEntity<?> getImage(@PathVariable @NotNull @Positive Long id){
        String url =  imageService.getImage(id);

        return ResponseEntity.ok(url);
    }
    @PostMapping
    public ResponseEntity<Map<Long,String>>addImages(@RequestBody List<MultipartFile> images){
        List<ImageWrapper> imageWrappersList = images.stream().map(ImageWrapper::fromMultiPart).toList();
        imageWrappersList.forEach(image -> imageValidator.isValid(image, ImagesFormat.JPEG,ImagesFormat.JPG,ImagesFormat.PNG , ImagesFormat.WEBP));
        Map<Long,String> id_url_map = imageService.saveImages(imageWrappersList);
        return ResponseEntity.status(HttpStatus.CREATED).body(id_url_map);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void>deleteImage(@PathVariable @NotNull @Positive Long id){
        imageService.deleteImage(id);
        return ResponseEntity.ok().build();
    }
}
