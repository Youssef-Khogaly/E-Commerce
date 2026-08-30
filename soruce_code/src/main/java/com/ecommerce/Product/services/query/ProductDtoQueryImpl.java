package com.ecommerce.Product.services.query;

import com.ecommerce.Category.entity.Category;
import com.ecommerce.Category.services.ProductCategoryCachingProxy;
import com.ecommerce.Category.services.ProductCategoryService;
import com.ecommerce.Images.dtos.ProductImageDto;
import com.ecommerce.Images.services.ProductImagesCachingService;
import com.ecommerce.Images.services.ProductImagesService;
import com.ecommerce.Product.dtos.ProductDTO;
import com.ecommerce.Product.entity.Product;
import com.ecommerce.util.advices.ReqCollapsing;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

@Service
@Primary
public class ProductDtoQueryImpl implements ProductDtoQueryService {
    private final ProductQueryService<Product> productQueryService;
    private final ProductCategoryService productCategoryService;
    private final ProductImagesService productImagesService;
    private final RedisTemplate<String,Object>redisTemplate;
    private final ObjectMapper objectMapper;;
    public ProductDtoQueryImpl(@Qualifier("productQueryImpl") ProductQueryService<Product> productQueryService
            , @Qualifier("productCategoryServiceImpl") ProductCategoryService productCategoryService
            , @Qualifier("productImageServiceImpl") ProductImagesService productImagesService
            , RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.productQueryService = productQueryService;
        this.productCategoryService = productCategoryService;
        this.productImagesService = productImagesService;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }


    private Product convertProductOrFetchFromDp(Object object,Long productId ,Map<String,Object> cacheAfterMap)
    {
        Product product = null;
        if(object == null)
        {
            product = productQueryService.findById(productId); // get from db
            cacheAfterMap.put(ProductQueryCaching.toRedisKey(productId),product);
        }else{
            product = objectMapper.convertValue(object,Product.class);
        }

        return  product;
    }
    private List<ProductImageDto> convertImagesOrFetchFromDp(Object object,Long productId ,Map<String,Object> cacheAfterMap)
    {
        List<ProductImageDto>productImageDtoList = null;
        if(object == null)
        {
            productImageDtoList = productImagesService.getProductImages(productId);
            cacheAfterMap.put(ProductImagesCachingService.toRedisKey(productId),productImageDtoList);
        }else{
            productImageDtoList = objectMapper.convertValue(object, new TypeReference<List<ProductImageDto>>() {});
        }

        return  productImageDtoList;
    }

    private List<Category> convertCategoriesOrFetchFromDp(Object object,Long productId ,Map<String,Object> cacheAfterMap)
    {
        List<Category>categoryList = null;
        if(object == null)
        {
            categoryList = productCategoryService.getProductCategories(productId).stream().toList();
            cacheAfterMap.put(ProductCategoryCachingProxy.productIdToRedisKey(productId),categoryList);
        }else{
            categoryList =  objectMapper.convertValue(object, new TypeReference<List<Category>>() {});
        }

        return  categoryList;
    }
    @Override
    @ReqCollapsing(keys = {"#productId"})
    public ProductDTO findById(Long productId) {
        Product product = null;
        List<ProductImageDto>productImageDtoList = null;
        List<Category>categoryList = null;

        // objects to cache after
        // objects to cache after
        Map<String,Object> productCacheAfter = new HashMap<>(1,1);
        Map<String,Object> categoriesCacheAfter = new HashMap<>(1,1);
        Map<String,Object> imagesCacheAfter = new HashMap<>(1,1);

        // keys to fetch
        List<String>redisKeys = new ArrayList<>(3);
        redisKeys.add(ProductQueryCaching.toRedisKey(productId));
        redisKeys.add(ProductImagesCachingService.toRedisKey(productId));
        redisKeys.add((ProductCategoryCachingProxy.productIdToRedisKey(productId)));

        List<Object> objectList = redisTemplate.opsForValue().multiGet(redisKeys);
        assert objectList != null;

        var categoriesFuture = CompletableFuture.supplyAsync(() -> convertCategoriesOrFetchFromDp(objectList.get(1),productId,categoriesCacheAfter));
        var imagesFuture = CompletableFuture.supplyAsync(() -> convertImagesOrFetchFromDp(objectList.get(2),productId,imagesCacheAfter));


        product = convertProductOrFetchFromDp(objectList.get(0),productId,productCacheAfter);
        try{
            categoryList = categoriesFuture.join();
            productImageDtoList = imagesFuture.join();
        }catch (CompletionException e)
        {
            if(e.getCause() instanceof RuntimeException re)
                throw re;

            throw new RuntimeException(e.getCause());
        }


        productCacheAfter.putAll(categoriesCacheAfter);
        productCacheAfter.putAll(imagesCacheAfter);
        if(!productCacheAfter.isEmpty())
        {
            redisTemplate.opsForValue().multiSet(productCacheAfter);
        }


        return ProductDTO.fromProduct(product,productImageDtoList,categoryList);
    }

