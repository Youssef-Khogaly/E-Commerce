package com.ecommerce.Stock.service;

import com.ecommerce.Exception.BadRequestException;
import com.ecommerce.Exception.NotFoundException;
import com.ecommerce.Stock.entity.ProductStock;
import com.ecommerce.Stock.repos.StockJpaRepo;
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
    public Set<ProductStock> add(Map<Long,Integer> id_quantityMap)
    {
        var entities = findAllByIdForUpdate(id_quantityMap.keySet());

        for(ProductStock stock : entities)
        {
            stock.add(id_quantityMap.get(stock.getId()));
        }
        return entities;
    }
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Set<ProductStock> remove(Map<Long,Integer> id_quantityMap)
    {
        var entities = findAllByIdForUpdate(id_quantityMap.keySet());

        int toRemove;
        for(ProductStock stock : entities)
        {
            toRemove =  id_quantityMap.get(stock.getId());
            stock.remove(toRemove);
        }
        return entities;
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
            final var existingIds = data.stream().map(ProductStock::getId).collect(Collectors.toUnmodifiableSet());
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
    @Transactional(isolation = Isolation.READ_COMMITTED,readOnly = true)
    public Set<ProductStock> findAllByIdReadOnly(Set<Long> ids) {
        if(ids == null)
            throw new NullPointerException("null pointer passed to update stock");
        if(ids.isEmpty())
            return Collections.emptySet();

        final Set<ProductStock> data = stockJpaRepo.findAllByIdReadOnly(ids);
        if(data.size() != ids.size())
        {
            final var existingIds = data.stream().map(ProductStock::getId).collect(Collectors.toUnmodifiableSet());
            throw new NotFoundException("stock ids does not exists: " + ids.stream().filter(existingIds::contains).toList());
        }

        return data;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ProductStock add(Long id, int quantity) {

        var entity = findByIdForUpdate(id);
        entity.add(quantity);
        return entity;

    }
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public ProductStock remove(Long id, int quantity) {
        var entity = findByIdForUpdate(id);
        entity.remove(quantity);
        return entity;
    }
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public ProductStock reserve(Long id, int quantity) {
        var entity = findByIdForUpdate(id);
        entity.reserve(quantity);
        return entity;
    }
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public ProductStock release(Long id, int quantity) {
        var entity = findByIdForUpdate(id);
        entity.reserve(quantity);
        return entity;
    }
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public ProductStock commit(Long id, int quantity) {
        var entity = findByIdForUpdate(id);
        entity.release(quantity);
        return entity;
    }

    @Override
    public ProductStock create(Long productId) {
        if(stockJpaRepo.existsById(productId))
            throw new BadRequestException("Stock already exists, id:" + productId);

        var st = new ProductStock(productId,0);


        return stockJpaRepo.save(st);
    }
    @Override
    public ProductStock create(Long productId, int quantity)
    {
        if(quantity < 0)
            throw new IllegalArgumentException("Cannot create new stock with negative quantity");
        if(stockJpaRepo.existsById(productId))
            throw new BadRequestException("Stock already exists, id:" + productId);

        var st = new ProductStock(productId,quantity);
        return stockJpaRepo.save(st);
    }
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Set<ProductStock> reserve(Map<Long,Integer> id_quantityMap ){

        var entities = findAllByIdForUpdate(id_quantityMap.keySet());
        entities.forEach(
                stock ->{
                    long productId = stock.getId();
                    int quantityNeeded = id_quantityMap.get(productId);
                    stock.reserve(quantityNeeded);
                }
        );

        return entities;
    }
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Set<ProductStock> release(Map<Long,Integer> id_quantityMap ){

        var entities = findAllByIdForUpdate(id_quantityMap.keySet());

        entities.forEach(
                stock ->{
                    long productId = stock.getId();
                    int quantityNeeded = id_quantityMap.get(productId);
                    stock.release(quantityNeeded);
                }
        );
        return entities;
    }
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Set<ProductStock> commit(Map<Long,Integer> id_quantityMap ){
        var entities = findAllByIdForUpdate(id_quantityMap.keySet());


        entities.forEach(
                stock ->{
                    long productId = stock.getId();
                    int quantityNeeded = id_quantityMap.get(productId);
                    stock.commit(quantityNeeded);
                }
        );
        return entities;
    }
}
