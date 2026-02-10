package com.ecommerce.Mappers;

import com.ecommerce.DTO.AddressDto;
import com.ecommerce.DTO.Money;
import com.ecommerce.DTO.OrderDTOView;
import com.ecommerce.DTO.ShippingDTO;
import com.ecommerce.entities.Payments.Payment;
import com.ecommerce.entities.Shiping.ShipingMethod;
import com.ecommerce.entities.orders.Order;
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
        orderview.setOrderState(order.getOrderState());
        orderview.setPaymentMethod(order.getPayment().getPaymentMethod());
        orderview.setTransactionId(order.getPayment().getTransaction_id());
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
