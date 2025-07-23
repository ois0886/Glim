package com.lovedbug.geulgwi.dto.resposne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import java.time.Instant;

@Value
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JwtResponseDto {

    @JsonProperty("access_token")
    String accessToken;

    @JsonProperty("refresh_token")
    String refreshToken;

    @JsonProperty("token_type")
    @Builder.Default
    String tokenType = "Bearer";

    @JsonProperty("expires_in")
    Long expiresIn;

    @JsonProperty("access_token_expires")
    Instant accessTokenExpires;

    @JsonProperty("refresh_token_expires")
    Instant refreshTokenExpires;

    @JsonProperty("user_email")
    String userEmail;

    @JsonProperty("scope")
    String scope;

    @JsonIgnore
    public String getAuthorizationHeader(){
        return tokenType + " " + accessToken;
    }

    @JsonIgnore
    public boolean isExpired(){
        return accessTokenExpires != null && accessTokenExpires.isBefore(Instant.now());
    }
}
