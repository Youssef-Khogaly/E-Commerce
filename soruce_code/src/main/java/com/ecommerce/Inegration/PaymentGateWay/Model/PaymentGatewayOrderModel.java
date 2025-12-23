package com.ecommerce.Inegration.PaymentGateWay.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentGatewayOrderModel {
    private UUID order_id;
    private  long customer_id;
    private  UUID payment_id;
    private  List<PaymentGatewayLineItem> items;

}
