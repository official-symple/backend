package com.DreamOfDuck.account.controller;

import com.DreamOfDuck.account.dto.request.MemberCreateRequest;
import com.DreamOfDuck.account.dto.request.MemberUpdateRequest;
import com.DreamOfDuck.account.dto.request.ScoreRequest;
import com.DreamOfDuck.account.dto.response.MemberResponse;
import com.DreamOfDuck.account.entity.CustomUserDetails;
import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.service.MemberService;
import com.DreamOfDuck.talk.dto.response.MessageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {
    private final MemberService memberService;
    @PostMapping("/signup")
    @Operation(summary = "개인정보 입력", description = "개인정보를 생성할 때 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = MemberResponse.class)
            )})
    })
    public MemberResponse signup(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody @Valid MemberCreateRequest request) {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return memberService.join(member, request);
    }
    @PutMapping("/update")
    @Operation(summary = "개인정보 수정", description = "개인정보를 수정할 때 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = MemberResponse.class)
            )})
    })
    public MemberResponse updateMember(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody @Valid MemberUpdateRequest
            request) {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return memberService.updateMemberInfo(member, request);
    }

    @GetMapping("")
    @Operation(summary = "개인정보 받기", description = "개인정보를 받을 때 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = MemberResponse.class)
            )})
    })
    public MemberResponse getInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return MemberResponse.from(member);
    }

    @PostMapping("/score")
    @Operation(summary = "점수 업데이트", description = "유저의 이전 최대 점수와 비교해 더 높으면 업데이트하는 api(이전 점수가 없다면 생성)")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = MemberResponse.class)
            )})
    })
    public MemberResponse updateScore(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody @Valid ScoreRequest request) {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return memberService.updateScore(member, request);
    }


}
