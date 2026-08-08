package com.ecommerce.DTO;

import com.ecommerce.ApplicationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Currency;

@Getter
@AllArgsConstructor
public class Money {
    @Schema(description = "money in smallest unit", example = "100")
    private final long price;
    private final Currency currency;

    public Money(long price){
        this.price = price;
        currency = ApplicationConstants.defaultCurrency;
    }
    public Money(long price,String currencyCode){
        this.price = price;
        currency = Currency.getInstance(currencyCode);
    }
    private void checkCurrency(Money another){
        if(!isSameCurrency(another))
            throw new IllegalArgumentException("Can't sum not equal money currency");
    }
    public boolean isSameCurrency(Money another){
        return currency.getNumericCode() == another.getCurrency().getNumericCode();
    }
    public Money add(Money another){
        checkCurrency(another);
        return new Money(price+another.price,currency);
    }
    public Money sub(Money another){
        checkCurrency(another);
        return  new Money(price-another.price,currency);
    }
    public Money multi(long factor){
        return  new Money(price*factor,currency);
    }

    public BigDecimal toBigDecimal(){
        return BigDecimal.valueOf(price);
    }


}
