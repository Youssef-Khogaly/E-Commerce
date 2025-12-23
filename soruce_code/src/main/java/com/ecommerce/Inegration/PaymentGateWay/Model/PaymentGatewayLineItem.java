package com.ecommerce.Inegration.PaymentGateWay.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Currency;

@AllArgsConstructor
@Getter@Setter
@NoArgsConstructor
public class PaymentGatewayLineItem {
    private  String itemName;
    private   String itemDescription;
    private   Currency currency;
    private   int quantity;
    private   long finalAmountInCents;
    private  String imgUrl;
}
