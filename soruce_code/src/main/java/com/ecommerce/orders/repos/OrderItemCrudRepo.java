package com.ecommerce.orders.repos;

import com.ecommerce.orders.entity.OrderItem;
import com.ecommerce.orders.entity.OrderItemId;
import org.springframework.data.repository.CrudRepository;

public interface OrderItemCrudRepo extends CrudRepository<OrderItem, OrderItemId> {
}
