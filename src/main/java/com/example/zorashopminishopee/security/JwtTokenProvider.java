package com.example.zorashopminishopee.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.access-expiration}")
    private Long accessExpiration;
    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    public String generateRefreshToken(UserDetails userDetails) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpiration );
        return Jwts.builder()
                .issuedAt(now)
                .signWith(getSecretKey())
                .expiration(expiryDate)
                .subject(userDetails.getUsername())
                .compact();
    }
    public String getEmailFromRefreshToken(String refreshToken) {
        return  Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseClaimsJws(refreshToken)
                .getPayload()
                .getSubject();
    }
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = getClaimsUser(userDetails);
        Date now = new Date();
        Date expiration = new Date(now.getTime() + accessExpiration);
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .signWith(getSecretKey())
                .issuedAt(now)
                .expiration(expiration)
                .compact();
    }
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        }
        catch (Exception e) {
            log.error("Invalid JWT: {}",e.getMessage());
            return false;
        }
    }
    Map<String, Object> getClaimsUser(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", userDetails.getUsername());
        claims.put("role", userDetails.getAuthorities().stream()
                .map(Object::toString)
                .collect(Collectors.joining(",")));
        return claims;
    }
    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getPayload()
                .getSubject()
                .toString();
    }
}
