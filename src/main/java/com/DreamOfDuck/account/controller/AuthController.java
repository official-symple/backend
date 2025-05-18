package com.DreamOfDuck.account.controller;

import com.DreamOfDuck.account.dto.request.LoginRequest;
import com.DreamOfDuck.account.dto.response.TokenResponse;
import com.DreamOfDuck.account.service.AuthService;
import com.DreamOfDuck.account.service.MemberService;
import com.DreamOfDuck.talk.dto.response.MessageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
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
    @Operation(summary = "카카오톡 로그인", description = "카카오톡으로 로그인할 때 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = TokenResponse.class)
            )})
    })
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request){
        TokenResponse response = authService.kakaoLogin(request);
        return ResponseEntity.ok(response);
    }
}
