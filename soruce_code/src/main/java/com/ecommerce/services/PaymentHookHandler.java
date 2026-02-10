package com.ecommerce.services;

import com.ecommerce.entities.Payments.Payment;
import com.ecommerce.entities.Payments.PaymentState;
import com.ecommerce.entities.orders.Order;
import com.ecommerce.entities.orders.OrderItem;
import com.ecommerce.entities.orders.OrderState;
import com.ecommerce.repository.Order.OrderJpaRepo;
import com.ecommerce.repository.PaymentJpaRepo;
import com.ecommerce.services.StockService.StockService;
import com.ecommerce.services.interfaces.IPaymentService;
import jakarta.persistence.LockModeType;
import jakarta.persistence.OptimisticLockException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class PaymentHookHandler {

    private final Handler handler;
    public static enum PaymentEvents{
        PAID_SUCCESSFULLY , SESSION_EXPIRED
    }
    public void handle(UUID orderId , UUID paymentId , String transaction_id ,PaymentEvents event){
        int maxRetry = 5;
        int attempts = 0;
        long delay = 200;
        while(attempts < maxRetry){
            attempts++;
                try{
                    if(event == PaymentEvents.PAID_SUCCESSFULLY){
                        handler.paidSuccessfullyHandle(orderId,paymentId,transaction_id);
                        break;
                    }
                    else if (event == PaymentEvents.SESSION_EXPIRED){
                        handler.sessionExpireHandle(orderId,paymentId);
                        break;
                    }
                    log.error("Unexpected payment webhook event , event: {} , orderId: {} , paymentId: {} , transaction_id: {}" ,event,orderId,paymentId,transaction_id);
                    break;
                } catch (RuntimeException e) {
                    if(attempts > maxRetry-1)
                        throw  e;
                    try{
                        Thread.sleep(delay, 0);
                    } catch (InterruptedException ignored) {}
                }
        }
    }


    @AllArgsConstructor
    @Service
    @Slf4j
    public static class Handler{
        private final IPaymentService paymentService;
        private final OrderJpaRepo orderJpaRepo;
        private final StockService stockService;

        @Lock(value = LockModeType.PESSIMISTIC_WRITE)
        @Transactional(isolation = Isolation.READ_COMMITTED)
        public void paidSuccessfullyHandle(UUID orderId, UUID paymentId , @Nullable String transaction_id){
            Order order = orderJpaRepo.findByIdForPaymentEvent(orderId);
            Payment payment = order.getPayment();
            if(order.getOrderState() != OrderState.CANCELED && payment.getPaymentState() != PaymentState.EXPIRED){
                log.error("Error stripe payment successfully order event with expired order , orderId:{} paymnetId:{} , transactionId : {}", orderId, paymentId , transaction_id);
            }
            if(payment.getPaymentState() == PaymentState.CONFIRMED) // duplicate stripe web event
                return;
            payment.setTransaction_id(transaction_id);
            payment.setPaymentState(PaymentState.CONFIRMED);
            // order can't be deleted without payment
            order.setOrderState(OrderState.SHIPPING);
            // release stock
            Map<Long , Integer> iq_quantity_map = order.getOrderItems().stream().collect(Collectors.toMap(OrderItem::getProduct_id, OrderItem::getQuantity));
            stockHandle(iq_quantity_map, StockService.StockOperation.COMMIT);
            log.info("order:{} paid successfully", orderId);

        }
        @Lock(value = LockModeType.PESSIMISTIC_WRITE)
        @Transactional(isolation = Isolation.READ_COMMITTED)
        public void sessionExpireHandle(UUID orderId,UUID paymentId){
            Order order = orderJpaRepo.findById(orderId).orElse(null);
            Payment payment = paymentService.findPayment(paymentId).orElseGet(() -> null);
            if(order == null) {
                log.error("session expire webhook for non existing order , orderId: {}", orderId);
                return;
            }
            if(payment == null)
            {
                log.error("session expire webhook for order that does not have payment!! , orderId: {} , paymentId: {}" , orderId , paymentId);
                return;
            }

            if(payment.getPaymentState() == PaymentState.CONFIRMED) // already paid , ignore
            {
                return;
            }

            paymentService.updateState(payment,PaymentState.EXPIRED);
            order.setOrderState(OrderState.CANCELED);
            // release stock
            Map<Long , Integer> iq_quantity_map = order.getOrderItems().stream().collect(Collectors.toMap(OrderItem::getProduct_id , OrderItem::getQuantity));

            stockHandle(iq_quantity_map, StockService.StockOperation.RELEASE);
            log.info("order:{} expired and handled", orderId);
        }
        private void stockHandle(Map<Long , Integer> iq_quantity_map , StockService.StockOperation stockOperation){
            int maxRetry = 5;
            int attempts = 0;
            long delay = 10;
            boolean handled = false;
            while (attempts < maxRetry){
                ++attempts;
                try {
                    stockService.updatestock(iq_quantity_map,stockOperation);
                    handled = true;
                    break;
                }
                catch (OptimisticLockException optimisticLockException){
                    try{
                        Thread.sleep(delay, 0);
                    } catch (InterruptedException ignored) {}
                    if(attempts ==  4)
                        throw optimisticLockException;
                } catch (RuntimeException e) {
                    if(attempts == 4)
                        throw new RuntimeException(e);
                }
            }

            if(!handled)
                throw new RuntimeException(stockOperation + " stock operation failed ");
        }

    }

}
