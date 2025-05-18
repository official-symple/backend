package com.DreamOfDuck.account.service;

import com.DreamOfDuck.account.dto.request.LoginRequest;

import com.DreamOfDuck.account.dto.response.OAuthResponse;
import com.DreamOfDuck.account.dto.response.TokenResponse;
import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.entity.Role;
import com.DreamOfDuck.account.infra.KakaoApiClient;
import com.DreamOfDuck.account.jwt.JWTProvider;
import com.DreamOfDuck.account.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly=true)
public class AuthService {
    private final MemberRepository memberRepository;
    private final JWTProvider jwtProvider;
    private final KakaoApiClient kakaoApiClient;

    @Transactional
    public TokenResponse kakaoLogin(LoginRequest request){

        OAuthResponse response = kakaoApiClient.requestOAuthInfo(request.getAccessToken());
        Member findOne = findOrCreateMember(response);

        return jwtProvider.createJWT(findOne);
    }
    private Member findOrCreateMember(OAuthResponse response){
        return memberRepository.findByEmail(response.getEmail()).orElseGet(()->createMember(response));
    }
    private Member createMember(OAuthResponse response){

        Member member = Member.builder()
                .email(response.getEmail())
                .role(Role.ROLE_GUEST)
                .socialType(response.getSocialType())
                .build();

        memberRepository.save(member);
        return member;
    }
}
