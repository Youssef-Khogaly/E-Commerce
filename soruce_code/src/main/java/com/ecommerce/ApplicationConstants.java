package com.ecommerce;

import org.springframework.beans.factory.annotation.Value;

import java.util.Currency;

public class ApplicationConstants {
    public final static String JWT_HEADER_NAME = "Authorization";

    public final static Currency defaultCurrency = Currency.getInstance("EGP");
}
