package com.weave.authservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key;
    private final String secretKey;
    private final long accessTokenValidTime;
    private final long refreshTokenValidTime;

    public JwtUtil(@Value("${jwt.secret.key}") String secretKey,
                   @Value("${jwt.access.token.valid.time}") long accessTokenValidTime,
                   @Value("${jwt.refresh.token.valid.time}") long refreshTokenValidTime) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.secretKey = secretKey;
        this.accessTokenValidTime = accessTokenValidTime;
        this.refreshTokenValidTime = refreshTokenValidTime;
    }

    public String generateAccessToken(Long userId, String email) {
        return generateToken(userId, email, accessTokenValidTime);
    }

    public String generateRefreshToken(Long userId, String email) {
        return generateToken(userId, email, refreshTokenValidTime);
    }

    private String generateToken(Long userId, String email, long expireTime) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("userId", userId);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

}
