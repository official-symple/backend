package com.DreamOfDuck.pang.controller;

import com.DreamOfDuck.account.entity.CustomUserDetails;
import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.service.MemberService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
