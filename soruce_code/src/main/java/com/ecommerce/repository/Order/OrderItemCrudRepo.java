package com.ecommerce.repository.Order;

import com.ecommerce.entities.orders.OrderItem;
import com.ecommerce.entities.orders.OrderItemId;
import org.springframework.data.repository.CrudRepository;

public interface OrderItemCrudRepo extends CrudRepository<OrderItem, OrderItemId> {
}
