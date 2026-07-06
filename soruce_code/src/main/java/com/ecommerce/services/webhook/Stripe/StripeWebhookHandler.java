package com.ecommerce.services.webhook.Stripe;


import com.ecommerce.DTO.Money;
import com.ecommerce.entities.Payments.Payment;
import com.ecommerce.entities.Payments.PaymentMethod;
import com.ecommerce.entities.Payments.PaymentState;
import com.ecommerce.entities.orders.Order;
import com.ecommerce.entities.orders.OrderItem;
import com.ecommerce.entities.orders.OrderState;
import com.ecommerce.repository.Order.OrderJpaRepo;
import com.ecommerce.repository.PaymentJpaRepo;
import com.ecommerce.services.StockService.StockService;
import com.ecommerce.services.webhook.Interfaces.PaymentWebhookHandler;
import com.ecommerce.services.webhook.Interfaces.PaymentWebhookParser;
import com.ecommerce.services.webhook.Interfaces.PaymentWebhookValidator;
import com.ecommerce.services.webhook.PaymentEvents;
import com.ecommerce.services.webhook.PaymentWebhookEvent;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Currency;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StripeWebhookHandler implements PaymentWebhookParser, PaymentWebhookValidator, PaymentWebhookHandler {

    private final String stripeHookSec;
    private final PaymentJpaRepo paymentJpaRepo;
    private final OrderJpaRepo orderJpaRepo;
    private final StockService stockService;
    public StripeWebhookHandler(@Value("${StripeWhsec}") String stripeHookSec, PaymentJpaRepo paymentJpaRepo, OrderJpaRepo orderJpaRepo, StockService stockService) {
        this.stripeHookSec = stripeHookSec;
        this.paymentJpaRepo = paymentJpaRepo;
        this.orderJpaRepo = orderJpaRepo;
        this.stockService = stockService;
    }

    private PaymentWebhookEvent toPaymentSucceedEvent(Event event){
        Session session = (Session) event.getDataObjectDeserializer().getObject().orElseThrow(() -> new RuntimeException("error parsing payment intent object , event Id:" + event.getId()));
        Money amount = new Money(session.getAmountTotal(), Currency.getInstance(session.getCurrency().toUpperCase(Locale.ROOT)));
        return PaymentWebhookEvent.builder()
                .id(event.getId())
                .provider(PaymentMethod.Stripe)
                .event(PaymentEvents.SUCCESS)
                .transactionId(session.getPaymentIntent())
                .sessionId(session.getId())
                .totalAmount(amount)
                .provider_created(event.getCreated())
                .metaData(session.getMetadata())
                .build();
    }
    private PaymentWebhookEvent toSessionExpireEvent(Event event){
        Session session = (Session) event.getDataObjectDeserializer().getObject().orElseThrow(() -> new RuntimeException("error parsing payment intent object , event Id:" + event.getId()));
        return PaymentWebhookEvent.builder()
                .id(event.getId())
                .provider(PaymentMethod.Stripe)
                .event(PaymentEvents.SESSION_EXPIRED)
                .transactionId(null)
                .sessionId(session.getId())
                .totalAmount(null)
                .provider_created(event.getCreated())
                .metaData(session.getMetadata())
                .build();
    }
    @Override
    public PaymentWebhookEvent parse(HttpServletRequest request , String payload) {
        Event event = Event.deserializeStripeObject(payload,Event.class,Event.getGlobalResponseGetter());
        return switch (event.getType()) {
            case "checkout.session.completed" -> toPaymentSucceedEvent(event);
            case "checkout.session.expired" -> toSessionExpireEvent(event);
            default -> null;
        };
    }

    @Override
    public void validate(HttpServletRequest request , String payload) throws Exception{
        Webhook.Signature.verifyHeader(payload,request.getHeader("Stripe-Signature"),stripeHookSec, TimeUnit.MINUTES.toSeconds(5));
    }

    private void handlePaymentSuccess(PaymentWebhookEvent event)
    {
        Long orderId = Long.valueOf(event.getMetaData().get("OrderId"));

        Order order = orderJpaRepo.findByIdForPaymentEvent(orderId);
//        if(order.getOrderState() != OrderState.CANCELED){
//            log.error("Error stripe payment successfully order event with expired order , orderId:{} paymnetId:{} , transactionId : {}", orderId, payment.getId() ,event.getTransactionId());
//        }
//        if(payment.getPaymentState() == PaymentState.CONFIRMED) // duplicate stripe web event
//            return;
//        payment.setTransaction_id(event.getTransactionId());
//        payment.setPaymentState(PaymentState.CONFIRMED);
//        // order can't be deleted without payment
//        order.setOrderState(OrderState.SHIPPING);
//        // release stock
//        Map<Long , Integer> iq_quantity_map = order.getOrderItems().stream().collect(Collectors.toMap(OrderItem::getProduct_id, OrderItem::getQuantity));
//        stockService.updatestock(iq_quantity_map, StockService.StockOperation.COMMIT);

    }

    private void handleSessionExpire(PaymentWebhookEvent event)
    {
        Long orderId = Long.valueOf(event.getMetaData().get("OrderId"));
        Order order = orderJpaRepo.findByIdForPaymentEvent(orderId);
//        Payment payment = order.
//        if(payment.getPaymentState() == PaymentState.CONFIRMED) // already paid , ignore
//        {
//            return;
//        }
//
//        paymentService.updateState(payment,PaymentState.EXPIRED);
//        order.setOrderState(OrderState.CANCELED);
//        // release stock
//        Map<Long , Integer> iq_quantity_map = order.getOrderItems().stream().collect(Collectors.toMap(OrderItem::getProduct_id , OrderItem::getQuantity));
//
//        stockHandle(iq_quantity_map, StockService.StockOperation.RELEASE);
//        log.info("order:{} expired and handled", orderId);
    }
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void handle(PaymentWebhookEvent event) {

        switch (event.getEvent()){
            case SUCCESS -> {
                handlePaymentSuccess(event) ;
                log.info("order:{} paid successfully", event.getMetaData().get("OrderId"));
                break;
            }
            case SESSION_EXPIRED -> handleSessionExpire(event);
        }
    }
}
