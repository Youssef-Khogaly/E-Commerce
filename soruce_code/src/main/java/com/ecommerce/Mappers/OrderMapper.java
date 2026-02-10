package com.ecommerce.Mappers;


import com.ecommerce.ApplicationConstants;
import com.ecommerce.DTO.OrderDTO;
import com.ecommerce.DTO.ShippingDTO;
import com.ecommerce.entities.Carts.Cart;
import com.ecommerce.entities.Payments.Payment;
import com.ecommerce.entities.Payments.PaymentMethod;
import com.ecommerce.entities.Payments.PaymentState;
import com.ecommerce.entities.orders.Order;
import com.ecommerce.entities.orders.OrderItem;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class OrderMapper {

    private final OrderItemMapper orderItemMapper;
    private final PaymentMapper paymentMapper;
    public Order from(OrderDTO orderDTO){
        var  order = new Order();
        order.setOrderState(orderDTO.getOrderState());
        order.setOrderItems(orderItemMapper.from(orderDTO.getOrderItemDTOS()));
        // address
        order = setOrderShipping(order,orderDTO.getShippingDTO());
        order.setCurrencyCode(orderDTO.getTotal().getCurrency().toString());
        order.setSubTotal(orderDTO.getTotal().getPrice());

        order.setCustomer_id(orderDTO.getCust_id());
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
    public Order from(Cart cart , ShippingDTO shippingDTO)
    {
        var  order = new Order();
        order.setCustomer_id(cart.getId());
        order.setCurrencyCode(ApplicationConstants.defaultCurrency.getCurrencyCode());
        setOrderShipping(order,shippingDTO);
        order.setOrderItems(cart.getCartItemSet().stream().map(orderItemMapper::from).toList());
        order.getOrderItems().forEach(item -> item.setOrder(order));
        order.setSubTotal(order.getOrderItems().stream().map(OrderItem::getSubTotalInCents).reduce(0L,Long::sum));
        return order;
    }
}
