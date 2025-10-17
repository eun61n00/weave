package com.weave.authservice.util;

import com.weave.authservice.exception.WeaveAuthException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

import static com.weave.authservice.exception.ErrorMap.*;

@Slf4j
@Component
public class JwtProvider {

    private Key key;

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Getter
    @Value("${jwt.access.token.valid.time}")
    private long accessTokenValidTime;

    @Getter
    @Value("${jwt.refresh.token.valid.time}")
    private long refreshTokenValidTime;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
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

    public void validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
            throw new WeaveAuthException(TOKEN_INVALID_ERROR);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
            throw new WeaveAuthException(TOKEN_EXPIRED_ERROR);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
            throw new WeaveAuthException(TOKEN_INVALID_ERROR);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
            throw new WeaveAuthException(TOKEN_INVALID_ERROR);
        }
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public Long getUserIdFromToken(String token) {
        return parseClaims(token).get("userId", Long.class);
    }

    public Long getExpiration(String token) {
        Date expiration = parseClaims(token).getExpiration();
        long now = new Date().getTime();
        return (expiration.getTime() - now);
    }
}
