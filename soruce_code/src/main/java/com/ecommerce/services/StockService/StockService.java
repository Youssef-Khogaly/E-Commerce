package com.ecommerce.services.StockService;

import com.ecommerce.Exception.NotFoundException;
import com.ecommerce.entities.Products.ProductStock;
import com.ecommerce.repository.StockJpaRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StockService implements IStockService {

    private StockJpaRepo stockJpaRepo;
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void add(Map<Long,Integer> id_quantityMap)
    {
        var entities = findAllByIdForUpdate(id_quantityMap.keySet());

        for(ProductStock stock : entities)
        {
            stock.add(id_quantityMap.get(stock.getProduct_id()));
        }
    }
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void remove(Map<Long,Integer> id_quantityMap)
    {
        var entities = findAllByIdForUpdate(id_quantityMap.keySet());

        int toRemove;
        for(ProductStock stock : entities)
        {
            toRemove =  id_quantityMap.get(stock.getProduct_id());
            stock.remove(toRemove);
        }
    }
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Set<ProductStock> findAllByIdForUpdate(Set<Long> ids)
    {
        if(ids == null)
            throw new NullPointerException("null pointer passed to update stock");
        if(ids.isEmpty())
            return Collections.emptySet();

        final Set<ProductStock> data = stockJpaRepo.findAllByIdForUpdate(ids);
        if(data.size() != ids.size())
        {
            final var existingIds = data.stream().map(ProductStock::getProduct_id).collect(Collectors.toUnmodifiableSet());
            throw new NotFoundException("can't update stock for non existing product ids: " + ids.stream().filter(existingIds::contains).toList());
        }

        return data;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ProductStock findByIdForUpdate(Long id) {
        return stockJpaRepo.findByIdForUpdate(id).orElseThrow(() -> new NotFoundException("Stock not found, id:" + id));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED,readOnly = true)
    public ProductStock findByIdReadOnly(Long id) {
        return stockJpaRepo.findById(id).orElseThrow(() -> new NotFoundException("Stock not found, id:" + id));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void add(Long id, int quantity) {

        var entity = findByIdForUpdate(id);
        entity.add(quantity);

    }
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public void remove(Long id, int quantity) {
        var entity = findByIdForUpdate(id);
        entity.remove(quantity);
    }
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public void reserve(Long id, int quantity) {
        var entity = findByIdForUpdate(id);
        entity.reserve(quantity);
    }
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public void release(Long id, int quantity) {
        var entity = findByIdForUpdate(id);
        entity.reserve(quantity);
    }
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public void commit(Long id, int quantity) {
        var entity = findByIdForUpdate(id);
        entity.release(quantity);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void reserve(Map<Long,Integer> id_quantityMap ){

        var entities = findAllByIdForUpdate(id_quantityMap.keySet());
        entities.forEach(
                stock ->{
                    long productId = stock.getProduct().getId();
                    int quantityNeeded = id_quantityMap.get(productId);
                    stock.reserve(quantityNeeded);
                }
        );
    }
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void release(Map<Long,Integer> id_quantityMap ){

        var entities = findAllByIdForUpdate(id_quantityMap.keySet());

        entities.forEach(
                stock ->{
                    long productId = stock.getProduct().getId();
                    int quantityNeeded = id_quantityMap.get(productId);
                    stock.release(quantityNeeded);
                }
        );
    }
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void commit(Map<Long,Integer> id_quantityMap ){
        var entities = findAllByIdForUpdate(id_quantityMap.keySet());


        entities.forEach(
                stock ->{
                    long productId = stock.getProduct().getId();
                    int quantityNeeded = id_quantityMap.get(productId);
                    stock.commit(quantityNeeded);
                }
        );
    }
}
