package com.ecommerce.Mappers;

import com.ecommerce.DTO.CartItemDTO;
import com.ecommerce.DTO.ProductSearchView;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@AllArgsConstructor
public class CartItemsDtoMapper {


    public List<CartItemDTO> from(final Map<Long, ProductSearchView> productDTOMap , final Map<Long,Integer> idQuantity)
    {
        var cartDto = new CartItemDTO();

        return productDTOMap.keySet().stream().map(
                (i) -> {
                    var dto = new CartItemDTO();
                    var product = productDTOMap.get(i);
                    dto.setQuantity(idQuantity.get(i));
                    dto.setProductDTO(product);
                    dto.setSubTotalInCents((product.getPriceInCents().sub(product.getDiscount().getMoney())).multi(dto.getQuantity()));
                    return dto;
                }
        ).toList();
    }
}
