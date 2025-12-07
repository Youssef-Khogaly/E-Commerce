package com.ecommerce.security;

import com.ecommerce.Exception.JwtExpiredTokenException;
import com.ecommerce.Exception.JwtInvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Slf4j
@Service
public class JwtService {


    private final SecretKey JWT_SEC_KEY;


    public JwtService(@Value("${JWT_SECRET}") String sec){
        assert (sec != null && sec.trim().length() < 256);
        try{
            this.JWT_SEC_KEY = Keys.hmacShaKeyFor(sec.getBytes(StandardCharsets.UTF_8));
        }catch (WeakKeyException e) {
            log.error("Weak Jwt seceret key!!");
            assert (false);
            throw new RuntimeException(e);
        }

    }

    public  String generateNewToken(Map<String,String> calims, Duration duration){

        return Jwts.builder().issuer("E-commernce").issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(duration)))
                .claims(calims)
                .signWith(JWT_SEC_KEY)
                .compact();
    }
    public Claims isValid(String token) {
        if(token == null || token.isBlank())
            throw new JwtInvalidTokenException("invalid token");

        try{
            return Jwts.parser().verifyWith(JWT_SEC_KEY).build().parseSignedClaims(token).getPayload();
        } catch (ExpiredJwtException e) {
            throw new JwtExpiredTokenException("TokenExpired");
        }
        catch (JwtException e) {
            throw new JwtInvalidTokenException("InvalidToken");
        }
    }
}
