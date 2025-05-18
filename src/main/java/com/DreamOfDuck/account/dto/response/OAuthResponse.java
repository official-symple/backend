package com.DreamOfDuck.account.dto.response;

import com.DreamOfDuck.account.entity.SocialType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OAuthResponse {
    String email;
    SocialType socialType;
    String refreshToken;
}
