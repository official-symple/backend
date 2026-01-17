package com.DreamOfDuck.account.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.DreamOfDuck.account.dto.response.OAuthResponse;
import com.DreamOfDuck.account.dto.response.TokenResponse;
import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.entity.Role;
import com.DreamOfDuck.account.infra.AppleApiClient;
import com.DreamOfDuck.account.infra.GoogleApiClient;
import com.DreamOfDuck.account.infra.KakaoApiClient;
import com.DreamOfDuck.account.jwt.JWTProvider;
import com.DreamOfDuck.account.repository.MemberRepository;
import com.google.firebase.auth.FirebaseAuthException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly=true)
public class AuthService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTProvider jwtProvider;
    private final KakaoApiClient kakaoApiClient;
    private final GoogleApiClient googleApiClient;
    private final AppleApiClient appleApiClient;

    @Transactional
    public TokenResponse kakaoLogin(String accessToken){

        OAuthResponse response = kakaoApiClient.requestOAuthInfo(accessToken);
        Member findOne = findOrCreateMember(response);
        return jwtProvider.createJWT(findOne);
    }

    @Transactional
    public TokenResponse googleLogin(String accessToken) throws FirebaseAuthException {
        OAuthResponse response = googleApiClient.requestOAuthInfo(accessToken);
        Member findOne = findOrCreateMember(response);
        return jwtProvider.createJWT(findOne);
    }

    @Transactional
    public TokenResponse appleLogin(String accessToken) throws FirebaseAuthException {
        OAuthResponse response = appleApiClient.requestOAuthInfo(accessToken);
        Member findOne = findOrCreateMember(response);
        return jwtProvider.createJWT(findOne);
    }
    private Member findOrCreateMember(OAuthResponse response){
        return memberRepository.findByEmail(response.getEmail()).orElseGet(()->createMember(response));
    }
    private Member createMember(OAuthResponse response){

        Member member = Member.builder()
                .email(response.getEmail())
                .socialEmail(response.getSocialEmail())
                .role(Role.ROLE_GUEST)
                .socialType(response.getSocialType())
                .build();

        memberRepository.save(member);
        return member;
    }

    @Transactional
    public TokenResponse reissue(String refreshToken){
        return jwtProvider.reissue(refreshToken);
    }

    @Transactional
    public void logout(String email){
        jwtProvider.deleteToken(email);

    }

    @Transactional
    public void cancelMembership(Member member){
        jwtProvider.deleteToken(member.getEmail());
        member.setRole(Role.ROLE_DELETED);
        member.setEmail(null);
        member.setSocialEmail(null);
        member.setPassword(null);
        member.setNickname(null);
        member.setBirthday(null);
        member.setSocialType(null);
        member.setLanguage(null);
        member.setIsMarketing(null);
        member.setGender(null);
    }
}
