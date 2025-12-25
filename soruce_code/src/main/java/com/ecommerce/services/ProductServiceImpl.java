package com.ecommerce.services;

import com.ecommerce.DTO.ProductDTO;
import com.ecommerce.DTO.ProductSearchView;
import com.ecommerce.Exception.ConflictException;
import com.ecommerce.entities.Categories.Category;
import com.ecommerce.entities.Products.Product;
import com.ecommerce.entities.Products.ProductStock;
import com.ecommerce.entities.images.Image;
import com.ecommerce.repository.Category.CategoryJpaRepo;
import com.ecommerce.repository.Product.IProductSearchRepo;
import com.ecommerce.repository.Product.ProductJpaRepo;
import com.ecommerce.Exception.BadRequestException;
import com.ecommerce.Exception.NotFoundException;
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
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private IProductSearchRepo productSearchRepo;
    private ProductJpaRepo productJpaRepo;
    private CategoryJpaRepo categoryJpaRepo;


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
    public Page<ProductSearchView> getProducts(QueryProduct queryProduct) {
        int pageNum = queryProduct.page();
        int pageSize = queryProduct.pageSize();
        Sort.Direction direction = (queryProduct.direction() == ProductSortDirection.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;

        String sortby = queryProduct.sortBy().toProductField();
        Sort sort = Sort.by(direction,sortby);

        Pageable page = PageRequest.of(pageNum,pageSize,sort);
        String searchQuery = normalizeSearchQuery(queryProduct.name());
        Integer catId = queryProduct.category();


        System.out.println(searchQuery);
        return productSearchRepo.searchForProducts(searchQuery, catId ,queryProduct.minPrice(),queryProduct.maxPrice(),page);
    }

    @Override
    public ProductDTO getProduct(Long product_id) {

        Product product = productJpaRepo.findById(product_id).orElseThrow(
                ()-> new NotFoundException("product with id:" +product_id +" doesn't exists")
        );

        return new ProductDTO(product_id,product.getTitle(),product.getDescription(),product.getPrice(),0,product.getStock().getAvailableStock(),product.getImages().stream().map(Image::getImage_url).toList());
    }


    @Override
    public boolean isProductExists(Long product_id) {
        return !productJpaRepo.isExists(product_id);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Category> getProductCategory(Long product_id) {
        if(isProductExists(product_id))
            throw new NotFoundException("product with id:" +product_id +" doesn't exists");

        return productJpaRepo.findCategoriesById(product_id);
    }

    @Override
    public void deleteProduct(Long product_id) {
        if(productJpaRepo.existsById(product_id))
            throw new NotFoundException("product with id:" +product_id +" doesn't exists");

        productJpaRepo.deleteById(product_id);

    }

    @Override
    @Transactional
    public Product addProduct(PostProductCommand command) throws BadRequestException {
        Product product = new Product();

        product.setTitle(command.title());
        product.setPrice(command.price());
        product.setDescription(command.description());
        var st = new ProductStock();
        st.setProduct(product);
        st.setStock(command.stock());
        product.setStock(st);
        product =  productJpaRepo.save(product);

        return product;
    }
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateProduct(UpdateProductCommand command){


        Product product= productJpaRepo.findByIdForUpdate(command.product_id()).orElseThrow(
                () -> new NotFoundException("product with id:" + command.product_id()+" doesn't exists"));
        if(product.getStock().getReservedStock() > command.stock())
            throw new ConflictException("Can't update product stock with stock less than reserved , productId:" + command.product_id() + "Stock:" +product.getStock());
        product.setTitle(command.title());
        product.setDescription(command.description());
        product.setPrice(command.price());
        product.getStock().setStock(command.stock());
    }

    @Override
    @Transactional
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
            categoriesIds.removeAll(existsIds);
            throw new NotFoundException("bad categories id:" + categoriesIds + "doesn't exist");
        }
        product.setCategories(categories);
    }

    @Override
    public Map<Long,ProductDTO> getProducts(Collection<Long> ids) {

        return productJpaRepo.findAllByIdReadOnly(ids).stream().collect(Collectors.toMap(Product::getId,this::toProductDTO));
    }
    private ProductDTO toProductDTO(Product product){
        //product.getImages().stream().map(Image::getImage_url).toList()
        return new ProductDTO(product.getId(),product.getTitle(),product.getDescription(),product.getPrice(),0,product.getStock().getAvailableStock(), null);

    }
}
