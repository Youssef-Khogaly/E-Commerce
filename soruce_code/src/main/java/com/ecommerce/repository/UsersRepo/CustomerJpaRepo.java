package com.ecommerce.repository.UsersRepo;

import com.ecommerce.entities.user.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerJpaRepo extends JpaRepository<Customer, Long> {


    boolean existsByNameOrEmail(String name, String email);
}
