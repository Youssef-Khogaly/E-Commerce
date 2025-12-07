package com.ecommerce.entities.user;

import jakarta.persistence.*;
import  jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.UniqueElements;

import java.math.BigInteger;
import java.util.Objects;

@Setter
@Getter
@Entity
@Table(name = "user" , schema = "E_Commerce")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "role")
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usr_id" )
    private Long id;
    @Column(name = "usr_name")
    @NotEmpty
    @Length(min = 5 , max = 15,
            message = "User name length must be between 5 and 15"
    )
    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9_]{5,15}",
            message = "Not allowed user name"
    )

    private String name;
    @Column(name = "usr_email")
    @NotEmpty
    @Email(message = "Invalid email address")
    private String email;
    @Column(name = "usr_pass" ,nullable = false)
    private String pass;

    @Column(name="isDeleted")
    private boolean isDeleted;
    private boolean isEmailVerified;


    public  abstract UserRoles getRole();
    public User() {
        isDeleted = false;
        isEmailVerified = false;
    }
    @Override
    public boolean equals(Object o) {
        if (o == null || !(this.getClass().equals(o.getClass()))) return false;
        User user = (User) o;
        return Objects.equals(getId(), user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
