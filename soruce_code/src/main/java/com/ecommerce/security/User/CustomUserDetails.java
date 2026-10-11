package com.ecommerce.security.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    @Getter
    private final long id;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority>authorities;
    private final boolean isEnabled;

    public CustomUserDetails(long id, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.authorities = authorities;
        this.email = null;
        this.isEnabled = true;
        this.password = null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isEnabled() {
        return  isEnabled;
    }
}
