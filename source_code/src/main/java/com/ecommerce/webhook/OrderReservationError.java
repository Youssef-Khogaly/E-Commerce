package com.ecommerce.webhook;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Primary;

@Entity
@Table(name = "orderReservationError")
@Getter
@Setter
public class OrderReservationError {
    @Id
    private Long orderId;

    private Long resID;

    private String error;
}
