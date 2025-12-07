package com.ecommerce.entities.images;


import com.ecommerce.entities.Products.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "image")
@Getter@Setter
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id",unique = true,nullable = false )
    private Long id;
    @Column(name = "image_url" , nullable = false)
    private String image_url;

    @ManyToOne
    @JoinColumn(name = "product_id" , nullable = false)
    private Product product;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Image image = (Image) o;
        return Objects.equals(getId(), image.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
