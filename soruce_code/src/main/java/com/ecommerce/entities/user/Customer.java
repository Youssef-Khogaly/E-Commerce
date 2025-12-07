package com.ecommerce.entities.user;


import com.ecommerce.entities.Carts.CartItem;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Entity
@Table(name = "customer")
@DiscriminatorValue(value = "customer")
@PrimaryKeyJoinColumn(name = "cust_id" , referencedColumnName = "usr_id")
@Setter
@Getter
public class Customer extends User {


    @Override
    public UserRoles getRole() {
        return UserRoles.CUSTOMER;
    }
}
