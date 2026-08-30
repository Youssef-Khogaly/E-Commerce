package com.ecommerce.Cart.services;

import com.ecommerce.Cart.Dto.CartDTO;

public interface CartService {

    /* replace user cart
     * */
    public void addToCart(Long cust_id, Long product_id , Integer quantity);
    public CartDTO getCart(Long cust_id);
    public void putToCart(Long cust_id , Long product_id , Integer quantity);
    public void deleteFromCart(Long cust_id , Long product_id);


}
