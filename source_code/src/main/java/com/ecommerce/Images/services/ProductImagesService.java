package com.ecommerce.Images.services;

import com.ecommerce.Images.dtos.ProductImageDto;
import com.ecommerce.Images.entity.ProductImages;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ProductImagesService {

    public List<ProductImageDto> getProductImages(Long productId);
    public Map<Long,List<ProductImageDto>>getproductsImages(Set<Long> productIds);

    public void putProductImages(Long productId,Set<Long>imagesIds , Long mainImageId);
}
