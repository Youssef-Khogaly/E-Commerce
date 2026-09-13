package com.ecommerce.util;

import com.ecommerce.ApplicationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;


@Builder
public record Money(@Schema(description = "money in smallest unit", example = "100") long price,
                    Currency currency) implements Serializable {
    public Money(long price) {
        this(price, ApplicationConstants.defaultCurrency);
    }

    public Money(long price, String currencyCode) {
        this(price, Currency.getInstance(currencyCode));
    }

    private void checkCurrency(Money another) {
        if (!isSameCurrency(another))
            throw new IllegalArgumentException("Can't sum not equal money currency");
    }

    public boolean isSameCurrency(Money another) {
        return currency.getNumericCode() == another.currency().getNumericCode();
    }

    public Money add(Money another) {
        checkCurrency(another);
        return new Money(price + another.price, currency);
    }

    public Money sub(Money another) {
        checkCurrency(another);
        return new Money(price - another.price, currency);
    }

    public Money multi(long factor) {
        return new Money(price * factor, currency);
    }

    public BigDecimal toBigDecimal() {
        return BigDecimal.valueOf(price);
    }


}
