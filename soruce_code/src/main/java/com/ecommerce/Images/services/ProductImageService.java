package com.ecommerce.Images.services;

import com.ecommerce.Exception.BadRequestException;
import com.ecommerce.Images.dtos.ProductImageDto;
import com.ecommerce.Images.dtos.ProductImageDtoMapper;
import com.ecommerce.Images.entity.Image;
import com.ecommerce.Images.entity.ProductImages;
import com.ecommerce.Images.repos.ProductImageRepo;
import com.ecommerce.Product.entity.Product;
import com.ecommerce.Product.services.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductImageService implements ProductImagesService{

    private final ProductImageRepo productImageRepo;
    private final ProductService productService;
    private final IImageService imageService;
    private final ProductImageDtoMapper productImageDtoMapper;
    @Override
    @Transactional(readOnly = true)
    public List<ProductImageDto> getProductImages(Long productId) {
        productService.existsById(productId);

        return productImageRepo.findAllByProduct_Id(productId).stream().map(productImageDtoMapper::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, List<ProductImageDto>> getproductsImages(Set<Long> productIds) {
        if(productIds == null || productIds.isEmpty())
            return Map.of();

        productService.existsByIds(productIds);
        List<ProductImages> productImages = productImageRepo.findAllByProductIds(productIds);

        return productImages.stream()
                .collect(Collectors.groupingBy(ProductImages::getProductId
                        ,Collectors.mapping(productImageDtoMapper::from,Collectors.toList())));
    }

    @Override
    @Transactional
    public void putProductImages(Long productId, Set<Long> imagesIds, Long mainImageId) {
        productService.existsById(productId);
        if(imagesIds == null || imagesIds.isEmpty())
        {
            productImageRepo.deleteAllByProduct_Id(productId);
            return;
        }
        if(!imagesIds.contains(mainImageId))
            throw new BadRequestException("main image id does not exists in product id list");



        Set<Image> images = imageService.findAllByIds(imagesIds);

        final Product productRef = productService.getReferenceById(productId);
        productImageRepo.deleteAllByProduct_Id(productId);
        productImageRepo.flush();

        List<ProductImages> productImages = images.stream().map(i -> {
            var productImage = new ProductImages(productId,i.getId());
            productImage.setProduct(productRef);
            productImage.setImage(i);
            if(i.getId().equals(mainImageId))
                productImage.setMain(true);
            return productImage;
        }).toList();

        productImageRepo.saveAll(productImages);
    }

}
