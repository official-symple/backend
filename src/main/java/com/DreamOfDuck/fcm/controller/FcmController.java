package com.DreamOfDuck.fcm.controller;

import com.DreamOfDuck.account.entity.CustomUserDetails;
import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.service.MemberService;
import com.DreamOfDuck.fcm.dto.FcmRequest;
import com.DreamOfDuck.fcm.service.FcmService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fcm")
public class FcmController {
    private final FcmService fcmService;
    private final MemberService memberService;
    @PostMapping("/pushMessage")
    public ResponseEntity<?> pushMessage(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid @RequestBody FcmRequest request) throws IOException {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        String deviceToken = member.getDeviceToken();
        fcmService.sendMessageTo(deviceToken, request);
        return ResponseEntity.ok("fcm alarm success");
    }
}
