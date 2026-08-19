package com.ecommerce.Product;


import com.ecommerce.Product.dtos.ProductDTO;
import com.ecommerce.Product.dtos.ProductSearchView;
import com.ecommerce.Product.entity.ProductSortByOptions;
import com.ecommerce.Product.entity.ProductSortDirection;
import com.ecommerce.Product.requests.AddProductRequest;
import com.ecommerce.Product.requests.PutProductRequest;
import com.ecommerce.Product.services.ProductService;
import com.ecommerce.util.ErrorResponse;
import com.ecommerce.Images.resposes.ProductImageResponseDto;

import com.ecommerce.docs.RequireAuthDocs;
import com.ecommerce.docs.CommonErrorDocs;
import com.ecommerce.Category.entity.Category;
import com.ecommerce.Exception.BadRequestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/products")
@AllArgsConstructor
@Validated
public class ProductController {
    private ProductService productService;



    @Operation(summary = "product search and filtration")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200",content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,schema = @Schema(implementation = ProductSearchView.class)) ),
                    @ApiResponse(responseCode = "400",description = "constrain violation",content = @Content(schema = @Schema(implementation = ErrorResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "500",description = "internal error",content = @Content(schema = @Schema(implementation = ErrorResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE))
            }

    )
    @GetMapping
    public ResponseEntity<Page<ProductSearchView>> getProducts(
            @RequestParam(name = "page",defaultValue = "0") @PositiveOrZero int page
            ,@RequestParam(name = "size",defaultValue = "50") @Positive int pageSize,
            @RequestParam(required = false,name = "s") @Length(max = 64 ,message = "title text query can't have length more than 64") String title ,
            @RequestParam( name = "minPrice" , defaultValue = "0")  @PositiveOrZero Long minPrice ,
            @RequestParam(name = "maxPrice" , defaultValue = "1000000000") @Positive Long maxPrice,
            @RequestParam(required = false, name = "categoryId")  @Positive Integer category,
            @RequestParam(name = "sortBy" , defaultValue = "DATE") ProductSortByOptions sortBy,
            @RequestParam(name = "direction" , defaultValue = "DESC") ProductSortDirection direction
    )
    {
        if(maxPrice != null && minPrice != null && maxPrice < minPrice){

            throw new BadRequestException("max product price can't be less than min price in query product");
        }
        var query = new ProductService.QueryProduct(page,pageSize,title,minPrice,maxPrice,category,sortBy,direction);
        var result = productService.getProductSearchView(query);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "get product")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,schema = @Schema(implementation = ProductDTO.class)) ),
            }

    )
    @CommonErrorDocs
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO>getProduct(@PathVariable @Positive long id){


        var p = productService.getProductById(id);
        return ResponseEntity.ok(ProductDTO.fromProduct(p));
    }

    @Operation(summary = "create new product" , description = "Required Role: Admin")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "201",headers = {@Header(name = "Location",description = "api/products/1",example = "api/products/1")}),
            }
    )
    @RequireAuthDocs
    @CommonErrorDocs
    @PostMapping
    public ResponseEntity<Void> addNewProduct(@Valid @RequestBody AddProductRequest req){
        ProductService.PostProductCommand command = new ProductService.PostProductCommand(
                req.title(), req.description(),req.priceInCents(),req.stock());
        long id = productService.addProduct(command).getId();
        return ResponseEntity.created(URI.create("api/products/"+id)).build();
    }

    @Operation(summary = "delete product" , description = "Required Role: Admin")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200"),
            }
    )
    @RequireAuthDocs
    @CommonErrorDocs
    @DeleteMapping("/{id}")
    public  ResponseEntity<Void> deleteProduct(@PathVariable  @Positive long id){
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "update product", description = "Required Role: Admin")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200"),
            }
    )
    @RequireAuthDocs
    @CommonErrorDocs
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProduct(@RequestBody @Valid PutProductRequest putProductRequest, @PathVariable @Valid @NotNull @Positive Long id){

        productService.updateProduct(
                new ProductService.UpdateProductCommand(id,putProductRequest.title() , putProductRequest.description() ,putProductRequest.priceInCents())
        );

        return ResponseEntity.ok().build();
    }

    //////////////////// product category
    @Operation(summary = "attach categories to product" , description = "Required Role: Admin")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200"),
            }

    )
    @RequireAuthDocs
    @CommonErrorDocs
    @PutMapping("/{id}/categories")
    public ResponseEntity<Void>putCategoryToProduct(@PathVariable @Valid @NotNull@Positive Long id ,@RequestBody @NotNull Set<@NotNull @Positive Integer> categoriesIds ){

        productService.putProductCategories(id,categoriesIds);
        return ResponseEntity.ok().build();
    }
    @Operation(summary = "get product categories")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200",useReturnTypeSchema = true),
            }
    )
    @CommonErrorDocs
    @GetMapping("/{id}/categories")
    public ResponseEntity<Collection<Category>>getProductCategory(@PathVariable @Valid @NotNull@Positive Long id ){

        Collection<Category> categories = productService.getProductCategory(id);
        return ResponseEntity.ok().body(categories);
    }
    /// //////////////////
    /// / product images

    @Operation(summary = "get product images")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200",useReturnTypeSchema = true ),
            }
    )
    @CommonErrorDocs
    @GetMapping("/{id}/images")
    public ResponseEntity<List<ProductImageResponseDto>> getProductImages(@PathVariable  @NotNull  @Positive Long id){

        List<ProductImageResponseDto> productImageResponseDtoList = productService.getProductImages(id);
        return ResponseEntity.ok(productImageResponseDtoList);
    }


    @Operation(summary = "put images to product, it replace product images" , description = "Required Role: Admin")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200")
            }
    )
    @CommonErrorDocs
    @RequireAuthDocs
    @PutMapping("/{id}/images/")
    public ResponseEntity<Void> addProductImages(@PathVariable @NotNull @Positive Long id , @RequestParam Set<@Positive @NotNull Long> imagesIds){
        productService.putProductImages(id,imagesIds);
        return  ResponseEntity.ok().build();
    }

    @Operation(summary = "set product main image" , description = "Required Role: Admin")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200")
            }
    )
    @CommonErrorDocs
    @RequireAuthDocs
    @PutMapping(value = "/{id}/images/" ,params = "mainId")
    public ResponseEntity<Void> setProductMainImage(@PathVariable @NotNull @Positive Long id , @RequestParam @NotNull @Positive Long mainId){
        productService.setProductMainImage(id,mainId);
        return  ResponseEntity.ok().build();
    }


}
