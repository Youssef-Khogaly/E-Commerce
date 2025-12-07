package com.ecommerce.services.interfaces;

import com.ecommerce.DTO.CartDTO;

public interface CartService {

    /* replace user cart
     * */
    public void addToCart(Long cust_id, Long product_id , Integer quantity);
    public CartDTO getCart(Long cust_id);
    public void putToCart(Long cust_id , Long product_id , Integer quantity);
    public void deleteFromCart(Long cust_id , Long product_id);

}
