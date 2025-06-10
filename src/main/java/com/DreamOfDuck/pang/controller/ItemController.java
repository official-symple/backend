package com.DreamOfDuck.pang.controller;

import com.DreamOfDuck.account.entity.CustomUserDetails;
import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.service.MemberService;
import com.DreamOfDuck.pang.dto.request.ItemCreateRequest;
import com.DreamOfDuck.pang.dto.response.ItemResponse;
import com.DreamOfDuck.pang.service.ItemService;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/item")
public class ItemController {
    private final ItemService itemService;
    private final MemberService memberService;
    @PostMapping("")
    @Operation(summary = "item 생성/수정", description = "item을 생성/수정할 때 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = ItemResponse.class)
            )})
    })
    public ResponseEntity<?> createItem(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid @RequestBody ItemCreateRequest request){
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return ResponseEntity.ok(itemService.save(member, request));
    }
    @GetMapping("")
    @Operation(summary = "item 얻기", description = "해당 유저의 item을 얻을 때 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = ItemResponse.class)
            )})
    })
    public ResponseEntity<?> getByUser(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return ResponseEntity.ok(itemService.getItemByHost(member));
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "특정 item 삭제", description = "특정 item을 삭제할 때 사용하는 API")
    public ResponseEntity<?> deleteItemById(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable("id") Long id){
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        itemService.delete(member, id);
        return ResponseEntity.ok("아이템이 정상적으로 삭제되었습니다.");
    }
    @DeleteMapping("")
    @Operation(summary = "유저의 item 삭제", description = "해당 유저의 item을 삭제할 때 사용하는 API")
    public ResponseEntity<?> deleteItemByUser(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        itemService.deleteByUser(member);
        return ResponseEntity.ok("아이템이 정상적으로 삭제되었습니다.");
    }
}
