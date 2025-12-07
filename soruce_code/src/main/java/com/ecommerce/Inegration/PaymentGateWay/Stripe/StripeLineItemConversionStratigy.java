package com.ecommerce.Inegration.PaymentGateWay.Stripe;


import com.ecommerce.Inegration.PaymentGateWay.Model.GateWayLineItemAbstract;
import com.stripe.param.checkout.SessionCreateParams;

public interface StripeLineItemConversionStratigy {


    public <T extends GateWayLineItemAbstract>SessionCreateParams.LineItem convertToLineItem(T itemModel);
}
