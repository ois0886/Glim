package com.lovedbug.geulgwi.core.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Component
@Getter
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    private SecretKey hmacKey;

    private static final long ACCESS_TOKEN_EXPIRY = 1000L * 10;
    private static final long REFRESH_TOKEN_EXPIRY = 1000L * 60 * 60 * 24 * 30;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_AUTH  = "Authorization";

    @PostConstruct
    public void init() {
        this.hmacKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8),
            SignatureAlgorithm.HS256.getJcaName());
    }

    public String generateAccessToken(String email, Long memberId){

        return generateToken(email, memberId, ACCESS_TOKEN_EXPIRY, "access");
    }

    public String generateRefreshToken (String email, Long memberId){

        return generateToken(email, memberId, REFRESH_TOKEN_EXPIRY, "refresh");
    }

    private String generateToken(String email, Long memberId ,long expiry, String tokenType){

        Date now =  new Date();
        Date exp = new Date(now.getTime() + expiry);

        return Jwts.builder()
            .setSubject(email)
            .setIssuedAt(now)
            .setExpiration(exp)
            .claim("memberId", memberId)
            .claim("type", tokenType)
            .claim("jti", UUID.randomUUID().toString())
            .signWith(hmacKey)
            .compact();
    }

    public String extractEmail(String token) {

        return parseClaims(token).get("sub", String.class);
    }

    public boolean validateRefreshToken(String token){

        try{
            Claims claims = parseClaims(token);
            String tokenType = claims.get("type", String.class);

            return "refresh".equals(tokenType) && !claims.getExpiration().before(new Date());
        }catch (JwtException | IllegalArgumentException e){
            return false;
        }
    }

    public boolean validateAccessToken(String token){

        try{
            Claims claims = parseClaims(token);
            String tokenType = claims.get("type", String.class);

            return "access".equals(tokenType) && !claims.getExpiration().before(new Date());
        }catch ( JwtException | IllegalArgumentException e){
            return false;
        }
    }

    private Claims parseClaims(String token){

        return Jwts.parserBuilder()
                .setSigningKey(hmacKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
