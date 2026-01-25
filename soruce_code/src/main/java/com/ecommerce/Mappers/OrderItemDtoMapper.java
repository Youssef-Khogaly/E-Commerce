package com.ecommerce.Mappers;

import com.ecommerce.DTO.CartItemDTO;
import com.ecommerce.DTO.Money;
import com.ecommerce.DTO.OrderItemDTO;
import com.ecommerce.entities.orders.OrderItem;
import org.springframework.stereotype.Component;

@Component
public class OrderItemDtoMapper {

    public OrderItemDTO from(CartItemDTO cartItemDTO){
        var  dto = new OrderItemDTO();
        dto.setProduct_id(cartItemDTO.getProductDTO().getId());
        dto.setName(cartItemDTO.getProductDTO().getTitle());
        dto.setQuantity(cartItemDTO.getQuantity());
        dto.setSubtotal(cartItemDTO.getSubTotalInCents());
        dto.setFinalDiscount(cartItemDTO.getProductDTO().getDiscount().getMoney());
        dto.setUnitPrice(cartItemDTO.getProductDTO().getPriceInCents());
        return dto;
    }

    public OrderItemDTO from(OrderItem orderItem){
        var item  = new OrderItemDTO();
        item.setName(orderItem.getName());
        item.setDescription(orderItem.getDescription());
        item.setProduct_id(orderItem.getProduct_id());
        item.setQuantity(orderItem.getQuantity());
        item.setUnitPrice(new Money(orderItem.getUnitPriceInCents(),orderItem.getCurrencyCode()));
        item.setFinalDiscount(new Money(orderItem.getDiscountInCents(),orderItem.getCurrencyCode()));
        item.setSubtotal(new Money(orderItem.getSubTotalInCents(),orderItem.getCurrencyCode()));
       return item;
    }
}
