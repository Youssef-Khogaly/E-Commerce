package com.ecommerce.Category.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.util.Objects;

@Entity
@Table(name = "category")
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cate_id;
    @Length(max = 16)
    @NotEmpty(message = "category name can't be empty")
    private String name;



    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return getCate_id() == category.getCate_id();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCate_id());
    }
}
