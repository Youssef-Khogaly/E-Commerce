package com.ecommerce.Inegration.PaymentGateWay.Stripe;


import com.ecommerce.Inegration.PaymentGateWay.Model.GateWayLineItemAbstract;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.stereotype.Component;

@Component
public class StripeLineItemConversionStratigyImpl implements StripeLineItemConversionStratigy{
    @Override
    public <T extends GateWayLineItemAbstract> SessionCreateParams.LineItem convertToLineItem(T itemModel) {

        var product = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(itemModel.getItemName() + "  Qty: " + itemModel.getQuantity())
                .setDescription(
                itemModel.getItemDescription()
        );
        long finalAmount = itemModel.getFinalAmountInCents();
        if(itemModel.getImgUrl() != null)
            product = product.addImage(itemModel.getImgUrl());

        var priceData = SessionCreateParams.LineItem.PriceData.builder()
                .setProductData(product.build())
                .setCurrency(itemModel.getCurrency().getCurrencyCode())
                .setUnitAmount(finalAmount)
                .build();

        return SessionCreateParams.LineItem.builder().setQuantity(1L).setPriceData(priceData).build();
    }
}
