package com.ecommerce.Controllers;

import com.ecommerce.DTO.ErrorResponse;
import com.ecommerce.DTO.Requests.RegistrationRequest;
import com.ecommerce.entities.user.Customer;
import com.ecommerce.repository.UsersRepo.CustomerJpaRepo;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/auth/register")
@AllArgsConstructor
@Validated
public class registrationController {

    private final PasswordEncoder passwordEncoder;
    private final CustomerJpaRepo customerJpaRepo;
    @PostMapping
    public ResponseEntity<?> registerCustomer(@RequestBody @Valid RegistrationRequest req){

        if(customerJpaRepo.existsByNameOrEmail(req.name(),req.email()))
        {
            var error  = new ErrorResponse(HttpStatus.CONFLICT, List.of("Email or User name exists"),"/api/auth/register", Instant.now());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }
        Customer customer = new Customer();
        customer.setName(req.name());
        customer.setEmail(req.email());
        customer.setPass(passwordEncoder.encode(req.password()));
        customer = customerJpaRepo.save(customer);

        return ResponseEntity.ok().build();
    }
}
