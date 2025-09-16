package br.com.andredevel.gateway.service.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    private Key key;

    @PostConstruct
    public void initKey() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public Claims parseToken(String token) {
        
        return io.jsonwebtoken.Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();
    }

    public Date getExpirationDate(String token) {
        return parseToken(token).getExpiration();
    }
    
    public boolean isTokenExpired(String token) {
        return getExpirationDate(token).before(new Date());
    }

    public String getIdUserFromToken(String authorization) {
        return parseToken(authorization).get("userId", String.class);   
    }
}
