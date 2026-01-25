package com.ecommerce.DTO;

import com.ecommerce.entities.Payments.PaymentMethod;
import com.ecommerce.entities.Payments.PaymentState;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PaymentDTO {
    private UUID id;
    private PaymentState paymentState;
    private PaymentMethod paymentMethod;
    private String transaction_id;
    private String session_id;
    private Long expireAt;
}
