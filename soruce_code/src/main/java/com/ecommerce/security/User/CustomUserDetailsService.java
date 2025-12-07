package com.ecommerce.security.User;

import com.ecommerce.entities.user.User;
import com.ecommerce.repository.UsersRepo.UserCrudRepo;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;




@AllArgsConstructor
@Component
public class CustomUserDetailsService implements UserDetailsService {
    private final UserCrudRepo userCrudRepo;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userCrudRepo.findByEmail(email);
        if(user == null)
                throw new UsernameNotFoundException("invalid credentials");

        return toCustomUserDetails(user);
    }
    UserDetails toCustomUserDetails(User user){
        var ret = new CustomUserDetails(user.getId(),user.getEmail(),user.getPass(),
                List.of(new SimpleGrantedAuthority(user.getRole().toString())),
                !user.isDeleted());

        return ret;
    }
}
