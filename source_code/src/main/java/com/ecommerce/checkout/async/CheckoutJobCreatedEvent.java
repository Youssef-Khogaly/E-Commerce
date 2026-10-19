package com.ecommerce.checkout.async;

import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.Instant;
import java.util.UUID;


@Getter
public class CheckoutJobCreatedEvent extends ApplicationEvent
{
    private final UUID jobId;

    public CheckoutJobCreatedEvent(CheckoutJob job) {
        super(job);
        this.jobId = job.getId();
    }
}


