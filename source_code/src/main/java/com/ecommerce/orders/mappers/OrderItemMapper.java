package com.ecommerce.orders.mappers;

import com.ecommerce.Cart.Dto.CartItemDTO;
import com.ecommerce.orders.dtos.OrderItemDTO;
import com.ecommerce.orders.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class OrderItemMapper {


    public OrderItem from(OrderItemDTO orderItemDTO){
        OrderItem orderItem = new OrderItem();
        orderItem.setDescription(orderItemDTO.getDescription());
        orderItem.setDiscountInCents(orderItemDTO.getFinalDiscount().price());
        orderItem.setName(orderItemDTO.getName());
        orderItem.setQuantity(orderItemDTO.getQuantity());
        orderItem.setUnitPriceInCents(orderItemDTO.getUnitPrice().price());
        orderItem.setSubTotalInCents(orderItemDTO.getSubtotal().price());
        orderItem.setDiscountInCents(orderItemDTO.getFinalDiscount().price());
        orderItem.setProduct_id(orderItemDTO.getProduct_id());
        orderItem.setCurrencyCode(orderItemDTO.getSubtotal().currency().getCurrencyCode());

        return orderItem;
    }
    public List<OrderItem> from(Collection<OrderItemDTO> orderItemDTO){
        return orderItemDTO.stream().map(this::from).toList();
    }
    public OrderItem from(CartItemDTO cartItem){
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setProduct_id(orderItem.getProduct_id());
        orderItem.setName(cartItem.getProductDTO().getName());
        orderItem.setCurrencyCode(cartItem.getProductDTO().getPrice().currency().getCurrencyCode());
        orderItem.setUnitPriceInCents(cartItem.getProductDTO().getPrice().price());
        orderItem.setDiscountInCents(0L);
        orderItem.setSubTotalInCents(cartItem.getProductDTO().getPrice().multi(cartItem.getQuantity()).price());
        orderItem.setProduct_id(cartItem.getProductDTO().getId());
        return orderItem;
    }
}
