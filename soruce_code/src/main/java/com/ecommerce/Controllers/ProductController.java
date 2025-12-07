package com.ecommerce.Controllers;


import com.ecommerce.DTO.ProductDTO;
import com.ecommerce.DTO.Requests.AddProductRequest;
import com.ecommerce.DTO.Requests.PutProductRequest;

import com.ecommerce.entities.Categories.Category;
import com.ecommerce.Exception.BadRequestException;
import com.ecommerce.services.interfaces.ProductService;
import com.ecommerce.services.ProductSortByOptions;
import com.ecommerce.services.ProductSortDirection;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/products")
@AllArgsConstructor
@Validated
public class ProductController {


    private ProductService productService;

    public static record AdminQueryRequest(ProductService.DeletedOptions deletedOptions){

    }


    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getProducts(
            @RequestParam(name = "page",defaultValue = "0") @PositiveOrZero int page
            ,@RequestParam(name = "size",defaultValue = "50") @Positive int pageSize,
            @RequestParam(required = false,name = "name") @Length(max = 64 ,message = "title text query can't have length more than 64") String title ,
            @RequestParam(required = false, name = "minPrice") @PositiveOrZero Long minPrice , @RequestParam(required = false, name = "maxPrice") @PositiveOrZero Long maxPrice,
            @RequestParam(required = false, name = "categoryId")  @Positive Integer categoies,
            @RequestParam(name = "sortBy" , defaultValue = "DATE") ProductSortByOptions sortBy,
            @RequestParam(name = "direction" , defaultValue = "DESC") ProductSortDirection direction
    )
    {
        if(maxPrice != null && minPrice != null && maxPrice < minPrice){

            throw new BadRequestException("max product price can't be less than min price in query product");
        }
        Optional<Long> minPriceOpt = Optional.ofNullable(minPrice);
        Optional<Long>maxPriceOpt = Optional.ofNullable(maxPrice);
        var query = new ProductService.QueryProduct(page,pageSize,Optional.ofNullable(title),minPriceOpt,maxPriceOpt,Optional.ofNullable(categoies),sortBy,direction);
        var result = productService.getProducts(query);
        return ResponseEntity.ok(result);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO>getProduct(@PathVariable @Positive long id){

        // handle not found exception later

        var p = productService.getProduct(id);
        return ResponseEntity.ok(p);
    }
    @PostMapping
    public ResponseEntity<?> addNewProduct(@Valid @RequestBody AddProductRequest req){
        ProductService.PostProductCommand command = new ProductService.PostProductCommand(
                req.title(), req.description(),req.priceInCents(),req.stock());
        long id = productService.addProduct(command).getId();
        return ResponseEntity.created(URI.create("api/products/"+id)).build();
    }
    @DeleteMapping("/{id}")
    public  ResponseEntity<?> deleteProduct(@PathVariable  @Positive long id){
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@RequestBody @Valid PutProductRequest putProductRequest, @PathVariable @Valid @NotNull @Positive Long id){

        productService.updateProduct(
                new ProductService.UpdateProductCommand(id,putProductRequest.title() , putProductRequest.description() ,putProductRequest.priceInCents(),putProductRequest.stock())
        );

        return ResponseEntity.ok().build();
    }

    //////////////////// product category
    @PutMapping("/{id}/categories")
    public ResponseEntity<?>putCategoryToProduct(@PathVariable @Valid @NotNull@Positive Long id ,@RequestBody @NotNull Set<@NotNull @Positive Integer> categoriesIds ){

        productService.putProductCategories(id,categoriesIds);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/{id}/categories")
    public ResponseEntity<Collection<Category>>getProductCategory(@PathVariable @Valid @NotNull@Positive Long id ){

        Collection<Category> categories = productService.getProductCategory(id);
        return ResponseEntity.ok().body(categories);
    }
    /// //////////////////
    /// / product images
    @GetMapping("/{id}/images")
    public ResponseEntity<?> getProductImages(@RequestParam(defaultValue = "false") Boolean mainOnly, @PathVariable @Positive long id){
         // to do later

        return null;
    }

    static public record PutProductImagesRequest(@NotNull Set<@NotNull@Positive Long>  image_ids)
    {
    }
    @PutMapping("/{id}/images")
    public ResponseEntity<?> putProductImage(@RequestBody @Valid PutProductImagesRequest req, @PathVariable @NotNull @Positive Long id){
        // to do later
        return  null;
    }
}
