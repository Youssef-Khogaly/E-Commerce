package com.ecommerce.User.UsersRepo;

import com.ecommerce.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepo extends JpaRepository<User,Long> {
}
