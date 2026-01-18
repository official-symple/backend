package com.DreamOfDuck.account.controller;

import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.DreamOfDuck.account.dto.request.ATRequest;
import com.DreamOfDuck.account.dto.response.TokenResponse;
import com.DreamOfDuck.account.entity.CustomUserDetails;
import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.jwt.JWTUtil;
import com.DreamOfDuck.account.service.AuthService;
import com.DreamOfDuck.account.service.MemberService;
import com.google.firebase.auth.FirebaseAuthException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JWTUtil jwtUtil;
    private final MemberService memberService;
    @PostMapping("/login/kakao")
    @Operation(summary = "카카오톡 로그인", description = "카카오톡으로 로그인할 때 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = TokenResponse.class)
            )})
    })
    public ResponseEntity<?> loginByKakao(HttpServletRequest request, @RequestBody @Valid ATRequest atRequest) {
        String accessToken = jwtUtil.resolveToken(request);
        if (accessToken == null) {
            accessToken = atRequest.getAccessToken();
        }
        TokenResponse response = authService.kakaoLogin(accessToken);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/login/google")
    @Operation(summary = "구글 로그인", description = "구글로 로그인할 때 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = TokenResponse.class)
            )})
    })
    public ResponseEntity<?> loginByGoogle(HttpServletRequest request, @RequestBody @Valid ATRequest atRequest) throws FirebaseAuthException {
        String accessToken = atRequest.getAccessToken(); 
        if (accessToken == null) {
            accessToken = jwtUtil.resolveToken(request);
        }
        TokenResponse response = authService.googleLogin(accessToken);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/login/apple")
    @Operation(summary = "애플 로그인", description = "애플로 로그인할 때 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = TokenResponse.class)
            )})
    })
    public ResponseEntity<?> loginByApple(HttpServletRequest request, @RequestBody @Valid ATRequest atRequest) throws FirebaseAuthException {
        String accessToken = jwtUtil.resolveToken(request);
        if (accessToken == null) {
            accessToken = atRequest.getAccessToken();
        }
        TokenResponse response = authService.appleLogin(accessToken);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/reissue")
    @Operation(summary = "토큰 재발급", description = "토큰 재발급할 때 사용하는 api(in header, Authorization : Bearer <refresh token> 형식)")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = TokenResponse.class)
            )})
    })
    public ResponseEntity<?> reissue(HttpServletRequest request){
        String refreshToken = jwtUtil.resolveToken(request);
        TokenResponse response = authService.reissue(refreshToken);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "로그아웃할 때 사용하는 api")
    public ResponseEntity<?> logout(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        String email = customUserDetails.getUsername();
        try{
            authService.logout(email);
            return ResponseEntity.ok("해당 유저가 성공적으로 로그아웃되었습니다.");
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @PostMapping("/cancel")
    @Operation(summary="회원 탈퇴", description = "회원 탈퇴할 때 사용하는 api")
    public ResponseEntity<?> cancel(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        authService.cancelMembership(member);
        return ResponseEntity.ok(new HashMap<>());
    }
    @GetMapping("/login")
    public String login(){
        return "login";
    }


}
