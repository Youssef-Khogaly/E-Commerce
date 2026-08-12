package com.ecommerce.Mappers;

import com.ecommerce.DTO.CartItemDTO;
import com.ecommerce.DTO.OrderItemDTO;
import com.ecommerce.entities.orders.OrderItem;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class OrderItemMapper {


    public OrderItem from(OrderItemDTO orderItemDTO){
        OrderItem orderItem = new OrderItem();
        orderItem.setDescription(orderItemDTO.getDescription());
        orderItem.setDiscountInCents(orderItemDTO.getFinalDiscount().getPrice());
        orderItem.setName(orderItemDTO.getName());
        orderItem.setQuantity(orderItemDTO.getQuantity());
        orderItem.setUnitPriceInCents(orderItemDTO.getUnitPrice().getPrice());
        orderItem.setSubTotalInCents(orderItemDTO.getSubtotal().getPrice());
        orderItem.setDiscountInCents(orderItemDTO.getFinalDiscount().getPrice());
        orderItem.setProduct_id(orderItemDTO.getProduct_id());
        orderItem.setCurrencyCode(orderItemDTO.getSubtotal().getCurrency().getCurrencyCode());

        return orderItem;
    }
    public List<OrderItem> from(Collection<OrderItemDTO> orderItemDTO){
        return orderItemDTO.stream().map(this::from).toList();
    }
    public OrderItem from(CartItemDTO cartItem){
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setProduct_id(orderItem.getProduct_id());
        orderItem.setName(cartItem.getProductDTO().getTitle());
        orderItem.setCurrencyCode(cartItem.getProductDTO().getPriceInCents().getCurrency().getCurrencyCode());
        orderItem.setUnitPriceInCents(cartItem.getProductDTO().getPriceInCents().getPrice());
        orderItem.setDiscountInCents(0L);
        orderItem.setSubTotalInCents(cartItem.getProductDTO().getPriceInCents().multi(cartItem.getQuantity()).getPrice());
        orderItem.setProduct_id(cartItem.getProductDTO().getId());
        return orderItem;
    }
}
