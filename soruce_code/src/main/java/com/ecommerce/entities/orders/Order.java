package com.ecommerce.entities.orders;

import com.ecommerce.entities.Payments.Payment;
import com.ecommerce.entities.Payments.PaymentMethod;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.util.*;

@Entity
@Table(name="CustomerOrder")
@Getter@Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;
    @Column(name = "recipientName")
    @NotEmpty(message = "recipient name is required")
    @Length(min = 1, max = 63 , message = "Recipient name length must be less that 63 char and not empty")
    private String recipientName;
    @NotEmpty(message = "recipient phone is required")
    @Length(max = 63 , message = "recipient phone must be less then 63")
    @Column(name = "recipientPhone")
    private String recipientPhone;
    @NotEmpty(message = "Country field is required")
    @Length(max = 64 , message = "country length must be less that 65 ")
    private String country;
    @NotEmpty(message = "city field is required")
    @Length(max = 64,message = "city max length is 64")
    private String city;
    @NotEmpty(message = "street field is required")
    @Length(max = 64,message = "street max length is 64")
    private String street;
    @NotEmpty(message = "building field is required")
    @Length(max = 64,message = "building max length is 64")
    private String building;
    @Column(name = "order_state")
    @Enumerated(EnumType.STRING)
    private OrderState orderState;


    private Long subTotal;
    @Column(name = "currency_code")
    @NotEmpty(message = "order currency code required")
    @Length(max = 10)
    private String currencyCode;
    @OneToMany(mappedBy = "order" ,cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @NotEmpty
    private List<OrderItem> orderItems;

    @OneToMany(mappedBy = "order",cascade = {CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REMOVE})
    private List<Payment> paymentList = new ArrayList<>();
    @Column(name = "cust_id")
    private Long customer_id;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    private String session_id;
    private Long expireAt;

    public void addItem(OrderItem orderItem){
        orderItems.add(orderItem);
    }
}