    @Override
    @ReqCollapsing(keys = {"#ids"})
    public Map<Long, ProductDTO> findAllByIds(Set<Long> ids) {

        var imagesFuture = CompletableFuture.supplyAsync(() -> productImagesService.getproductsImages(ids));
        var categoriesFuture = CompletableFuture.supplyAsync(() -> productCategoryService.getProductsCategories(ids));

         Map<Long,Product> productMap = null;
         Map<Long,List<ProductImageDto>> imagesMap = null;
         Map<Long, Collection<Category>> categoriesMap = null;

        final Map<Long ,ProductDTO> ret = new HashMap<>(ids.size(),1);


        productMap = productQueryService.findAllByIds(ids);
        try{

            imagesMap = imagesFuture.join();
            categoriesMap = categoriesFuture.join();

        } catch (CompletionException e)
        {
            if(e.getCause() instanceof RuntimeException re)
                throw re;

            throw new RuntimeException(e.getCause());
        }


        ProductDTO productDTO = null;
        for(var entry : productMap.entrySet())
        {
            productDTO = ProductDTO.fromProduct(entry.getValue(),imagesMap.get(entry.getKey()) , categoriesMap.get(entry.getKey()));
            ret.put(entry.getKey(),productDTO);
        }

        return ret;
    }

    @Override
    @ReqCollapsing(keys = {"#productId","#includes"})
    public ProductDTO findById(final Long productId, final EnumSet<ProductDtoFields> includes) {
        Product product = null;
        List<ProductImageDto>productImageDtoList = null;
        List<Category>categoryList = null;
        final boolean fetchCategory = includes.contains(ProductDtoFields.CATEGORY);
        final boolean fetchImages =  includes.contains(ProductDtoFields.IMAGES);
        int productIdx = 0;
        int categoryIdx;
        int imagesIdx;
        if(fetchCategory && fetchImages)
            return findById(productId);

        // objects to cache after
        Map<String,Object> productCacheAfter = new HashMap<>(1,1);
        Map<String,Object> categoriesCacheAfter = new HashMap<>(1,1);
        Map<String,Object> imagesCacheAfter = new HashMap<>(1,1);
        // keys to fetch
        List<String>redisKeys = new ArrayList<>(3);
        redisKeys.add(ProductQueryCaching.toRedisKey(productId));
        if(fetchImages)
        {
            redisKeys.add(ProductImagesCachingService.toRedisKey(productId));
            categoryIdx = redisKeys.size()-1;
        } else {
            categoryIdx = 0;
        }

        if(fetchCategory)
        {
            redisKeys.add((ProductCategoryCachingProxy.productIdToRedisKey(productId)));
            imagesIdx = redisKeys.size()-1;
        } else {
            imagesIdx = 0;
        }


        List<Object> objectList = redisTemplate.opsForValue().multiGet(redisKeys);
        assert objectList != null;
        CompletableFuture<List<Category>> categoriesFuture = null;
        CompletableFuture<List<ProductImageDto>> imagesFuture = null;

        if(fetchCategory)
            categoriesFuture = CompletableFuture.supplyAsync(() -> convertCategoriesOrFetchFromDp(objectList.get(categoryIdx),productId,categoriesCacheAfter));

        if(fetchImages)
            imagesFuture = CompletableFuture.supplyAsync(() -> convertImagesOrFetchFromDp(objectList.get(imagesIdx),productId,imagesCacheAfter));


        product = convertProductOrFetchFromDp(objectList.get(0),productId,productCacheAfter);

        try{
            if(fetchCategory)
                categoryList = categoriesFuture.join();
            if(fetchImages)
                productImageDtoList = imagesFuture.join();
        }catch (CompletionException e)
        {
            if(e.getCause() instanceof RuntimeException re)
                throw re;

            throw new RuntimeException(e.getCause());
        }

        productCacheAfter.putAll(categoriesCacheAfter);
        productCacheAfter.putAll(imagesCacheAfter);
        if(!productCacheAfter.isEmpty())
        {
            redisTemplate.opsForValue().multiSet(productCacheAfter);
        }

        return ProductDTO.fromProduct(product,productImageDtoList,categoryList);
    }

    @Override
    @ReqCollapsing(keys = {"#ids","#includes"})
    public Map<Long, ProductDTO> findAllByIds(Set<Long> ids, EnumSet<ProductDtoFields> includes) {

        boolean fetchCategories = includes.contains(ProductDtoFields.CATEGORY);
        boolean fetchImages = includes.contains(ProductDtoFields.IMAGES);

        if(fetchCategories && fetchImages)
            return findAllByIds(ids);


        Map<Long,Product> productMap = null;
        Map<Long,List<ProductImageDto>> imagesMap = null;
        Map<Long, Collection<Category>> categoriesMap = null;
        CompletableFuture<Map<Long, Collection<Category>>> categoriesFuture = null;
        CompletableFuture<Map<Long,List<ProductImageDto>>> imagesFuture = null;

        if(fetchImages)
            imagesFuture = CompletableFuture.supplyAsync(() -> productImagesService.getproductsImages(ids));
        if(fetchCategories)
            categoriesFuture = CompletableFuture.supplyAsync(() -> productCategoryService.getProductsCategories(ids));

        productMap = productQueryService.findAllByIds(ids);

        final Map<Long ,ProductDTO> ret = new HashMap<>(ids.size(),1);



        try{

            imagesMap = imagesFuture.join();
            categoriesMap = categoriesFuture.join();

        } catch (CompletionException e)
        {
            if(e.getCause() instanceof RuntimeException re)
                throw re;

            throw new RuntimeException(e.getCause());
        }


        ProductDTO productDTO = null;
        List<ProductImageDto> productImageDtoList = null;
        Collection<Category> categorySet = null;
        for(var entry : productMap.entrySet())
        {
            if(fetchImages)
                productImageDtoList = imagesMap.get(entry.getKey());
            if(fetchCategories)
                categorySet = categoriesMap.get(entry.getKey());
            productDTO = ProductDTO.fromProduct(entry.getValue(),productImageDtoList , categorySet);
            ret.put(entry.getKey(),productDTO);
        }

        return ret;
    }
}
