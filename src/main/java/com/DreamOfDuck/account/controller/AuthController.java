package com.DreamOfDuck.account.controller;

import com.DreamOfDuck.account.dto.response.TokenResponse;
import com.DreamOfDuck.account.entity.CustomUserDetails;
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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

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
    public ResponseEntity<?> loginByKakao(HttpServletRequest request){
        String accessToken = jwtUtil.resolveToken(request);
        TokenResponse response = authService.kakaoLogin(accessToken);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/login/google")
    @Operation(summary = "구글 로그인", description = "구글로 로그인할 때 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = TokenResponse.class)
            )})
    })
    public ResponseEntity<?> loginByGoogle(HttpServletRequest request) throws FirebaseAuthException {
        String accessToken = jwtUtil.resolveToken(request);
        TokenResponse response = authService.googleLogin(accessToken);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/login/apple")
    @Operation(summary = "애플 로그인", description = "애플로 로그인할 때 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = TokenResponse.class)
            )})
    })
    public ResponseEntity<?> loginByApple(HttpServletRequest request) throws FirebaseAuthException {
        String accessToken = jwtUtil.resolveToken(request);
        TokenResponse response = authService.appleLogin(accessToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request){
        String refreshToken = jwtUtil.resolveToken(request);
        TokenResponse response = authService.reissue(refreshToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        String email = customUserDetails.getUsername();
        try{
            authService.logout(email);
            return ResponseEntity.ok("해당 유저가 성공적으로 로그아웃되었습니다.");
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @GetMapping("/login")
    public String login(){
        return "login";
    }

}
