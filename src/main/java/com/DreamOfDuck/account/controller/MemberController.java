package com.DreamOfDuck.account.controller;

import com.DreamOfDuck.account.dto.request.*;
import com.DreamOfDuck.account.dto.response.HomeResponse;
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

    @GetMapping("/home")
    @Operation(summary = "홈화면 점수받기", description = "유저의 홈화면 정보를 불러오는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = HomeResponse.class)
            )})
    })
    public HomeResponse getHomeInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return memberService.getHomeInfo(member);
    }
    @PostMapping("/heart")
    @Operation(summary = "하트 업데이트", description = "하트를 업데이트하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = HomeResponse.class)
            )})
    })
    public HomeResponse updateHeart(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody @Valid HeartRequest request) {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return memberService.updateHeart(member, request);
    }
    @PostMapping("/dia")
    @Operation(summary = "다이아 업데이트", description = "다이아를 업데이트하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = HomeResponse.class)
            )})
    })
    public HomeResponse updateHeart(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody @Valid DiaRequest request) {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return memberService.updateDia(member, request);
    }
    @PostMapping("/feather")
    @Operation(summary = "깃털 업데이트", description = "깃털을 업데이트하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = HomeResponse.class)
            )})
    })
    public HomeResponse updateFeather(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody @Valid FeatherRequest request) {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return memberService.updateFeather(member, request);
    }
    @PostMapping("/duckname")
    @Operation(summary = "오리이름 업데이트", description = "오리이름을 업데이트하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = HomeResponse.class)
            )})
    })
    public HomeResponse updateDuckName(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody @Valid DucknameRequest request) {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return memberService.updateDuckname(member, request);
    }
    @PostMapping("/lv")
    @Operation(summary = "레벨 업데이트", description = "레벨을 업데이트하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = HomeResponse.class)
            )})
    })
    public HomeResponse updateLv(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody @Valid LvRequest request) {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return memberService.updateLv(member, request);
    }
}
