package com.ecommerce.Product.services;

import com.ecommerce.Images.resposes.ProductImageResponseDto;
import com.ecommerce.Category.entity.Category;
import com.ecommerce.Images.entity.ProductImageId;
import com.ecommerce.Images.entity.ProductImages;
import com.ecommerce.Images.entity.Image;
import com.ecommerce.Category.repos.CategoryJpaRepo;
import com.ecommerce.Images.repos.ImagesJpaRepo;
import com.ecommerce.Exception.BadRequestException;
import com.ecommerce.Exception.NotFoundException;
import com.ecommerce.Images.repos.ProductImageRepo;
import com.ecommerce.Product.entity.ProductSortDirection;
import com.ecommerce.Product.dtos.ProductSearchView;
import com.ecommerce.Product.entity.Product;
import com.ecommerce.Product.repos.IProductSearchRepo;
import com.ecommerce.Product.repos.ProductJpaRepo;
import com.ecommerce.Stock.service.IStockService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
    @Cacheable(value = "products",key = "#product_id")
    public Product getProductById(Long product_id) {

        return productJpaRepo.findById(product_id).orElseThrow(
                ()-> new NotFoundException("product with id:" +product_id +" doesn't exists")
        );
    }

    public Product getReferenceById(Long id)
    {
        return productJpaRepo.getReferenceById(id);
    }
    @Caching(
            evict = {
                    @CacheEvict(value = "products",key = "#product.id"),
            }


    )
    public Product save(Product product)
    {
        return productJpaRepo.save(product);
    }
    @Override
    public boolean isProductExists(Long product_id) {
        return productJpaRepo.isExists(product_id);
    }

    @Override
    public void existsById(Long productId) {
        if(!isProductExists(productId))
            throw new NotFoundException("Product " + productId + " does not exist");
    }

    @Override
    public void existsByIds(Set<Long> productIds) {
        if(productIds == null || productIds.isEmpty())
            return;

        var exists = productJpaRepo.getExistingIds(productIds);
        if(exists.size() != productIds.size())
        {
            List<Long> notExistingIds = productIds.stream().filter(i -> !exists.contains(i)).toList();
            throw new NotFoundException("prodcut ids: " + notExistingIds + " does not exists");
        }
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "products",key = "#product_id"),
            }


    )
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
    @CacheEvict(value = "products",key = "#command.product_id")
    public void updateProduct(UpdateProductCommand command){


        Product product= productJpaRepo.findByIdForUpdate(command.product_id()).orElseThrow(
                () -> new NotFoundException("product with id:" + command.product_id()+" doesn't exists"));
        product.setTitle(command.title());
        product.setDescription(command.description());
        product.setPrice(command.price());
    }

    @Override
    public Map<Long,ProductSearchView> getProductSearchView(Collection<Long> ids) {

        return productJpaRepo.findAllByidsForProductSearchView(ids).stream().collect(Collectors.toMap(ProductSearchView::getId,p -> p));
    }

}
