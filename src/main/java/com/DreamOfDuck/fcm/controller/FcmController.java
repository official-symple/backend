package com.DreamOfDuck.fcm.controller;

import com.DreamOfDuck.account.dto.response.MemberResponse;
import com.DreamOfDuck.account.entity.CustomUserDetails;
import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.service.MemberService;
import com.DreamOfDuck.fcm.dto.FcmRequest;
import com.DreamOfDuck.fcm.dto.PushAlarmRequest;
import com.DreamOfDuck.fcm.dto.PushAlarmResponse;
import com.DreamOfDuck.fcm.service.FcmService;
import com.DreamOfDuck.fcm.service.PushAlarmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fcm")
public class FcmController {
    private final PushAlarmService pushAlarmService;
    private final FcmService fcmService;
    private final MemberService memberService;
    @PostMapping("/pushMessage")
    public ResponseEntity<?> pushMessage(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid @RequestBody FcmRequest request) throws IOException {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        String deviceToken = member.getDeviceToken();
        fcmService.sendMessageTo(deviceToken, request);
        return ResponseEntity.ok("fcm alarm success");
    }

    @PostMapping("/setting")
    @Operation(summary = "푸시알림 동의 여부", description = "푸시알림 동의 여부 셋할 때 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = PushAlarmResponse.class)
            )})
    })
    public PushAlarmResponse pushAlarm(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid @RequestBody PushAlarmRequest request) throws IOException {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return pushAlarmService.setAlarm(member, request);
    }
    @GetMapping("/setting")
    @Operation(summary = "푸시알림 동의 여부", description = "푸시알림 동의 여부 얻을 때 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = PushAlarmResponse.class)
            )})
    })
    public PushAlarmResponse getPushAlarm(@AuthenticationPrincipal CustomUserDetails customUserDetails) throws IOException {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return pushAlarmService.getAlarm(member);
    }
}
