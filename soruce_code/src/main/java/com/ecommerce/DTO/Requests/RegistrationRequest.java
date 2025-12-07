package com.ecommerce.DTO.Requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record RegistrationRequest(
                                    @Length(min = 5 , max = 15,
                                            message = "User name length must be between 5 and 15"
                                    )
                                    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9_]{5,15}",
                                            message = "Not allowed user name"
                                    )
                                    String name ,
                                  @Length(min = 8 , message = "Password length must be at least 8")
                                  @Pattern(
                                          regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).{8,}$",
                                          message = "Password must contain at least one uppercase, one lowercase, and one number "
                                  )
                                  String password , @Email(message = "Invalid Email address") String email) {
}
