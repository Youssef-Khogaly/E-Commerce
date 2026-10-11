package com.ecommerce.orders.mappers;

import com.ecommerce.orders.dtos.AddressDto;
import com.ecommerce.util.Money;
import com.ecommerce.orders.dtos.OrderDTOView;
import com.ecommerce.orders.dtos.ShippingDTO;
import com.ecommerce.util.ShipingMethod;
import com.ecommerce.orders.entity.Order;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class OrderDtoViewMapper {
    private final OrderItemDtoMapper orderItemDtoMapper;
    public OrderDTOView from(Order order ){
        var orderview = new OrderDTOView();
        orderview.setTotal(new Money(order.getSubTotal(),order.getCurrencyCode()));
        orderview.setOrder_id(order.getId());
        orderview.setOrderState(order.getState());
        orderview.setPaymentMethod(order.getPaymentMethod());
        orderview.setOrderItemDTOS(order.getOrderItems().stream().map(orderItemDtoMapper::from).toList());
        var shippingDto = new ShippingDTO();
        shippingDto.setRecipientName(order.getRecipientName());
        shippingDto.setRecipientPhone(order.getRecipientPhone());
        shippingDto.setMethod(ShipingMethod.NOT_SUPPORTED_YET);
        shippingDto.setShippingAddress(new AddressDto(order.getCountry(),order.getCity(),order.getStreet(),order.getBuilding()));
        orderview.setShippingDTO(shippingDto);

        return orderview;
    }
}
