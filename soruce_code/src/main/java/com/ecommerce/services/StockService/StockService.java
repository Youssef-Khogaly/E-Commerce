package com.ecommerce.services.StockService;

import com.ecommerce.entities.Products.ProductStock;
import com.ecommerce.repository.StockJpaRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class StockService {

    private StockJpaRepo stockJpaRepo;

    public enum StockOperation {
        RESERVE,        // Temporarily reserve stock
        RELEASE,        // Release reserved stock (cancel)
        COMMIT          // Decrement total stock after payment
    }
    @Transactional(isolation = Isolation.READ_COMMITTED , propagation = Propagation.REQUIRES_NEW)
    // ids should be valid
    public void updatestock(Map<Long,Integer> id_quantityMap , StockOperation operationEnum) {
        if(operationEnum == null || id_quantityMap == null)
                throw new NullPointerException("null pointer passed to update stock");
        List<Long>ids = id_quantityMap.keySet().stream().toList();
        List<ProductStock> data = stockJpaRepo.findAllByIdForUpdate(ids);
        if(data.size() != id_quantityMap.size())
            throw new IllegalArgumentException("can't lock stock for non existing product id");

        if(operationEnum  == StockOperation.RESERVE){
            reserveStock(data,id_quantityMap);
        }
        else if (operationEnum  == StockOperation.RELEASE){
            unReserveStock(data,id_quantityMap);
        }
        else if(operationEnum  == StockOperation.COMMIT)
            totalStock(data,id_quantityMap);

        return;
    }
    private void reserveStock(List<ProductStock> entities ,Map<Long,Integer> id_quantityMap ){
        entities.forEach(
                stock ->{
                    long productId = stock.getProduct().getId();
                    int quantityNeeded = id_quantityMap.get(productId);
                    if(stock.getAvailableStock() >= quantityNeeded){
                        stock.setReservedStock(stock.getReservedStock()+quantityNeeded);
                    }else
                        throw new OutOfStock("not enough stock for product:"+stock.getProduct().getId() + "available stock:" + stock.getAvailableStock());
                }
        );
    }
    private void unReserveStock(List<ProductStock> entities ,Map<Long,Integer> id_quantityMap ){
        entities.forEach(
                stock ->{
                    long productId = stock.getProduct().getId();
                    int quantityNeeded = id_quantityMap.get(productId);
                    if(quantityNeeded > stock.getReservedStock())
                        throw  new IllegalArgumentException("can't unReserve stock , reserved will be negative!! , pid: " + productId + " quantity to unreserve:" +quantityNeeded);
                    stock.setReservedStock(stock.getReservedStock()-quantityNeeded);
                }
        );
    }

    private void totalStock(List<ProductStock> entities ,Map<Long,Integer> id_quantityMap ){
        entities.forEach(
                stock ->{
                    long productId = stock.getProduct().getId();
                    int quantityNeeded = id_quantityMap.get(productId);
                    if(quantityNeeded > stock.getReservedStock())
                        throw  new IllegalArgumentException("can't unreserve stock , reserved will be negative!! , pid: " + productId + "quantity to Unreserve:" +quantityNeeded);
                    if(quantityNeeded > stock.getStock())
                        throw  new IllegalArgumentException("can't update stock , stock will be negative!! , pid: " + productId + "quantity to Unreserve:" +quantityNeeded);
                    stock.setReservedStock(stock.getReservedStock()-quantityNeeded);
                    stock.setStock(stock.getStock()-quantityNeeded);
                }
        );
    }
}
