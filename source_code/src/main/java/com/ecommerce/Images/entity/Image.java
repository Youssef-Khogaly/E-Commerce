package com.ecommerce.Images.entity;


import com.ecommerce.Images.StorageProvider;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "image")
@Getter@Setter
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;
    @Column(unique = true,nullable = false)
    private String storage_key;
    @Enumerated(EnumType.STRING)
    private StorageProvider storageProvider;
    @Column(nullable = true)
    private String region;
    @Column(name = "image_url" , nullable = false)
    private String imageUrl;


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
