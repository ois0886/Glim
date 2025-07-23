package com.lovedbug.geulgwi.utils;

import com.lovedbug.geulgwi.dto.request.SignUpRequestDto;
import com.lovedbug.geulgwi.enums.MemberGender;
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
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Component
@Getter
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    private SecretKey hmacKey;

    private final long accessTokenExpiry = 1000 * 60 * 15;
    private final long refreshTokenExpiry = 1000 * 60 * 60;
    private final long emailVerificationTokenExpiry = 1000 * 60 * 10;


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

    public String generateEmailVerificationToken(SignUpRequestDto signUpRequestDto){

        Date now = new Date();
        Date expiry = new Date(now.getTime() + emailVerificationTokenExpiry);

        return Jwts.builder()
            .claim("sub", signUpRequestDto.getEmail())
            .claim("iat", now)
            .claim("exp", expiry)
            .claim("type", "email_verification")
            .claim("jti", UUID.randomUUID().toString())
            .claim("email", signUpRequestDto.getEmail())
            .claim("password", signUpRequestDto.getPassword())
            .claim("nickname", signUpRequestDto.getNickname())
            .claim("gender",signUpRequestDto.getGender().toString())
            .claim("birthDate", signUpRequestDto.getBirthDate().toString())
            .signWith(hmacKey)
            .compact();
    }

    public SignUpRequestDto extractSignUpInfo(String token){

        Claims claims = parseClaims(token);

        return SignUpRequestDto.builder()
            .email(claims.get("email", String.class))
            .password(claims.get("password", String.class))
            .nickname(claims.get("nickname", String.class))
            .gender(MemberGender.valueOf(claims.get("gender", String.class)))
            .birthDate(LocalDate.parse(claims.get("birthDate", String.class)))
            .build();
    }

    private String generateToken(String email, long expiry, String tokenType){

        Date now =  new Date();
        Date exp = new Date(now.getTime() + expiry);

        return Jwts.builder()
            .claim("sub", email)
            .claim("iat", now)
            .claim("exp", exp)
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

    public boolean validateToken(String token){

        try{
            return !(parseClaims(token).getExpiration()
                .before(new Date()));
        }catch (JwtException | IllegalArgumentException e){
            return false;
        }
    }

    public boolean validateEmailVerificationToken(String token){

        try{
            Claims claims = parseClaims(token);
            String tokenType = claims.get("type", String.class);

            return "email_verification".equals(tokenType) && !claims.getExpiration().before(new Date());
        }catch (JwtException | IllegalArgumentException e){
            return false;
        }
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
