package com.ecommerce.Cart.services;

import com.ecommerce.Cart.Cart;
import com.ecommerce.Cart.Dto.CartDTO;
import com.ecommerce.Cart.mappers.CartItemsDtoMapper;
import com.ecommerce.Cart.repos.CartJpaRepo;
import com.ecommerce.Product.dtos.ProductDTO;
import com.ecommerce.Product.dtos.ProductSearchView;
import com.ecommerce.Exception.BadRequestException;
import com.ecommerce.Exception.NotFoundException;
import com.ecommerce.Product.services.crud.ProductCrudService;
import com.ecommerce.Product.services.query.ProductDtoQueryService;
import com.ecommerce.Product.services.search.ProductSearchService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartJpaRepo cartJpaRepo;
    private final ProductCrudService productCrudService;
    private final ProductSearchService productSearchService;
    private CartItemsDtoMapper cartItemsDtoMapper;
    private ProductDtoQueryService productDtoQueryService;
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

        if(!productCrudService.isProductExists(product_id))
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
        Set<Long> productIds = cart.getProductId_quantity_map().keySet();

        Map<Long, ProductDTO> productDTOMap = productDtoQueryService.findAllByIds(productIds,EnumSet.of(ProductDtoQueryService.ProductDtoFields.IMAGES));

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
        if(!productCrudService.isProductExists(product_id))
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
