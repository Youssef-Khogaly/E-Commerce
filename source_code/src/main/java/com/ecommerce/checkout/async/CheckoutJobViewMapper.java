package com.ecommerce.checkout.async;

import org.springframework.stereotype.Component;

@Component
public class CheckoutJobViewMapper {



    public CheckoutJobView from(CheckoutJob checkoutJob)
    {
        return CheckoutJobView
                .builder().id(checkoutJob.getId())
                .orderId(checkoutJob.getOrderId())
                .status(checkoutJob.getStatus())
                .sessionUrl(checkoutJob.getSessionUrl())
                .createdAt(checkoutJob.getCreatedAt())
                .build();
    }
}
