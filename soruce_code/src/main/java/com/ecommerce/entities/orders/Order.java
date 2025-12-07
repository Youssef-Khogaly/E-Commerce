package com.ecommerce.entities.orders;

import com.ecommerce.entities.Payments.Payment;
import com.ecommerce.entities.user.Customer;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.BiFunction;

@Entity
@Table(name="CustomerOrder")
@Getter@Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id" , columnDefinition = "BINARY(16)")
    private UUID id;
    @Column(name = "recipientName")
    @NotEmpty(message = "recipient name is required")
    @Length(min = 1, max = 63 , message = "Recipient name length must be less that 63 char and not empty")
    private String recipientName;
//    @NotEmpty(message = "recipient phone is required")
//    @Length(max = 63 , message = "recipient phone must be less then 63")
    @Column(name = "recipientPhone")
    private String recipientPhone;
//    @NotEmpty(message = "Country field is required")
//    @Length(max = 64 , message = "country length must be less that 65 ")
    private String country;
//    @NotEmpty(message = "city field is required")
//    @Length(max = 64,message = "city max length is 64")
    private String city;
//    @NotEmpty(message = "street field is required")
//    @Length(max = 64,message = "street max length is 64")
    private String street;
//    @NotEmpty(message = "building field is required")
//    @Length(max = 64,message = "building max length is 64")
    private String building;
    @Column(name = "order_state")
    @Enumerated(EnumType.STRING)
    private OrderState orderState;

    private Long subTotal;
    private String currency;
    @OneToMany(mappedBy = "order" ,cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<OrderItem> orderItems;

    @OneToOne(mappedBy = "order"  , fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Payment payment;

    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "cust_id")
    private Customer customer;

    public void addItem(OrderItem orderItem){
        orderItems.add(orderItem);
    }
}
