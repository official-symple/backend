package com.DreamOfDuck.account.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.DreamOfDuck.account.dto.response.TokenResponse;

@SpringBootTest
class TokenTestUtilTest {

    @Autowired
    private TokenTestUtil tokenTestUtil;

    @Test
    void getAccessTokenByEmail() {
        // 여기에 테스트할 이메일을 입력하세요
        String email = "test@example.com";
        
        try {
            String accessToken = tokenTestUtil.getAccessTokenByEmail(email);
            System.out.println("Access Token: " + accessToken);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    @Test
    void getTokenResponseById() {
        // 여기에 테스트할 이메일을 입력하세요
         Long id = 1L;
        
        try {
            TokenResponse tokenResponse = tokenTestUtil.getTokenResponseById(id);
            System.out.println("Access Token: " + tokenResponse.getAccessToken());
            System.out.println("Refresh Token: " + tokenResponse.getRefreshToken());
            System.out.println("Role: " + tokenResponse.getRole());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}

