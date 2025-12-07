package com.ecommerce.repository.UsersRepo;

import com.ecommerce.entities.user.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

public interface UserCrudRepo extends CrudRepository<User, BigInteger> {

    @Query("""
        SELECT CASE 
                 WHEN EXISTS (
                     SELECT 1 FROM User u 
                     WHERE u.email = :email OR u.name = :name
                 ) 
                 THEN TRUE 
                 ELSE FALSE 
               END
       """)
    public boolean isEmailOrNameExists( String email , String name);

    @Transactional(readOnly = true)
    User findByEmail(String email);
}
