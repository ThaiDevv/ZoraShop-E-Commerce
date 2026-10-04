package com.example.zorashopminishopee.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {
    @Value("${jwt.access.secret}")
    private String accessSecret;
    @Value("${jwt.refresh.secret}")
    private String refreshSecret;
    private static final String REFRESH_TOKEN = "RefreshToken";
    private static final String ACCESS_TOKEN = "AccessToken";
    private SecretKey getAccessSecretKey() {
        return Keys.hmacShaKeyFor(accessSecret.getBytes());
    }
    private SecretKey getRefreshSecretKey() {
        return Keys.hmacShaKeyFor(refreshSecret.getBytes());
    }
    //parse
    private Claims parse(String token, SecretKey key){
        return  Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    public Claims parseAccessToken(String token) {
        Claims claims = parse(token, getAccessSecretKey());
        if(!ACCESS_TOKEN.equals(claims.get("type", String.class))){
            throw new JwtException("Don't use Refresh token to parse Access token");
        }
        return claims;
    }
    public Claims parseRefreshToken(String token) {
            Claims claims = parse(token, getRefreshSecretKey());
            if(!REFRESH_TOKEN.equals(claims.get("type", String.class))){
                throw new JwtException("Don't use Access token to parse Refresh token");
            }
            return claims;


    }
    //generate
    public String generateRefreshToken(UserDetails userDetails, String jti, Date expirationAt) {
        Map<String, Object> claims = getClaimsUser(userDetails, REFRESH_TOKEN);
        Date now = new Date();
        return Jwts.builder()
                .id(jti)
                .issuedAt(now)
                .signWith(getRefreshSecretKey())
                .expiration(expirationAt)
                .claims(claims)
                .subject(userDetails.getUsername())
                .compact();
    }
    public String generateAccessToken(UserDetails userDetails, String jti, Date expirationAt) {
        Map<String, Object> claims = getClaimsUser(userDetails, ACCESS_TOKEN);
        Date now = new Date();
        return Jwts.builder()
                .id(jti)
                .claims(claims)
                .subject(userDetails.getUsername())
                .signWith(getAccessSecretKey())
                .issuedAt(now)
                .expiration(expirationAt)
                .compact();
    }
    Map<String, Object> getClaimsUser(UserDetails userDetails, String type) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userDetails.getAuthorities().stream()
                .map(Object::toString)
                .collect(Collectors.joining(",")));
        claims.put("type", type);
        return claims;
    }


}
