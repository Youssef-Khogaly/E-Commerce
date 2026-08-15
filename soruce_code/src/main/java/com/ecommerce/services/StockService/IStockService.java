package com.ecommerce.services.StockService;

import com.ecommerce.entities.Products.ProductStock;

import java.util.Map;
import java.util.Set;

public interface IStockService {

    public Set<ProductStock> add(Map<Long,Integer> id_quantityMap);
    public Set<ProductStock> remove(Map<Long,Integer> id_quantityMap);
    public Set<ProductStock> reserve(Map<Long,Integer> id_quantityMap );
    public Set<ProductStock> release(Map<Long,Integer> id_quantityMap );
    public Set<ProductStock> commit(Map<Long,Integer> id_quantityMap );

    public Set<ProductStock> findAllByIdForUpdate(Set<Long> ids );
    public ProductStock findByIdForUpdate( Long id);

    public ProductStock findByIdReadOnly(Long id);
    public Set<ProductStock> findAllByIdReadOnly(Set<Long> ids );

    public ProductStock add(Long id, int quantity);
    public ProductStock remove(Long id, int quantity);
    public ProductStock reserve(Long id, int quantity);
    public ProductStock release(Long id, int quantity);
    public ProductStock commit(Long id, int quantity);


    public ProductStock create(Long productId);
    public ProductStock create(Long productId, int quantity);
}
