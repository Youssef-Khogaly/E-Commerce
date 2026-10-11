package com.ecommerce.Payment.dtos;

import com.ecommerce.Payment.entity.PaymentState;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentDTO {
    private Long id;
    private PaymentState paymentState;
    private String transaction_id;
    private String currency;
    private Long amount;
}
