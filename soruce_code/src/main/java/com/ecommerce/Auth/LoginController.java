package com.ecommerce.Auth;

import com.ecommerce.ApplicationConstants;
import com.ecommerce.util.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/login")
@Validated
@AllArgsConstructor
public class LoginController {

    private final AuthenticationService authService;
    public record LoginRequest( @NotBlank String email ,  @NotBlank String password){

    }


    public record LoginResponse(String status,String token){

    }
    @Operation(description = "Not authenticated required")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "200", headers = @Header(name = "Authorization", description = "JwtToken" ),content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE , schema = @Schema(implementation = LoginResponse.class))),
                    @ApiResponse(responseCode = "401",description = "bad credentials",content =@Content(schema = @Schema(implementation = ErrorResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE) ),
                    @ApiResponse(responseCode = "400",description = "constrain violation",content = @Content(schema = @Schema(implementation = ErrorResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "500",description = "internal error",content = @Content(schema = @Schema(implementation = ErrorResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE)),

            }
    )
    @PostMapping
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request){

        String token = authService.login(request.email(),request.password());

        return ResponseEntity.ok().header(ApplicationConstants.JWT_HEADER_NAME,token).body(new LoginResponse("Login success",token));
    }
}
