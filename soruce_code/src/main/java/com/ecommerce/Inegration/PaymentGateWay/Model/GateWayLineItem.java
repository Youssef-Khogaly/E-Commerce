package com.ecommerce.Inegration.PaymentGateWay.Model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Currency;


@NoArgsConstructor
@Getter
@Setter
public class GateWayLineItem extends GateWayLineItemAbstract {

    public GateWayLineItem(String itemName, String itemDescription, Currency currency, int quantity, long finalAmountInCents, String imgUrl) {
        super(itemName, itemDescription, currency, quantity, finalAmountInCents, imgUrl);
    }
}

