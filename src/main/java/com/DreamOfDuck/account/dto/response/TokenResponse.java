package com.DreamOfDuck.account.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class TokenResponse {
    String accessToken;
    String refreshToken;
    String role;

    public static TokenResponse of(String aT, String rT, String role){
        return TokenResponse.builder()
                .accessToken(aT)
                .refreshToken(rT)
                .role(role)
                .build();
    }
}
