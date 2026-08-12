package com.ecommerce.entities.user;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
