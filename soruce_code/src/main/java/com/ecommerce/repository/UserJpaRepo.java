package com.ecommerce.repository;

import com.ecommerce.entities.user.User;
import org.springframework.data.repository.CrudRepository;

public interface UserJpaRepo extends CrudRepository<User,Long> {
}
