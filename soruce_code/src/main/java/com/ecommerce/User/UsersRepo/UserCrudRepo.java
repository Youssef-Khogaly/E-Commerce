package com.ecommerce.User.UsersRepo;

import com.ecommerce.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

public interface UserCrudRepo extends JpaRepository<User, Long> {

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
