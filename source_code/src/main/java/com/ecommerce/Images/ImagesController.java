package com.ecommerce.Images;

import com.ecommerce.Images.dtos.ImageDTO;
import com.ecommerce.Images.services.IImageService;
import com.ecommerce.util.ErrorResponse;
import com.ecommerce.Validation.ImageValidator;
import com.ecommerce.docs.CommonErrorDocs;
import com.ecommerce.docs.RequireAuthDocs;
import com.ecommerce.util.ImageWrapper;
import com.ecommerce.util.ImagesFormat;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
@AllArgsConstructor
@Validated
public class ImagesController {

    private final IImageService imageService;
    private final ImageValidator imageValidator;


    @Operation(description = " get image public url")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200",content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE , schema = @Schema(implementation = String.class))),
            }
    )
    @CommonErrorDocs
    @GetMapping("/{id}")
    public ResponseEntity<ImageDTO> getImage(@PathVariable @NotNull @Positive Long id){
        String url =  imageService.getImage(id);

        return ResponseEntity.ok(new ImageDTO(id,url));
    }
    @Operation(summary = "upload multiple images sync" , description = "Required role: Admin  - Max image size: 5MB - Max request size 50 MB - Supported formats: JPG,PNG,WEBP " )
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "201",useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "500",description = "internal error",content = @Content(schema = @Schema(implementation = ErrorResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "400",description = "constrain violation",content = @Content(schema = @Schema(implementation = ErrorResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE)),
            }
    )
    @RequireAuthDocs
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<Long,String>>addImages(@RequestPart("images") List<MultipartFile> images){
        List<ImageWrapper> imageWrappersList = images.stream().map(ImageWrapper::fromMultiPart).toList();
        imageWrappersList.forEach(image -> imageValidator.isValid(image, ImagesFormat.JPEG,ImagesFormat.JPG,ImagesFormat.PNG , ImagesFormat.WEBP));
        Map<Long,String> id_url_map = imageService.saveImages(imageWrappersList);
        return ResponseEntity.status(HttpStatus.CREATED).body(id_url_map);
    }

    @Operation(summary = "delete image" , description = "Required role: Admin")
    @CommonErrorDocs
    @RequireAuthDocs
    @DeleteMapping("/{id}")
    public ResponseEntity<Void>deleteImage(@PathVariable @NotNull @Positive Long id){
        imageService.deleteImage(id);
        return ResponseEntity.ok().build();
    }
}
