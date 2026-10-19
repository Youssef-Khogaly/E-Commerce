package com.ecommerce.checkout.async;

import com.ecommerce.Inegration.PaymentGateWay.Model.SessionGenerationCommand;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;



@Entity
@Table(name = "checkoutJob")
@Getter
@Setter
public class CheckoutJob {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;
    private long userId;
    private long orderId;

    @Enumerated(EnumType.STRING)
    private CheckoutJobStatus status;
    private String sessionId;
    private String sessionUrl;
    @JdbcTypeCode(SqlTypes.JSON)
    private SessionGenerationCommand command;
    private Instant createdAt;
}
