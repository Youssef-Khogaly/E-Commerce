package com.ecommerce.Mappers;


import com.ecommerce.ApplicationConstants;
import com.ecommerce.DTO.CartDTO;
import com.ecommerce.DTO.OrderDTO;
import com.ecommerce.DTO.ShippingDTO;
import com.ecommerce.entities.orders.Order;
import com.ecommerce.entities.orders.OrderItem;
import com.ecommerce.entities.user.Customer;
import com.ecommerce.repository.UserJpaRepo;
import com.ecommerce.repository.UsersRepo.CustomerJpaRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class OrderMapper {

    private final OrderItemMapper orderItemMapper;
    private final PaymentMapper paymentMapper;
    private final CustomerJpaRepo customerJpaRepo;
    public Order from(OrderDTO orderDTO){
        var  order = new Order();
        order.setState(orderDTO.getOrderState());
        order.setOrderItems(orderItemMapper.from(orderDTO.getOrderItemDTOS()));
        // address
        order = setOrderShipping(order,orderDTO.getShippingDTO());
        order.setCurrencyCode(orderDTO.getTotal().getCurrency().toString());
        order.setSubTotal(orderDTO.getTotal().getPrice());

        order.setCustomer(customerJpaRepo.getReferenceById(orderDTO.getCust_id()));
        final Order orderTmp = order;
        order.getOrderItems().forEach(i -> i.setOrder(orderTmp));
        return order;
    }
    private Order setOrderShipping(Order order,ShippingDTO shippingDTO){
        //shipping
        order.setRecipientName(shippingDTO.getRecipientName());
        order.setRecipientPhone(shippingDTO.getRecipientPhone());
        order.setCountry(shippingDTO.getShippingAddress().country());
        order.setCity(shippingDTO.getShippingAddress().city());
        order.setStreet(shippingDTO.getShippingAddress().street());
        order.setBuilding(shippingDTO.getShippingAddress().buildingDetail());
        return order;
    }
    public Order from(CartDTO cart , ShippingDTO shippingDTO)
    {
        var  order = new Order();
        order.setCustomer(customerJpaRepo.getReferenceById(cart.getCartId()));
        order.setCurrencyCode(ApplicationConstants.defaultCurrency.getCurrencyCode());
        setOrderShipping(order,shippingDTO);
        order.setOrderItems(cart.getItems().stream().map(orderItemMapper::from).collect(Collectors.toCollection(ArrayList::new)));
        order.getOrderItems().forEach(item -> item.setOrder(order));
        order.setSubTotal(order.getOrderItems().stream().map(OrderItem::getSubTotalInCents).reduce(0L,Long::sum));
        return order;
    }
}
