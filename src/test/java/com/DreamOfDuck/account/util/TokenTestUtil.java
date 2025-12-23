package com.DreamOfDuck.account.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.DreamOfDuck.account.dto.response.TokenResponse;
import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.jwt.JWTProvider;
import com.DreamOfDuck.account.service.MemberService;

@Component
public class TokenTestUtil {
    private static final Logger log = LoggerFactory.getLogger(TokenTestUtil.class);
    private final JWTProvider jwtProvider;
    private final MemberService memberService;

    public TokenTestUtil(JWTProvider jwtProvider, MemberService memberService) {
        this.jwtProvider = jwtProvider;
        this.memberService = memberService;
    }

    /**
     * 이메일로 액세스 토큰을 받는 테스트용 메서드
     * @param email 사용자 이메일
     * @return 액세스 토큰
     */
    public String getAccessTokenByEmail(String email) {
        Member member = memberService.findMemberByEmail(email);
        TokenResponse tokenResponse = jwtProvider.createJWT(member);
        log.info("Generated access token for email: {}", email);
        return tokenResponse.getAccessToken();
    }

    /**
     * 이메일로 전체 토큰 응답을 받는 테스트용 메서드
     * @param email 사용자 이메일
     * @return TokenResponse (accessToken, refreshToken, role 포함)
     */
    public TokenResponse getTokenResponseByEmail(String email) {
        Member member = memberService.findMemberByEmail(email);
        TokenResponse tokenResponse = jwtProvider.createJWT(member);
        log.info("Generated tokens for email: {}", email);
        return tokenResponse;
    }


    public TokenResponse getTokenResponseById(Long id) {
        Member member = memberService.findMemberById(id);
        TokenResponse tokenResponse = jwtProvider.createJWT(member);
        log.info("Generated tokens for id: {}", id);
        return tokenResponse;
    }
}

