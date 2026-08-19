package com.ecommerce.Product.dtos;

import com.ecommerce.util.Money;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter@Setter
public class DiscountDTO {
    @Schema(description = "net discount")
    private Money money;


    public DiscountDTO(long price){
        this.money = new Money(price);
    }
}
