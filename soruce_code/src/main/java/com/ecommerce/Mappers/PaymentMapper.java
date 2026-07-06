package com.ecommerce.Mappers;

import com.ecommerce.DTO.PaymentDTO;
import com.ecommerce.entities.Payments.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {



    public Payment from(PaymentDTO paymentDTO){
        var payment = new Payment();
        payment.setPaymentState(paymentDTO.getPaymentState());
        payment.setCurrency(paymentDTO.getCurrency());
        payment.setAmount(paymentDTO.getAmount());
        payment.setTransactionId(paymentDTO.getTransaction_id());
        return payment;
    }
}
