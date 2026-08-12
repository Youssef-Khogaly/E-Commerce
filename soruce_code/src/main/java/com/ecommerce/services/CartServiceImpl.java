package com.ecommerce.services;

import com.ecommerce.DTO.CartDTO;
import com.ecommerce.DTO.ProductSearchView;
import com.ecommerce.Exception.BadRequestException;
import com.ecommerce.Mappers.CartItemsDtoMapper;
import com.ecommerce.entities.Carts.Cart;
import com.ecommerce.repository.Cart.CartJpaRepo;
import com.ecommerce.Exception.NotFoundException;
import com.ecommerce.services.interfaces.CartService;
import com.ecommerce.services.interfaces.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartJpaRepo cartJpaRepo;
    private final ProductService productService;
    private CartItemsDtoMapper cartItemsDtoMapper;

    public Cart findById(Long custId)
    {
        return cartJpaRepo.findById(custId).orElseGet(() -> {
            var emptyCart = new Cart();
            emptyCart.setId(custId);
            return emptyCart;
        });
    }
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void addToCart(Long cust_id, Long product_id, Integer quantity) {

        if(!productService.isProductExists(product_id))
            throw new BadRequestException("product id doesnot exist + id:" + product_id);

        Cart cart = findById(cust_id);

        cart.addItem(product_id,quantity);
        cartJpaRepo.save(cart);
    }
    @Override
    @Transactional(readOnly = true , isolation = Isolation.READ_COMMITTED)
    public CartDTO getCart(Long cust_id) {

        Cart cart =  cartJpaRepo.findById(cust_id).orElseThrow(() -> new NotFoundException("Cart id doesn't exists:" + cust_id));
        CartDTO cartDTO = new CartDTO();
        cartDTO.setCartId(cust_id);
        Collection<Long> productIds = cart.getProductId_quantity_map().keySet();

        Map<Long, ProductSearchView> productDTOMap = productService.getProductSearchView(productIds);

        cartDTO.setItems(cartItemsDtoMapper.from(productDTOMap,cart.getProductId_quantity_map()));
        return cartDTO;
    }
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void putToCart(Long cust_id, Long product_id, Integer quantity) {
        if(quantity == 0){
            deleteFromCart(cust_id,product_id);
            return;
        }
        if(!productService.isProductExists(product_id))
            throw new BadRequestException("product id does not exist + id:" + product_id);

        Cart cart = findById(cust_id);
        var map = cart.getProductId_quantity_map();
        if(map.containsKey(product_id) && map.get(product_id).equals(quantity))
            return;

        if(quantity > 0)
            cart.getProductId_quantity_map().put(product_id,quantity);
        else
            cart.getProductId_quantity_map().remove(product_id);

        cartJpaRepo.save(cart);
    }

    @Override
    public void deleteFromCart(Long cust_id, Long product_id) {
        Cart cart = findById(cust_id);

        var map = cart.getProductId_quantity_map();
        if(!map.containsKey(product_id))
            return;
        map.remove(product_id);
        cartJpaRepo.save(cart);
    }

}
