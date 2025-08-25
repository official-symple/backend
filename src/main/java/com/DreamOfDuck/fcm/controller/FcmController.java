package com.DreamOfDuck.fcm.controller;

import com.DreamOfDuck.account.entity.CustomUserDetails;
import com.DreamOfDuck.account.service.MemberService;
import com.DreamOfDuck.fcm.dto.FcmSend;
import com.DreamOfDuck.fcm.service.FcmService;
import com.DreamOfDuck.pang.dto.response.ItemResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fcm")
public class FcmController {
    private final MemberService memberService;
    private final FcmService fcmService;

    @PostMapping("/send")
    public ResponseEntity<?> pushMessage(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody @Valid FcmSend fcmSend) throws IOException {
        log.debug("[+] 푸시 메시지를 전송합니다. ");
        //fcmSend.setToken(customUserDetails.getUsername());
        int result = fcmService.sendMessageTo(fcmSend);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}