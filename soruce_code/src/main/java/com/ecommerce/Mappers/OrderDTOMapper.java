package com.ecommerce.Mappers;

import com.ecommerce.DTO.*;
import com.ecommerce.entities.Payments.PaymentMethod;
import com.ecommerce.entities.orders.Order;
import com.ecommerce.entities.orders.OrderState;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@AllArgsConstructor
@Component
public class OrderDTOMapper {

    private final OrderItemDtoMapper orderItemDtoMapper;

    public OrderDTO from(CartDTO cartDTO, ShippingDTO shippingDTO , PaymentDTO paymentDTO , Long cust_id)
    {
        var ret = new OrderDTO();
        ret.setCust_id(cust_id);
        ret.setOrder_id(null);
        ret.setOrderItemDTOS(cartDTO.getItems().stream().map(orderItemDtoMapper::from).toList());
        ret.setOrderState(OrderState.PENDING);
        ret.setPaymentDTO(paymentDTO);
        ret.setShippingDTO(shippingDTO);
        ret.setTotal(cartDTO.getItems().stream().map(CartItemDTO::getSubTotalInCents).reduce(new Money(0),Money::add));
        return ret;
    }
}
