package com.DreamOfDuck.account.infra;

import com.DreamOfDuck.account.dto.response.OAuthResponse;
import com.google.firebase.auth.FirebaseAuthException;

public interface OAuthApiClient {
    OAuthResponse requestOAuthInfo(String accessToken) throws FirebaseAuthException;
}
