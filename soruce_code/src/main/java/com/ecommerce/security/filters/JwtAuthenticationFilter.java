package com.ecommerce.security.filters;

import com.ecommerce.ApplicationConstants;
import com.ecommerce.DTO.ErrorResponse;
import com.ecommerce.Exception.JwtExpiredTokenException;
import com.ecommerce.Exception.JwtInvalidTokenException;
import com.ecommerce.security.JwtService;
import com.ecommerce.security.User.CustomUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final List<PathPatternRequestMatcher> skipValidationMatchersList;
    private final JwtService jwtService;

    private void createSuccessfullAuthenticaionObject(long id , String authorities){

        Set<GrantedAuthority> authoritiesSet = new HashSet<>(AuthorityUtils.commaSeparatedStringToAuthorityList(authorities));
        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(new CustomUserDetails(id,authoritiesSet),null,authoritiesSet);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = request.getHeader(ApplicationConstants.JWT_HEADER_NAME);

        Claims claims;
        try{
            claims = jwtService.isValid(token);
        }catch (JwtInvalidTokenException | JwtExpiredTokenException e ){
            throw new BadCredentialsException(e.getMessage());
        }

        Long id = Long.parseLong(claims.get("id",String.class));

        String authorities = claims.get("authorities",String.class);
        createSuccessfullAuthenticaionObject(id,authorities);


        filterChain.doFilter(request,response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        for(PathPatternRequestMatcher matcher : skipValidationMatchersList){
            if(matcher.matches(request))
                return true;
        }

        return false;
    }

}
