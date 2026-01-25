package com.ecommerce.DTO;

import com.ecommerce.ApplicationConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Currency;


@AllArgsConstructor
@NoArgsConstructor
@Getter@Setter
public class DiscountDTO {
    private Money money;


    public DiscountDTO(long price){
        this.money = new Money(price);
    }
}
