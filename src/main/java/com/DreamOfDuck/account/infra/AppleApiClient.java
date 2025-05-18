package com.DreamOfDuck.account.infra;

import com.DreamOfDuck.account.dto.response.OAuthResponse;
import com.DreamOfDuck.account.entity.SocialType;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppleApiClient implements OAuthApiClient{
    private final FirebaseAuth firebaseAuth;
    @Override
    public OAuthResponse requestOAuthInfo(String accessToken) throws FirebaseAuthException {
        FirebaseToken decodedToken = firebaseAuth.verifyIdToken(accessToken);

        return OAuthResponse.builder()
                .socialType(SocialType.APPLE)
                .email(decodedToken.getUid()+ "@APPLE")
                .build();
    }
}
