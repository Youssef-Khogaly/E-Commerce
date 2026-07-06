package com.ecommerce.DTO;

import com.ecommerce.entities.Payments.PaymentMethod;
import com.ecommerce.entities.Payments.PaymentState;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PaymentDTO {
    private Long id;
    private PaymentState paymentState;
    private String transaction_id;
    private String currency;
    private Long amount;
}
