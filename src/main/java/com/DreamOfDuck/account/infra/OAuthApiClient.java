package com.DreamOfDuck.account.infra;

import com.DreamOfDuck.account.dto.response.OAuthResponse;

public interface OAuthApiClient {
    OAuthResponse requestOAuthInfo(String accessToken);
}
