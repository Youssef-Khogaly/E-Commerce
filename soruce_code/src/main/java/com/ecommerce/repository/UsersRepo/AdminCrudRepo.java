package com.ecommerce.repository.UsersRepo;

import com.ecommerce.entities.user.Admin;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;

public interface AdminCrudRepo extends CrudRepository<Admin, Long> {
}
