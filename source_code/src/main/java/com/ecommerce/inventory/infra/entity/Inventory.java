package com.ecommerce.inventory.infra.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "inventory")
@Getter
@Setter
@SqlResultSetMapping(


        name = "inventoryResult" ,
        entities = {
                @EntityResult(entityClass = Inventory.class
                , fields = {
                        @FieldResult(name = "id",column = "id"),
                        @FieldResult(name = "totalQuantity",column = "totalQuantity")
                }
                )
        }


)
public class Inventory {

    @Id
    Long id;               /* product id*/
    Integer totalQuantity; /* total quantity unsold quantity regardless of reserved*/


}
