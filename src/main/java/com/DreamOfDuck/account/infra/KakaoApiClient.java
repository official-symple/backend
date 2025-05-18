package com.DreamOfDuck.account.infra;

import com.DreamOfDuck.account.dto.response.OAuthResponse;
import com.DreamOfDuck.account.entity.SocialType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class KakaoApiClient implements OAuthApiClient {

    private final RestTemplate restTemplate;
    @Override
    public OAuthResponse requestOAuthInfo(String accessToken) {
        String url = "https://kapi.kakao.com/v2/user/me";
        HttpMethod httpMethod = HttpMethod.GET;

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.set("Authorization", "Bearer "+accessToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(httpHeaders);
        ParameterizedTypeReference<Map<String, Object>> RESPONSE_TYPE = new ParameterizedTypeReference<Map<String, Object>>() {};
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(url, httpMethod, request, RESPONSE_TYPE);

        return OAuthResponse.builder()
                .socialType(SocialType.KAKAO)
                .email(response.getBody().get("id") + "@KAKAO")
                .build();
    }


}
