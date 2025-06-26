package com.DreamOfDuck.account.jwt;

import com.DreamOfDuck.account.dto.response.TokenResponse;
import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.service.MemberService;
import com.DreamOfDuck.global.exception.CustomException;
import com.DreamOfDuck.global.exception.ErrorCode;
import com.DreamOfDuck.global.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class JWTProvider {
    private final JWTUtil jwtUtil;
    private final MemberService memberService;
    private final RedisService redisService;
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000;            // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7;
    public TokenResponse createJWT(Member member){
        long now = (new Date()).getTime();

        String subject = member.getEmail();
        String accessToken = jwtUtil.createJwt("access", member.getRole().toString(), subject, ACCESS_TOKEN_EXPIRE_TIME);
        String refreshToken = jwtUtil.createJwt("refresh", member.getRole().toString(), subject, REFRESH_TOKEN_EXPIRE_TIME);
        redisService.setValues(subject, refreshToken, REFRESH_TOKEN_EXPIRE_TIME, TimeUnit.MILLISECONDS);
        return TokenResponse.of(accessToken, refreshToken, member.getRole().toString());
    }
    public TokenResponse reissue(String refreshToken){
        String category = jwtUtil.getCategory(refreshToken);
        if(!category.equals("refresh")){
            throw new CustomException(ErrorCode.NOT_REFRESH_TOKEN);
        }
        //redis에 저장되어 있는 refresh token이 프론트측에서 받은 refresh token과 같은지 확인
        String subject = jwtUtil.getSubject(refreshToken);
        String valueToken = redisService.getValues(subject);
        if(valueToken == null || !valueToken.equals(refreshToken) || !jwtUtil.getCategory(valueToken).equals("refresh")){
            throw new CustomException(ErrorCode.NOT_REFRESH_TOKEN);
        }
        Member member = memberService.findMemberByEmail(subject);

        return createJWT(member);
    }

    public void deleteToken(String subject){
        redisService.deleteValues(subject);
    }
}
