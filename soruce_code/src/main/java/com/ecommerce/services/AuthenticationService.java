package com.ecommerce.services;

import com.ecommerce.ApplicationConstants;
import com.ecommerce.Controllers.LoginController;
import com.ecommerce.Exception.ConflictException;
import com.ecommerce.entities.user.Customer;
import com.ecommerce.repository.UsersRepo.CustomerJpaRepo;
import com.ecommerce.security.JwtService;
import com.ecommerce.security.User.CustomUserDetails;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager manager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final CustomerJpaRepo customerJpaRepo;
    public String login(final String email , final String pass)
    {
        Authentication authenticaion = new UsernamePasswordAuthenticationToken(email,pass);
        authenticaion = manager.authenticate(authenticaion);
        if(authenticaion.isAuthenticated()) {
            String authorities = authenticaion.getAuthorities().stream().map(s -> "ROLE_" + s.toString()).collect(Collectors.joining(","));
            String id = String.valueOf(((CustomUserDetails) authenticaion.getPrincipal()).getId());
            Map<String, String> claimsMap = new HashMap<>(2);
            claimsMap.put("id", id);
            claimsMap.put("authorities", authorities);

            return jwtService.generateNewToken(claimsMap, Duration.ofMinutes(30));
        }

        // should never reach here
        return  null;
    }

    public void signup(String name, String email , String password)
    {
        if(customerJpaRepo.existsByNameOrEmail(name,email))
        {
            throw new ConflictException("Email or User name exists");
        }
        Customer customer = new Customer();
        customer.setName(name);
        customer.setEmail(email);
        customer.setPass(passwordEncoder.encode(password));
        customer = customerJpaRepo.save(customer);

        return;
    }

    static public CustomUserDetails getCurrentAuthUser()
    {

        var auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.getPrincipal() instanceof CustomUserDetails c)
            return c;

        throw new ClassCastException("cannot cast auth principle to custom user details");
    }
}
