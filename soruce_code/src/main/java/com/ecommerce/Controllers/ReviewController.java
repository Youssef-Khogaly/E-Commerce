package com.ecommerce.Controllers;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {


    /*

        to do later
     */



    @GetMapping
    public ResponseEntity<?>getReviewsForProduct(
            @Positive @RequestParam(name = "product") Long product_id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "rating")String sortBy,
            @RequestParam(defaultValue = "desc" )String direction) {
        return null;
    }
    @GetMapping("/summaries")
    public ResponseEntity<?>getReviewSummary(
            @Positive @RequestParam(name = "product" , required = false) Long product_id){
        return null;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static record ReviewPostReq(@Range(max = 5) int rating , @Nullable String comment){

    }
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static record ReviewPatchReq(@Nullable @Range(max = 5) Integer rating , @Nullable String comment){

    }
    @PostMapping
    public ResponseEntity<?>addCustomerReview(@Positive @RequestParam(name = "product") Long product_id
                                                , @Valid @RequestBody ReviewPostReq review){

        return null;
    }
    @DeleteMapping
    public ResponseEntity<?>deleteReview(@Positive @RequestParam(name = "product") Long product_id){
        return null;
    }
    @PatchMapping public ResponseEntity<?>editReview
            (@Positive @RequestParam(name = "product") Long product_id
            , @Valid @RequestBody ReviewPatchReq review){

        return null;
    }


}
