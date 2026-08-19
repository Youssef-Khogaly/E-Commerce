package com.ecommerce.docs;


import com.ecommerce.util.ErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.MediaType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@SecurityRequirement(name = "JwtAuth")
@ApiResponses(
        {

                @ApiResponse(responseCode = "403",description = "forbidden, require specific role",content = @Content(schema = @Schema(implementation = ErrorResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE)),
                @ApiResponse(responseCode = "401",description = "not authorized , authentication required",content =@Content(schema = @Schema(implementation = ErrorResponse.class),mediaType = MediaType.APPLICATION_JSON_VALUE) ),
        }

)
public @interface RequireAuthDocs {
}
