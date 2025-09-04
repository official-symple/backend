package com.DreamOfDuck.pang.controller;

import com.DreamOfDuck.account.entity.CustomUserDetails;
import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.service.MemberService;
import com.DreamOfDuck.pang.dto.request.ItemUseRequest;
import com.DreamOfDuck.pang.dto.request.ScoreCreateRequest;
import com.DreamOfDuck.pang.dto.response.ItemResponse;
import com.DreamOfDuck.pang.dto.response.ScoreResponse;
import com.DreamOfDuck.pang.service.ItemService;
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
@RequestMapping("/api/item")
public class ItemController {
    private final MemberService memberService;
    private final ItemService itemService;

    @PostMapping("/tornado")
    @Operation(summary = "토네이도 + 1 ", description = "토네이도 + 1하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = ItemResponse.class)
            )})
    })
    public ItemResponse updateTornado(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return itemService.updateTornado(member);
    }
    @PostMapping("/bubblePang")
    @Operation(summary = "버블팡 + 1 ", description = "버블팡 + 1하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = ItemResponse.class)
            )})
    })
    public ItemResponse updateBubblePang(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return itemService.updateBubblePang(member);
    }
    @PostMapping("/breadCrumble")
    @Operation(summary = "빵조각 + 1 ", description = "빵조각 + 1하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = ItemResponse.class)
            )})
    })
    public ItemResponse updateBreadCrubme(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return itemService.updateBreadCrumble(member);
    }
    @PostMapping("/grass")
    @Operation(summary = "물풀 + 1 ", description = "물풀 + 1하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = ItemResponse.class)
            )})
    })
    public ItemResponse updateGrass(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return itemService.updateGrass(member);
    }
    @PostMapping("/use")
    @Operation(summary = "아이템 사용", description = "아이템을 사용한만큼 -시켜주는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = ItemResponse.class)
            )})
    })
    public ItemResponse useItem(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid @RequestBody ItemUseRequest request) {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return itemService.useItem(member, request);
    }
    @GetMapping
    @Operation(summary = "아이템 조회", description = "사용자의 아이템 조회하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = ItemResponse.class)
            )})
    })
    public ItemResponse getItem(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return itemService.getItem(member);
    }
}
