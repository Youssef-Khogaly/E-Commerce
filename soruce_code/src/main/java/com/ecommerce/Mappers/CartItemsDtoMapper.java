package com.ecommerce.Mappers;

import com.ecommerce.DTO.CartDTO;
import com.ecommerce.DTO.CartItemDTO;
import com.ecommerce.DTO.ProductSearchView;
import com.ecommerce.entities.Carts.Cart;
import com.ecommerce.entities.Carts.CartItem;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@AllArgsConstructor
public class CartItemsDtoMapper {


    public List<CartItemDTO> from(Map<Long, ProductSearchView> productDTOMap , Set<CartItem> cartItems)
    {
        var cartDto = new CartItemDTO();

        return cartItems.stream().map(
                (i) -> {
                    var dto = new CartItemDTO();
                    var product = productDTOMap.get(i.getId().getProduct_id());
                    dto.setQuantity(i.getQuantity());
                    dto.setProductDTO(product);
                    dto.setSubTotalInCents((product.getPriceInCents().sub(product.getDiscount().getMoney())).multi(dto.getQuantity()));
                    return dto;
                }
        ).toList();
    }
}
