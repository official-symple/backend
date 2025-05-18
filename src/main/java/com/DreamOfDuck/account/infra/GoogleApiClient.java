package com.DreamOfDuck.account.infra;

import com.DreamOfDuck.account.dto.response.OAuthResponse;
import com.DreamOfDuck.account.entity.SocialType;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
@Component
@RequiredArgsConstructor
public class GoogleApiClient implements OAuthApiClient{
    private final FirebaseAuth firebaseAuth;
    @Override
    public OAuthResponse requestOAuthInfo(String accessToken) throws FirebaseAuthException {
        FirebaseToken decodedToken = firebaseAuth.verifyIdToken(accessToken);

        return OAuthResponse.builder()
                .socialType(SocialType.GOOGLE)
                .email(decodedToken.getUid()+ "@GOOGLE")
                .build();
    }
}
