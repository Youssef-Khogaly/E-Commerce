package com.ecommerce.Controllers;

import com.ecommerce.DTO.ErrorResponse;
import com.ecommerce.DTO.Requests.RegistrationRequest;
import com.ecommerce.Exception.ConflictException;
import com.ecommerce.docs.CommonErrorDocs;
import com.ecommerce.entities.user.Customer;
import com.ecommerce.repository.UsersRepo.CustomerJpaRepo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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


    @Operation(description = "Not authenticated required")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200",description = "registered successfully"),
                    @ApiResponse(responseCode = "409",description = "User name or email exists",content = @Content(schema = @Schema(implementation = ErrorResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "400",description = "constrain violation",content = @Content(schema = @Schema(implementation = ErrorResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE))
            }
    )
    @PostMapping
    public ResponseEntity<Void> registerCustomer(@RequestBody @Valid RegistrationRequest req){

        if(customerJpaRepo.existsByNameOrEmail(req.name(),req.email()))
        {
            throw new ConflictException("Email or User name exists");
        }
        Customer customer = new Customer();
        customer.setName(req.name());
        customer.setEmail(req.email());
        customer.setPass(passwordEncoder.encode(req.password()));
        customer = customerJpaRepo.save(customer);

        return ResponseEntity.ok().build();
    }
}
