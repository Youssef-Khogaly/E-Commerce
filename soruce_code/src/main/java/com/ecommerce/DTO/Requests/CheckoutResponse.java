package com.ecommerce.DTO.Requests;

import java.time.Instant;

public record CheckoutResponse(String sessionUrl , String order_id , Instant expireAt){

}