package com.DreamOfDuck.pang.controller;

import com.DreamOfDuck.account.dto.request.ScoreRequest;
import com.DreamOfDuck.account.dto.response.HomeResponse;
import com.DreamOfDuck.account.entity.CustomUserDetails;
import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.service.MemberService;
import com.DreamOfDuck.pang.dto.request.ScoreCreateRequest;
import com.DreamOfDuck.pang.dto.response.RankingResponse;
import com.DreamOfDuck.pang.dto.response.ScoreResponse;
import com.DreamOfDuck.pang.service.ScoreService;
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
@RequestMapping("/api/pang")
public class PangController {
    private final MemberService memberService;
    private final ScoreService scoreService;
    @PostMapping("/score")
    @Operation(summary = "게임 후, 점수 저장", description = "게임 후 점수 저장하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = ScoreResponse.class)
            )})
    })
    public ScoreResponse updateHeartByAd(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody @Valid ScoreCreateRequest request) {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return scoreService.createScore(member, request);
    }

    @GetMapping("/ranking")
    @Operation(summary = "랭킹 페이지 및 나의 최고 기록", description = "랭킹 페이지 및 나의 최고기록 반환하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = RankingResponse.class)
            )})
    })
    public RankingResponse getRanking(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return scoreService.getRanking(member);
    }
}
