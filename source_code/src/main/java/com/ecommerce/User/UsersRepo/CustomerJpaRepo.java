package com.ecommerce.User.UsersRepo;

import com.ecommerce.User.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerJpaRepo extends JpaRepository<Customer, Long> {


    boolean existsByNameOrEmail(String name, String email);
}
