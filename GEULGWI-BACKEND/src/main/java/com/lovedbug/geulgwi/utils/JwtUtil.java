package com.lovedbug.geulgwi.utils;

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

    private final long accessTokenExpiry = 1000L * 60 * 60;
    private final long refreshTokenExpiry = 1000L * 60 * 60 * 24 * 30;
    private final long emailVerificationTokenExpiry = 1000L * 60 * 10;


    @PostConstruct
    public void init() {
        this.hmacKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8),
            SignatureAlgorithm.HS256.getJcaName());
    }

    public String generateAccessToken(String email){

        return generateToken(email, accessTokenExpiry, "access");
    }

    public String generateRefreshToken (String email){

        return generateToken(email, refreshTokenExpiry, "refresh");
    }

    private String generateToken(String email, long expiry, String tokenType){

        Date now =  new Date();
        Date exp = new Date(now.getTime() + expiry);

        return Jwts.builder()
            .setSubject(email)
            .setIssuedAt(now)
            .setExpiration(exp)
            .claim("type", tokenType)
            .claim("jti", UUID.randomUUID().toString())
            .signWith(hmacKey)
            .compact();
    }

    public String extractEmail(String token) {

        return parseClaims(token).get("sub", String.class);
    }

    public Date extractExpiration(String token){
        return parseClaims(token).getExpiration();
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
