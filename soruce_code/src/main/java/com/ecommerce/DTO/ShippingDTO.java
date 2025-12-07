package com.ecommerce.DTO;

import com.ecommerce.entities.Shiping.ShipingMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;


@AllArgsConstructor
@NoArgsConstructor
@Getter@Setter
public class ShippingDTO {
    @NotBlank(message ="recipientName is required") @Length(max = 32)
    private String recipientName;
    private @NotBlank(message ="recipient Phone is required") String recipientPhone;
        private @NotNull(message ="shipping Address is required") AddressDto shippingAddress;
    private ShipingMethod method;
};
