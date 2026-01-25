package com.ecommerce.Mappers;

import com.ecommerce.DTO.PaymentDTO;
import com.ecommerce.entities.Payments.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {



    public Payment from(PaymentDTO paymentDTO){
        var payment = new Payment();
        payment.setPaymentState(paymentDTO.getPaymentState());
        payment.setPaymentMethod(paymentDTO.getPaymentMethod());
        payment.setExpireAt(paymentDTO.getExpireAt());
        payment.setSession_id(paymentDTO.getSession_id());
        payment.setTransaction_id(paymentDTO.getTransaction_id());
        return payment;
    }
}
