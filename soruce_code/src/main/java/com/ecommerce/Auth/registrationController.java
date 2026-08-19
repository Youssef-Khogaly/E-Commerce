package com.ecommerce.Auth;

import com.ecommerce.util.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/register")
@AllArgsConstructor
@Validated
public class registrationController {

    private final AuthenticationService authenticationService;

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

        authenticationService.signup(req.name(), req.email(), req.password());

        return ResponseEntity.ok().build();
    }
}
