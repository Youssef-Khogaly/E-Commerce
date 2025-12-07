package com.ecommerce.Controllers;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/images")
public class ImagesController {

    /*

       // to do later
     */

    @GetMapping
    public ResponseEntity<?> getImages(){
        return null;
    }
    @PostMapping
    public ResponseEntity<?>addImages(@RequestBody List<String> imageUrl){
        return null;
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?>deleteImage(@PathVariable @Positive long id){
        return null;
    }
}
