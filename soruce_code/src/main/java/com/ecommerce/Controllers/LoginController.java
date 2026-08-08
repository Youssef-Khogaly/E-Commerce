package com.ecommerce.Controllers;

import com.ecommerce.ApplicationConstants;
import com.ecommerce.DTO.ErrorResponse;
import com.ecommerce.security.JwtService;
import com.ecommerce.security.User.CustomUserDetails;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth/login")
@Validated
@AllArgsConstructor
public class LoginController {

    private final AuthenticationManager manager;
    private final JwtService jwtService;
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

        Authentication authenticaion = new UsernamePasswordAuthenticationToken(request.email(),request.password);
        authenticaion = manager.authenticate(authenticaion);
        if(authenticaion.isAuthenticated()){
            String authorities = authenticaion.getAuthorities().stream().map(s -> "ROLE_"+s.toString()).collect(Collectors.joining(","));
            String id = String.valueOf(((CustomUserDetails)authenticaion.getPrincipal()).getId());
            Map<String,String> claimsMap = new HashMap<>(2);
            claimsMap.put("id",id);
            claimsMap.put("authorities",authorities);
            String token = jwtService.generateNewToken(claimsMap, Duration.ofMinutes(30));
            return ResponseEntity.ok().header(ApplicationConstants.JWT_HEADER_NAME,token).body(new LoginResponse("Login success",token));
        }

        // we will never reach here
        return ResponseEntity.internalServerError().build();
    }
}
