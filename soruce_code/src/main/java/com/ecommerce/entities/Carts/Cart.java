package com.ecommerce.entities.Carts;

import com.ecommerce.entities.user.Customer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@RedisHash(value = "cart",timeToLive = 604800) // 7 days ttl
@Getter
@Setter
public class Cart {


    @Id
    private Long id;

    private Map<Long,Integer> productId_quantity_map = new HashMap<>();



    public void putItem(final long id , final int quantity)
    {
        productId_quantity_map.put(id,quantity);
    }
    public void addItem(final long id , final int quantity)
    {
        productId_quantity_map.compute(id,(k,oldval) -> {
            if(oldval == null)
                return quantity;
            return oldval+quantity;
        });
    }
    public void removeItem(final long id)
    {
        productId_quantity_map.remove(id);
    }

}
