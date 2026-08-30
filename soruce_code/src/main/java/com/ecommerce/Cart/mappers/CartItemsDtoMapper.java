package com.ecommerce.Cart.mappers;

import com.ecommerce.Cart.Dto.CartItemDTO;
import com.ecommerce.Product.dtos.ProductDTO;
import com.ecommerce.Product.dtos.ProductSearchView;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class CartItemsDtoMapper {


    public List<CartItemDTO> from(final Map<Long, ProductDTO> productDTOMap , final Map<Long,Integer> idQuantity)
    {
        var cartDto = new CartItemDTO();

        return productDTOMap.keySet().stream().map(
                (i) -> {
                    var dto = new CartItemDTO();
                    var product = productDTOMap.get(i);
                    dto.setQuantity(idQuantity.get(i));
                    dto.setProductDTO(product);
                    dto.setSubTotalInCents((product.getPrice().sub(product.getDiscount().getMoney())).multi(dto.getQuantity()));
                    return dto;
                }
        ).toList();
    }
}
