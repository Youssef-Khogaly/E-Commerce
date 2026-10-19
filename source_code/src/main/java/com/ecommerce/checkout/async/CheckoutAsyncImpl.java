package com.ecommerce.checkout.async;

import com.ecommerce.Inegration.PaymentGateWay.Model.PaymentSession;
import com.ecommerce.Inegration.PaymentGateWay.Model.SessionGenerationCommand;
import com.ecommerce.Payment.services.IPaymentGatewayService;
import com.ecommerce.orders.services.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@Service
@Slf4j
public class CheckoutAsyncImpl  implements CheckoutSessionGenerationPublisher,CheckoutSessionGenerationHandler{
    private final ExecutorService executorService;
    private final CheckoutJobRepo checkoutJobRepo;
    private final IPaymentGatewayService paymentGatewayService;
    private final OrderService orderService;
    private final RetryTemplate retryTemplate;
    private final TransactionTemplate transactionTemplate;
    public CheckoutAsyncImpl(CheckoutJobRepo checkoutJobRepo, IPaymentGatewayService paymentGatewayService, OrderService orderService , PlatformTransactionManager platformTransactionManager) {
        this.checkoutJobRepo = checkoutJobRepo;
        this.paymentGatewayService = paymentGatewayService;
        this.orderService = orderService;
        BlockingQueue<Runnable> queue = new LinkedTransferQueue<>();
        this.executorService = new ThreadPoolExecutor(2,10,500L, TimeUnit.MILLISECONDS,queue);
        retryTemplate = RetryTemplate.builder().maxAttempts(5).exponentialBackoff(100,2.0,1000)
                .build();
        this.transactionTemplate = new TransactionTemplate(platformTransactionManager);
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
    }

    public void handle(CheckoutJobCreatedEvent event) {
        var checkoutJob = checkoutJobRepo.findById(event.getJobId()).orElseGet(() -> null);
        if(checkoutJob == null ) // maybe db transaction no commited yet
        {
            log.error("checkout job does not exists!!, cannot handle, jobId:{}" , event.getJobId());
            return; // should never happen
        }
        if(checkoutJob.getStatus() != CheckoutJobStatus.PROCESSING) // handled before
            return;
        var command = checkoutJob.getCommand();
        try{
            PaymentSession session = retryTemplate.execute((context) ->  paymentGatewayService.generateSessionUrl(command));

            transactionTemplate.executeWithoutResult((c) -> {
                checkoutJob.setSessionId(session.getSession_id());
                checkoutJob.setSessionUrl(session.getSession_url());
                checkoutJob.setStatus(CheckoutJobStatus.SUCCESS);
                orderService.findById(command.getOrderModel().getOrder_id()).setSession_id(session.getSession_id());
                checkoutJobRepo.save(checkoutJob);
            });


        } catch (Exception e) {
            transactionTemplate.executeWithoutResult((c) -> {
                checkoutJob.setStatus(CheckoutJobStatus.FAILED);
                orderService.cancelOrder(command.getOrderModel().getOrder_id());
                checkoutJobRepo.save(checkoutJob);
            });
        }


    }

    @Override
    public void publish(CheckoutJobCreatedEvent event) {
        executorService.submit(() ->{
            this.handle(event);
        });

    }
}
