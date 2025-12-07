package com.ecommerce.entities.Carts;

import com.ecommerce.entities.user.Customer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cart")
@Getter
@Setter
public class Cart {


    @Id
    @Column(name = "cart_id")
    private long id;

    @OneToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "cart_id")
    @MapsId
    private Customer customer;

    @OneToMany(mappedBy = "cart",fetch = FetchType.EAGER , cascade = CascadeType.ALL)
    Set<CartItem>cartItemSet = new HashSet<>();
}
