package com.DreamOfDuck.account.filter;

import com.DreamOfDuck.account.dto.response.TokenResponse;
import com.DreamOfDuck.account.entity.CustomUserDetails;
import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.jwt.JWTProvider;
import com.DreamOfDuck.account.repository.MemberRepository;
import com.DreamOfDuck.account.service.MemberService;
import com.DreamOfDuck.global.exception.CustomException;
import com.DreamOfDuck.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@RequiredArgsConstructor
@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private static final String CONTENT_TYPE = "application/json";
    private final MemberRepository memberRepository;
    private final AuthenticationManager authenticationManager;
    private final JWTProvider jwtProvider;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if(request.getContentType() == null || !request.getContentType().equals(CONTENT_TYPE)){
            throw new AuthenticationServiceException("요청타입이 json이여야 합니다.");
        }
        try {
            // 요청 본문에서 JSON 형식의 데이터를 추출
            String requestBody = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
            // JSON 데이터에서 사용자 이름과 비밀번호 추출
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(requestBody);
            String username = jsonNode.get("username").asText();
            String password = jsonNode.get("password").asText();
            // 추출한 사용자 이름과 비밀번호로 UsernamePasswordAuthenticationToken 생성
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
            log.info("username: {}, password: {}", username, password);
            // AuthenticationManager를 통해 인증을 시도하고 결과 반환
            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //로그인 성공시 실행하는 메소드 (여기서 JWT를 발급하면 됨)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String username = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();
        TokenResponse tokens = jwtProvider.createJWT(memberRepository.findByEmail(username).orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_EXIST)));
        response.setHeader("Authorization", "Bearer "+tokens.getAccessToken());
        response.addCookie(createCookie("refresh", tokens.getRefreshToken()));
    }
    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60*60*60);
        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }

    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        log.info("fail to authentication\n");
        response.setStatus(401);
    }

}
