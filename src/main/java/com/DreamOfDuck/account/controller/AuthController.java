package com.DreamOfDuck.account.controller;

import com.DreamOfDuck.account.dto.request.LoginRequest;
import com.DreamOfDuck.account.dto.response.TokenResponse;
import com.DreamOfDuck.account.service.AuthService;
import com.DreamOfDuck.account.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final MemberService memberService;
    @PostMapping("/login/kakao")
    public ResponseEntity<?> login(@RequestBody LoginRequest request){
        TokenResponse response = authService.kakaoLogin(request);
        return ResponseEntity.ok(response);
    }
}
