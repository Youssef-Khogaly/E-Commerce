package com.ecommerce.Payment.Mappers;

import com.ecommerce.Payment.dtos.PaymentDTO;
import com.ecommerce.Payment.entity.Payment;
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
