package com.ecommerce.services;

import com.ecommerce.DTO.CartDTO;
import com.ecommerce.DTO.CartItemDTO;
import com.ecommerce.DTO.ProductDTO;
import com.ecommerce.Exception.BadRequestException;
import com.ecommerce.entities.Carts.Cart;
import com.ecommerce.entities.Carts.CartItem;
import com.ecommerce.entities.Carts.CartItemId;
import com.ecommerce.repository.Cart.CartItemRepo;
import com.ecommerce.repository.CartJpaRepo;
import com.ecommerce.Exception.NotFoundException;
import com.ecommerce.repository.Product.ProductJpaRepo;
import com.ecommerce.services.interfaces.CartService;
import com.ecommerce.services.interfaces.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService {
    private CartJpaRepo cartJpaRepo;
    private  CartItemRepo cartItemRepo;
    private ProductService productService;
    private ProductJpaRepo productJpaRepo;

    @Override
    @Transactional
    public void addToCart(Long cust_id, Long product_id, Integer quantity) {

        if(productService.isProductExists(product_id))
            throw new BadRequestException("product id doesnot exist + id:" + product_id);
        Optional<CartItem> cartItemOptional = cartItemRepo.findById(new CartItemId(cust_id,product_id));
        if(cartItemOptional.isPresent()){
            var cartItem = cartItemOptional.get();
            cartItem.setQuantity(cartItem.getQuantity()+quantity);
        }
        else
        {
            var cartItem = new CartItem();
            cartItem.setCart(cartJpaRepo.getReferenceById(cust_id));
            cartItem.setProduct(productJpaRepo.getReferenceById(product_id));
            cartItem.setQuantity(quantity);
            cartItemRepo.save(cartItem);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CartDTO getCart(Long cust_id) {

        Cart cart =  cartJpaRepo.findById(cust_id).orElseThrow(() -> new NotFoundException("Cart id doesn't exists:" + cust_id));
        CartDTO cartDTO = new CartDTO();
        cartDTO.setCartId(cust_id);
        List<Long> productIds = cart.getCartItemSet().stream().map(i -> i.getId().getProduct_id()).toList();

        Map<Long, ProductDTO> productDTOMap = productService.getProducts(productIds);

        cartDTO.setItems(toCartItemsDTO(cart.getCartItemSet(),productDTOMap));
        return cartDTO;
    }

    @Override
    @Transactional
    public void putToCart(Long cust_id, Long product_id, Integer quantity) {
        if(quantity == 0){
            deleteFromCart(cust_id,product_id);
            return;
        }
        if(productService.isProductExists(product_id))
            throw new BadRequestException("product id doesnot exist + id:" + product_id);

        Optional<CartItem> cartItemOptional = cartItemRepo.findById(new CartItemId(cust_id,product_id));
        if(cartItemOptional.isPresent()){
            cartItemOptional.get().setQuantity(quantity);
        }else {
            var cartItem = new CartItem();
            cartItem.setCart(cartJpaRepo.getReferenceById(cust_id));
            cartItem.setProduct(productJpaRepo.getReferenceById(product_id));
            cartItem.setQuantity(quantity);
            cartItemRepo.save(cartItem);
        }
    }

    @Override
    public void deleteFromCart(Long cust_id, Long product_id) {
        cartItemRepo.deleteById(new CartItemId(cust_id,product_id));
    }

    private List<CartItemDTO> toCartItemsDTO(Collection<CartItem> cartItems, Map<Long, ProductDTO> longProductDTOMap){
        return cartItems.stream().map(
                (i) -> {
                  var dto = new CartItemDTO();
                  var product = longProductDTOMap.get(i.getId().getProduct_id());
                  dto.setQuantity(i.getQuantity());
                  dto.setProductDTO(product);
                  dto.setSubTotalInCents((product.getPriceInCents()-product.getDiscountInCents()) * dto.getQuantity());
                  return dto;
                }
        ).toList();
    }
}
