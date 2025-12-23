package com.ecommerce.services;

import com.ecommerce.DTO.*;
import com.ecommerce.Exception.BadRequestException;
import com.ecommerce.Exception.NotFoundException;
import com.ecommerce.Inegration.PaymentGateWay.CancelPaymentSessionFactory;
import com.ecommerce.entities.Payments.Payment;
import com.ecommerce.entities.Payments.PaymentMethod;
import com.ecommerce.entities.Payments.PaymentState;
import com.ecommerce.entities.Products.Product;
import com.ecommerce.entities.orders.Order;
import com.ecommerce.entities.orders.OrderItem;
import com.ecommerce.entities.orders.OrderState;
import com.ecommerce.repository.Order.OrderCrudRepo;
import com.ecommerce.repository.PaymentJpaRepo;
import com.ecommerce.repository.Product.ProductJpaRepo;
import com.ecommerce.repository.UsersRepo.CustomerJpaRepo;
import com.ecommerce.services.StockService.StockService;
import com.ecommerce.services.interfaces.IPaymentGatewayService;
import com.ecommerce.services.interfaces.OrderService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import jakarta.persistence.LockModeType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.atn.SemanticContext;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Slf4j
@AllArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderCrudRepo orderCrudRepo;
    private final CustomerJpaRepo customerJpaRepo;
    private final ProductJpaRepo productJpaRepo;
    private final StockService stockService;
    private final IPaymentGatewayService paymentGatewayService;
    @Override
    public Order createOrder(CartDTO cartDTO, ShippingDTO shippingDTO, PaymentMethod method) {
        final Long[] subTotalOrder  = new Long[1];
        subTotalOrder[0] = 0l;
        Order order = new Order();
        order.setOrderState(OrderState.PENDING);

        order.setCurrency("EGP");
        setOrderShipping(shippingDTO,order);

        List<OrderItem> orderItems = toOrderItem(cartDTO.getItems(),subTotalOrder,order);
        order.setSubTotal(subTotalOrder[0]);
        order.setOrderItems(orderItems);

        order.setCustomer(customerJpaRepo.getReferenceById(cartDTO.getCartId()));


        order.setPayment(createPayment(method,order));
        order.getPayment().setOrder(order);
        order = orderCrudRepo.save(order);
        return order;
    }

    @Override
    public List<OrderDTOView> getOrders(Long cust_id) {

        List<Order> order = orderCrudRepo.findAllByCustomerIdListView(cust_id);

        return order.stream().map(this::toListView).toList();
    }
    public OrderDTOView getOrder(Long customer_id , UUID orderId){

        Order order = orderCrudRepo.findByCustomerIdAndOrderId(orderId,customer_id);
        if(order == null)
            throw new NotFoundException("order is not found or not attached to this customer");
        OrderDTOView orderDTOView = toListView(order);
        orderDTOView.setOrderItemDTOS(toOrderItemDto(order.getOrderItems()));

        return orderDTOView;
    }

    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void cancelOrder(Long customer_id, UUID orderId) {
        Order order = orderCrudRepo.findWithAllByIdAndCustId(orderId,customer_id);
        if(order == null)
            throw new NotFoundException("order is not found or not attached to this customer");
        if(order.getOrderState() ==  OrderState.PENDING){
            // cancel session
            cancelSession(order.getPayment().getSession_id() , order.getPayment().getPaymentMethod());
            // don't handle it stock un reservation logic here or update states
            // gateway gonna send webhook and event will be handled async

        }
        else if (order.getOrderState() == OrderState.SHIPPING || order.getOrderState() == OrderState.DELIVERED)
            throw new BadRequestException("can't cancel the order already paid");
        else if (OrderState.REFUNDED == order.getOrderState())
            throw new BadRequestException("can't cancel refunded order");

    }
    private void cancelSession(String sessionId , PaymentMethod method){
        paymentGatewayService.cancelSession(sessionId,method);
    }
    private List<OrderItemDTO> toOrderItemDto(List<OrderItem> orderItems){

        return orderItems.stream().map(
                i ->{
                    var  dto = new OrderItemDTO();
                    dto.setName(i.getName());
                    dto.setDescription(dto.getDescription());
                    dto.setQuantity(dto.getQuantity());
                    dto.setSubtotalInCents(i.getSubTotalInCents());
                    return dto;
                }
        ).toList();
    }
    private OrderDTOView toListView(Order order){
        var view = new OrderDTOView();
        view.setCurrency(order.getCurrency());
        view.setOrder_id(order.getId());
        view.setPaymentMethod(order.getPayment().getPaymentMethod());
        view.setTotalInCents(order.getSubTotal());
        view.setTransactionId(order.getPayment().getTransaction_id());
        view.setOrderState(order.getOrderState());
        var shipping = toShippingDto(order);
        view.setShippingDTO(shipping);
        view.setOrderItemDTOS(null);
        return view;
    }
    private ShippingDTO toShippingDto(Order order){
        var shipping = new ShippingDTO();
        shipping.setRecipientName(order.getRecipientName());
        shipping.setRecipientPhone(order.getRecipientPhone());
        var add = new AddressDto(order.getCountry(),order.getCity(),order.getStreet(),order.getBuilding());
        shipping.setShippingAddress(add);
        return shipping;
    }
    protected void setOrderShipping(ShippingDTO shipping , Order order){
        order.setCountry(shipping.getShippingAddress().country());
        order.setCity(shipping.getShippingAddress().city());
        order.setStreet(shipping.getShippingAddress().street());
        order.setBuilding(shipping.getShippingAddress().buildingDetail());
        order.setRecipientName(shipping.getRecipientName());
        order.setRecipientPhone(shipping.getRecipientPhone());
    }
    protected Payment createPayment(PaymentMethod paymentMethod, Order order){
        Payment payment = new Payment();
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentState(PaymentState.PENDING);
        payment.setOrder(order);
        return payment;
    }
    protected List<OrderItem> toOrderItem(List<CartItemDTO> cartItems , final Long[] subTotal , Order order){
        return cartItems.stream().map(
                item ->{
                    var orderItem = new OrderItem();
                    var product = item.getProductDTO();
                    orderItem.setProduct(productJpaRepo.getReferenceById(product.getId()));
                    orderItem.setQuantity(item.getQuantity());
                    orderItem.setName(product.getName());
                    orderItem.setDescription(product.getDescription());
                    orderItem.setDiscountInCents(product.getDiscountInCents());
                    orderItem.setUnitPriceInCents(product.getPriceInCents());
                    orderItem.setSubTotalInCents((product.getPriceInCents() - product.getDiscountInCents()) * orderItem.getQuantity());
                    subTotal[0] += orderItem.getSubTotalInCents();
                    orderItem.setOrder(order);
                    return orderItem;
                }
        ).toList();
    }
}
