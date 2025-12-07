package com.ecommerce.DTO;

import jakarta.validation.constraints.NotBlank;

public record AddressDto(@NotBlank(message = "country field is required in address") String country ,
                         @NotBlank(message = "city field is required in address") String city ,
                         @NotBlank(message = "street field is required in address") String street ,
                         @NotBlank(message = "buildingDetail field is required in address") String buildingDetail

) {
}
