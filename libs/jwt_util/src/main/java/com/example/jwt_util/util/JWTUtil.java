package com.example.jwt_util.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {

    private final SecretKey secretKey;

    // application.yml → spring.jwt.secret: 32자 이상 랜덤 문자열
    public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    private Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /** username 클레임 꺼내기 */
    public String getUsername(String token) {
        return parse(token).get("username", String.class);
    }

    /** userId 예시 (Long) */
    public Long getUserId(String token) {
        return parse(token).get("userId", Long.class);
    }

    /** role 예시 */
    public String getRole(String token) {
        return parse(token).get("role", String.class);
    }

    public String getType(String token) {
        return parse(token).get("type", String.class);
    }

    public Boolean isExpired(String token) {
        try {
            return parse(token).getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }
}
