package com.ecommerce.checkout;

import java.time.Instant;

public record CheckoutResponse(String sessionUrl , String order_id , Instant expireAt){

}