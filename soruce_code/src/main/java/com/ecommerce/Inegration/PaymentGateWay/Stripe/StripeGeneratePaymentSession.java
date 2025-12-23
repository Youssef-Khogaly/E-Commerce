package com.ecommerce.Inegration.PaymentGateWay.Stripe;


import com.ecommerce.Inegration.PaymentGateWay.Exception.GateWayException;
import com.ecommerce.Inegration.PaymentGateWay.Exception.GateWayInvalidSessionDuration;
import com.ecommerce.Inegration.PaymentGateWay.Interfaces.GeneratePaymentSession;
import com.ecommerce.Inegration.PaymentGateWay.Mappers.StripeMappers;
import com.ecommerce.Inegration.PaymentGateWay.Model.PaymentGatewayLineItem;
import com.ecommerce.Inegration.PaymentGateWay.Model.PaymentSession;
import com.ecommerce.Inegration.PaymentGateWay.Model.SessionGenerationCommand;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class StripeGeneratePaymentSession implements GeneratePaymentSession {

    public StripeGeneratePaymentSession(@Value("${StripeApisec}")String sec) {
        Stripe.apiKey = sec ;
    }

    protected List<SessionCreateParams.LineItem> toLineItemsList(List<PaymentGatewayLineItem> items){
        return items.stream().map(StripeMappers::convertToLineItem).toList();
    }
    protected <T extends SessionGenerationCommand> SessionCreateParams createSessionPara(T command){
            return
                    SessionCreateParams.builder()
                            .addAllLineItem(toLineItemsList(command.getOrderModel().getItems()))
                            .setExpiresAt(Instant.now().getEpochSecond() + command.getExpireAfter().getSeconds())
                            .setMode(SessionCreateParams.Mode.PAYMENT)
                            .setSuccessUrl(command.getSuccessUrl())
                            .setCancelUrl(command.getFailUrl())
                            .putMetadata("OrderId",String.valueOf(command.getOrderModel().getOrder_id()))
                            .putMetadata("OrderPaymentId" , command.getOrderModel().getPayment_id().toString())
                            .setPaymentIntentData(
                                    SessionCreateParams.PaymentIntentData.builder()
                                            .putMetadata("OrderId",String.valueOf(command.getOrderModel().getOrder_id()))
                                            .putMetadata("OrderPaymentId" , command.getOrderModel().getPayment_id().toString()).build()
                            )
                            .build();
    }

    @Override
    public PaymentSession generateSessionUrl(SessionGenerationCommand command) {

        if (command.getExpireAfter().compareTo(Duration.ofMinutes(30)) < 1){
            throw new GateWayInvalidSessionDuration("min stripe session url duration is: 30min");
        }
        SessionCreateParams params =createSessionPara(command);

        Session session = null;
        try{
            session = Session.create(params);
        }catch (StripeException e){
            log.error(this.getClass().getName()+ "msg:" +e.getMessage() + "\n" + " strip error:" + e.getStripeError());
            throw  new GateWayException(e.getMessage());
        }

        return new PaymentSession(session.getId(),session.getUrl(),session.getClientReferenceId() ,command.getOrderModel(),Instant.ofEpochSecond(session.getExpiresAt()));
    }
}
