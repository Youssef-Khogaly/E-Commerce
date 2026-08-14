package com.ecommerce.services;

import com.ecommerce.DTO.ProductDTO;
import com.ecommerce.DTO.ProductImageResponseDto;
import com.ecommerce.DTO.ProductSearchView;
import com.ecommerce.Exception.ConflictException;
import com.ecommerce.entities.Categories.Category;
import com.ecommerce.entities.Products.Product;
import com.ecommerce.entities.Products.ProductImageId;
import com.ecommerce.entities.Products.ProductImages;
import com.ecommerce.entities.Products.ProductStock;
import com.ecommerce.entities.images.Image;
import com.ecommerce.repository.Category.CategoryJpaRepo;
import com.ecommerce.repository.ImagesJpaRepo;
import com.ecommerce.repository.Product.IProductSearchRepo;
import com.ecommerce.repository.Product.ProductJpaRepo;
import com.ecommerce.Exception.BadRequestException;
import com.ecommerce.Exception.NotFoundException;
import com.ecommerce.repository.ProductImageRepo;
import com.ecommerce.services.StockService.IStockService;
import com.ecommerce.services.interfaces.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final IProductSearchRepo productSearchRepo;
    private final ProductJpaRepo productJpaRepo;
    private final CategoryJpaRepo categoryJpaRepo;
    private final ProductImageRepo productImageRepo;
    private final ImagesJpaRepo imageRepo;
    private final IStockService stockService;
    private String normalizeSearchQuery(String name){
        if(name == null || name.isBlank())
                return null;
        // build search query match
        // allow only  chars and numbers and space
        String normalizedTxt = name.trim().toLowerCase().replaceAll("[^a-z0-9\\s]" , "");
        if(normalizedTxt.isBlank())
            throw new BadRequestException("only English chars and number are allowed in search query");
        String[] words = normalizedTxt.split("\\s+");
        StringBuilder searchQuery = new StringBuilder(name.length());

        for(String str : words)
        {
            if(!str.isBlank()){
                searchQuery.append('+').append(str).append(' ');
            }
        }
        return searchQuery.toString();
    }
    @Override
    public Page<ProductSearchView> getProductSearchView(QueryProduct queryProduct) {
        int pageNum = queryProduct.page();
        int pageSize = queryProduct.pageSize();
        Sort.Direction direction = (queryProduct.direction() == ProductSortDirection.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;

        String sortby = queryProduct.sortBy().toProductField();
        Sort sort = Sort.by(direction,sortby);

        Pageable page = PageRequest.of(pageNum,pageSize,sort);
        String searchQuery = normalizeSearchQuery(queryProduct.name());
        Integer catId = queryProduct.category();
        return productSearchRepo.searchForProducts(searchQuery, catId ,queryProduct.minPrice(),queryProduct.maxPrice(),page);
    }

    @Override
    public Product getProductById(Long product_id) {

        return productJpaRepo.findById(product_id).orElseThrow(
                ()-> new NotFoundException("product with id:" +product_id +" doesn't exists")
        );
    }

    public Product getReferenceById(Long id)
    {
        return productJpaRepo.getReferenceById(id);
    }

    public Product save(Product product)
    {
        return productJpaRepo.save(product);
    }
    @Override
    public boolean isProductExists(Long product_id) {
        return productJpaRepo.isExists(product_id);
    }

    @Override
    public Collection<Category> getProductCategory(Long product_id) {
        if(isProductExists(product_id))
            throw new NotFoundException("product with id:" +product_id +" doesn't exists");

        return productJpaRepo.findCategoriesById(product_id);
    }

    @Override
    public void deleteProduct(Long product_id) {
        if(!productJpaRepo.existsById(product_id))
            throw new NotFoundException("product with id:" +product_id +" doesn't exists");

        productJpaRepo.deleteById(product_id);

    }

    @Override
    @Transactional
    public Product addProduct(PostProductCommand command) {
        Product product = new Product();

        product.setTitle(command.title());
        product.setPrice(command.price());
        product.setDescription(command.description());
        product =  productJpaRepo.save(product);

        stockService.create(product.getId(),command.stock());

        return product;
    }
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateProduct(UpdateProductCommand command){


        Product product= productJpaRepo.findByIdForUpdate(command.product_id()).orElseThrow(
                () -> new NotFoundException("product with id:" + command.product_id()+" doesn't exists"));
        product.setTitle(command.title());
        product.setDescription(command.description());
        product.setPrice(command.price());
    }

    @Override
    public void putProductCategories(Long product_id,Set<Integer> categoriesIds) {
        if(isProductExists(product_id)){
            throw new NotFoundException("product with id:" + product_id +"doesn't exists or soft deleted");
        }
            Product product = productJpaRepo.getReferenceById(product_id);

        if(categoriesIds.isEmpty()){
            product.setCategories(Collections.emptySet());
            return;
        }
        Set<Category> categories = categoryJpaRepo.findAllById(categoriesIds).stream().collect(Collectors.toUnmodifiableSet());
        if(categories.size() != categoriesIds.size()){
            Set<Integer>existsIds = categories.stream().map(Category::getCate_id).collect(Collectors.toSet());

            throw new NotFoundException("bad categories id:" + categoriesIds.stream().filter(existsIds::contains).toList() + "doesn't exist");
        }
        product.setCategories(categories);
    }

    @Override
    public Map<Long,ProductSearchView> getProductSearchView(Collection<Long> ids) {

        return productJpaRepo.findAllByidsForProductSearchView(ids).stream().collect(Collectors.toMap(ProductSearchView::getId,p -> p));
    }

    @Override
    @Transactional
    public void putProductImages(Long productId, Set<Long> imageIds) {


        if(!productJpaRepo.isExists(productId))
            throw new NotFoundException("Product id:" +productId +" doesn't exist");
        if(imageIds.isEmpty()){
            productImageRepo.deleteAllByProduct_Id(productId);
            return;
        }else {
            List<Image>images = imageRepo.findAllById(imageIds.stream().toList());
            if(images.size() != imageIds.size()){
                imageIds.removeAll(images.stream().map(Image::getId).collect(Collectors.toSet()));
                throw new BadRequestException("Images ids doesn't exists:" + imageIds);
            }

            List<ProductImages> productImagesList = new ArrayList<>();
            Product productref = productJpaRepo.getReferenceById(productId);
            productImageRepo.deleteAllByProduct_Id(productId);
            images.forEach((image)->{
                ProductImages productImages1 = new ProductImages();
                productImages1.setProductImageId(new ProductImageId(productref.getId(),image.getId()));
                productImages1.setImage(image);
                productImages1.setProduct(productref);
                productImagesList.add(productImages1);
            });
            productImageRepo.saveAll(productImagesList);
        }
    }

    @Override
    public List<ProductImageResponseDto> getProductImages(Long productId) {
        return productImageRepo.findAllByProduct_Id(productId).stream()
                .map(img -> new ProductImageResponseDto(img.getProductImageId().getImage_id(),img.getImage().getImageUrl(), img.isMain()))
                .toList();
    }

    @Override
    @Transactional
    public void setProductMainImage(Long productId, Long mainImageId) {
        if(!imageRepo.existsById(mainImageId)){
            throw new BadRequestException("image with id:" + mainImageId + " doesn't exists");
        }
        Product product = productJpaRepo.findByIdWithImagesOnly(productId).orElseThrow(() -> new NotFoundException("Product id: "+productId+" doesn't exists"));
        AtomicBoolean exists = new AtomicBoolean(false);
        product.getImagesList().forEach(
                (pi) -> {
                    if(pi.getImage().getId().equals(mainImageId)){
                        exists.set(true);
                        pi.setMain(true);
                    }else if (pi.isMain()){
                        pi.setMain(false);
                    }
                }
        );
        if(!exists.get()){
            throw new BadRequestException("Image with id:" + mainImageId +" is not attached to product attach it first");
        }

    }

}
