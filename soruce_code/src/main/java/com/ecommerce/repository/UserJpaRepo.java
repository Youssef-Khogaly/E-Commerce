package com.ecommerce.repository;

import com.ecommerce.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface UserJpaRepo extends JpaRepository<User,Long> {
}
