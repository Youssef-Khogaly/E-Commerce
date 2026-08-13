package com.ecommerce.services.StockService;

import com.ecommerce.entities.Products.ProductStock;

import java.util.Map;
import java.util.Set;

public interface IStockService {

    public void add(Map<Long,Integer> id_quantityMap);
    public void remove(Map<Long,Integer> id_quantityMap);
    public void reserve(Map<Long,Integer> id_quantityMap );
    public void release(Map<Long,Integer> id_quantityMap );
    public void commit(Map<Long,Integer> id_quantityMap );

    public Set<ProductStock> findAllByIdForUpdate(Set<Long> ids );
    public ProductStock findByIdForUpdate( Long id);

    public ProductStock findByIdReadOnly(Long id);

    public void add(Long id, int quantity);
    public void remove(Long id, int quantity);
    public void reserve(Long id, int quantity);
    public void release(Long id, int quantity);
    public void commit(Long id, int quantity);

}
