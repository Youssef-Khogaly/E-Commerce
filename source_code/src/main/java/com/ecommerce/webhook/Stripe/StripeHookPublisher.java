package com.ecommerce.webhook.Stripe;


import com.ecommerce.webhook.Interfaces.PaymentWebhookHandler;
import com.ecommerce.webhook.Interfaces.PaymentWebhookPublisher;
import com.ecommerce.webhook.PaymentWebhookEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

@Slf4j
@Service
public class StripeHookPublisher implements PaymentWebhookPublisher {

    private final ExecutorService executorService;
    private final PaymentWebhookHandler handler;
    private static final int coreSize = 2;
    private static final int maxPoolSize = 3;
    private static final int keepAliveMilli = 10000;
    private static final int queueSize = 512;


    public StripeHookPublisher(PaymentWebhookHandler handler) {
        this.handler = handler;
        ThreadFactory threadFactory = (t) ->{
            var thread = new Thread(t);
            thread.setName("stripe webhook worker");
            return thread;
        };
        this.executorService = new ThreadPoolExecutor(coreSize,maxPoolSize,keepAliveMilli
                , TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(queueSize) , threadFactory);
    }
    private Runnable getRunnable(PaymentWebhookEvent event)
    {
        return () -> {
                handler.handle(event);
        };

    }

    @Override
    public void publish(PaymentWebhookEvent event) {
        var runnable = getRunnable(event);

        executorService.execute(runnable);
    }

}

