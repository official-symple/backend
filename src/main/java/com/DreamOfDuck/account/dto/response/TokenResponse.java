package com.DreamOfDuck.account.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Schema(description = "login response")
public class TokenResponse {
    @Schema(example="afdasfsdafdsa")
    String accessToken;
    @Schema(example="asdfasdfasdfijojkl")
    String refreshToken;
    @Schema(example="ROLE_GUEST")
    String role;

    public static TokenResponse of(String aT, String rT, String role){
        return TokenResponse.builder()
                .accessToken(aT)
                .refreshToken(rT)
                .role(role)
                .build();
    }
}
