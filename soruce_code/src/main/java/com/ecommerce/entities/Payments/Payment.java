package com.ecommerce.entities.Payments;

import com.ecommerce.entities.orders.Order;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

/*

CREATE TABLE IF NOT EXISTS payment (
    payment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT not null ,
    paymentState ENUM('failed','pending','confirmed','refunded') DEFAULT 'pending' not null,
    paymentMethod enum('CASH_ON_DELIVERY' , 'STRIPE'),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_order FOREIGN KEY (order_id)
    REFERENCES `order`(order_id)
                                      ON UPDATE CASCADE
                                      ON DELETE RESTRICT
    );
 */
@Entity
@Table(name = "Payment")
@Getter@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "payment_id" , columnDefinition = "BINARY(16)")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY )
    @JoinColumn(name = "order_id")
    private Order order;

    @Enumerated(EnumType.STRING)
    private PaymentState paymentState;
    @Enumerated(EnumType.STRING)
    @NotNull
    private PaymentMethod paymentMethod;

    private String transaction_id;
    private String session_id;
    private Long expireAt;
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(getId(), payment.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

}
