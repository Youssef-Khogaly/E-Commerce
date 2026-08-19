package com.ecommerce.orders.mappers;

import com.ecommerce.Cart.Dto.CartDTO;
import com.ecommerce.Cart.Dto.CartItemDTO;
import com.ecommerce.Payment.dtos.PaymentDTO;
import com.ecommerce.orders.dtos.OrderDTO;
import com.ecommerce.orders.entity.OrderState;
import com.ecommerce.orders.dtos.ShippingDTO;
import com.ecommerce.util.Money;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

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
        ret.setShippingDTO(shippingDTO);
        ret.setTotal(cartDTO.getItems().stream().map(CartItemDTO::getSubTotalInCents).reduce(new Money(0),Money::add));
        return ret;
    }
}
