package com.DreamOfDuck.account.jwt;

import com.DreamOfDuck.account.dto.response.TokenResponse;
import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
@RequiredArgsConstructor
public class JWTProvider {
    private final JWTUtil jwtUtil;
    private final MemberService memberService;
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;            // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7;
    public TokenResponse createJWT(Member member){
        long now = (new Date()).getTime();

        String subject = member.getEmail();
        String accessToken = jwtUtil.createJwt("access", member.getRole().toString(), subject, ACCESS_TOKEN_EXPIRE_TIME);
        String refreshToken = jwtUtil.createJwt("refresh", member.getRole().toString(), subject, REFRESH_TOKEN_EXPIRE_TIME);
        //redisService.setValues(subject, refreshToken, REFRESH_TOKEN_EXPIRE_TIME, TimeUnit.MILLISECONDS);
        return TokenResponse.of(accessToken, refreshToken, member.getRole().toString());
    }
    public TokenResponse reissue(String refreshToken){
        String category = jwtUtil.getCategory(refreshToken);
//        if(!category.e quals("refresh")){
//            throw new BadRequestException(IS_NOT_REFRESHTOKEN);
//        }
        //redis에 저장되어 있는 refresh token이 프론트측에서 받은 refresh token과 같은지 확인
        String subject = jwtUtil.getSubject(refreshToken);
        //String valueToken = redisService.getValues(subject);
//        if(valueToken == null || !valueToken.equals(refreshToken) || !jwtUtil.getCategory(valueToken).equals("refresh")){
//            throw new BadRequestException(IS_NOT_REFRESHTOKEN);
//        }
        Member member = memberService.findMemberByEmail(subject);

        return createJWT(member);
    }
    public TokenResponse signup(Member member){

        //String refreshToken = redisService.getValues(member.getEmail());

        String subject = member.getEmail();
        String role = member.getRole().toString();
        String newAT=jwtUtil.createJwt("access", role, subject, ACCESS_TOKEN_EXPIRE_TIME);
        String newRT=jwtUtil.createJwt("refresh", role, subject, REFRESH_TOKEN_EXPIRE_TIME);
        //log.info("original refresh token : "+redisService.getValues(subject));
        //redisService.setValues(subject, newRT, REFRESH_TOKEN_EXPIRE_TIME, TimeUnit.MILLISECONDS);
        //log.info("new refresh token : "+redisService.getValues(subject));
        return new TokenResponse(newAT, newRT, role);
    }

//    public void deleteToken(String subject){
//        redisService.deleteValues(subject);
//    }
}
