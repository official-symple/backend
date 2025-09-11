package com.DreamOfDuck.mind.controller;

import com.DreamOfDuck.account.entity.CustomUserDetails;
import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.service.MemberService;
import com.DreamOfDuck.mind.dto.request.MindCheckRequest;
import com.DreamOfDuck.mind.dto.request.MindCheckTimeRequest;
import com.DreamOfDuck.mind.dto.response.MindCheckReport;
import com.DreamOfDuck.mind.dto.response.MindCheckReportPeriod;
import com.DreamOfDuck.mind.dto.response.MindCheckResponse;
import com.DreamOfDuck.mind.dto.response.MindCheckTimeResponse;
import com.DreamOfDuck.mind.service.MindCheckService;
import com.DreamOfDuck.pang.dto.request.ItemUseRequest;
import com.DreamOfDuck.pang.dto.response.ItemResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mindcheck")
public class MindCheckController {
    private final MindCheckService mindCheckService;
    private final MemberService memberService;

    @PostMapping("/time")
    @Operation(summary = "마음체크 푸시알림 시간 설정", description = "마음체크 푸시알림 시간 설정하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(
                    array=@ArraySchema(schema=@Schema(implementation = MindCheckTimeResponse.class))
            )})
    })
    public List<MindCheckTimeResponse> setMindCheckTime(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid @RequestBody MindCheckTimeRequest request){
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return mindCheckService.setMindCheckTime(member, request);
    }
    @GetMapping("/time")
    @Operation(summary = "마음체크 푸시알림 시간 받기", description = "마음체크 푸시알림 시간 받는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(
                    array=@ArraySchema(schema=@Schema(implementation = MindCheckTimeResponse.class))
            )})
    })
    public List<MindCheckTimeResponse> getMindCheckTime(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return mindCheckService.getMindCheckTimes(member);
    }

    @PostMapping("")
    @Operation(summary = "마음체크 작성", description = "마음체크 작성하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = MindCheckResponse.class)
            )})
    })
    public MindCheckResponse useItem(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid @RequestBody MindCheckRequest request) {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return mindCheckService.checkMind(member, request);
    }
    @GetMapping("/report/{date}")
    @Operation(summary = "일별 마음체크 받기", description = "일별 마음체크를 받는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = MindCheckReport.class)
            )})
    })
    public MindCheckReport getMindCheckReport(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return mindCheckService.getMindCheckResult(member, date);
    }
    @GetMapping("/report/2weeks/{date}")
    @Operation(summary = "2주간 마음체크 받기", description = "2주간 마음체크를 받는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = MindCheckReportPeriod.class)
            )})
    })
    public MindCheckReportPeriod getMindCheckReportPer2Weeks(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return mindCheckService.getMindCheckResultPer2Weeks(member, date);
    }
    @GetMapping("/report/month/{date}")
    @Operation(summary = "한달간 마음체크 받기", description = "한달간 마음체크를 받는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = MindCheckReportPeriod.class)
            )})
    })
    public MindCheckReportPeriod getMindCheckReportPerMonth(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return mindCheckService.getMindCheckResultPer1Month(member, date);
    }


}
