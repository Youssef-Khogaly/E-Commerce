package com.ecommerce.Controllers;

import com.ecommerce.DTO.ErrorResponse;
import com.ecommerce.docs.CommonErrorDocs;
import com.ecommerce.docs.RequireAuthDocs;
import com.ecommerce.entities.Categories.Category;
import com.ecommerce.services.interfaces.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Validated
@AllArgsConstructor
public class CategoryController {
    private CategoryService categoryService;


    @Operation(description = "Get all categories")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200",useReturnTypeSchema = true),
                    @ApiResponse(responseCode = "500",description = "internal error",content = @Content(schema = @Schema(implementation = ErrorResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE)),

            }
    )
    @GetMapping
    public ResponseEntity<Collection<Category>> getCategory(){

        var ret = categoryService.getAllCategories();


        return ResponseEntity.status(HttpStatus.OK).body(ret);
    }
    @Operation(summary = "add new category" , description = "Required role: Admin")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200",content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE , schema = @Schema(implementation = Category.class))),
                    @ApiResponse(responseCode = "400",description = "constrain violation",content = @Content(schema = @Schema(implementation = ErrorResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "500",description = "internal error",content = @Content(schema = @Schema(implementation = ErrorResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE)),

            }
    )
    @RequireAuthDocs
    @PostMapping
        public ResponseEntity<Category> addCategory(@Valid @RequestParam  @NotNull @NotBlank String name){
        Category ret = categoryService.addCategory(name);
        return ResponseEntity.created(URI.create("/api/categories/" + ret.getCate_id())).build();
    }

    @Operation(summary = "add new category" , description = "Required role: Admin")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "400",description = "constrain violation",content = @Content(schema = @Schema(implementation = ErrorResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "500",description = "internal error",content = @Content(schema = @Schema(implementation = ErrorResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE)),

            }
    )
    @RequireAuthDocs
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable @Positive Integer id){
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();

    }
}
