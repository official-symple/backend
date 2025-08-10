package com.DreamOfDuck.record.controller;

import com.DreamOfDuck.account.entity.CustomUserDetails;
import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.service.MemberService;
import com.DreamOfDuck.record.dto.request.GoalCreateRequest;
import com.DreamOfDuck.record.dto.request.HealthCreateRequest;
import com.DreamOfDuck.record.dto.response.GoalResponse;
import com.DreamOfDuck.record.dto.response.GoalsResponse;
import com.DreamOfDuck.record.dto.response.HealthResponse;
import com.DreamOfDuck.record.service.GoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/goal")
public class GoalController {
    private final GoalService goalService;
    private final MemberService memberService;

    @PostMapping("")
    @Operation(summary = "목표 생성", description = "목표를 생성할 때 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = GoalResponse.class)
            )})
    })
    public ResponseEntity<?> createGoal(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid @RequestBody GoalCreateRequest request){
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return ResponseEntity.ok(goalService.createGoal(member, request));
    }
    @PutMapping("/{id}")
    @Operation(summary = "목표 수정", description = "목표를 수정할 때 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = GoalResponse.class)
            )})
    })
    public ResponseEntity<?> updateGoal(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid @RequestBody GoalCreateRequest request, @Parameter(description = "goal Id") @PathVariable("id") Long id){
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return ResponseEntity.ok(goalService.updateGoal(member, id, request));
    }
    @PostMapping("/success/{id}")
    @Operation(summary = "isSuccess toggle", description = "isSuccess 필드를 토클할 때 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = GoalResponse.class)
            )})
    })
    public ResponseEntity<?> toggleSuccess(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Parameter(description = "goal Id") @PathVariable("id") Long id){
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return ResponseEntity.ok(goalService.toggleSuccess(member, id));
    }
    @GetMapping("")
    @Operation(summary = "유저의 모든 목표 받기", description = "유저의 목표를 받을 때 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = GoalsResponse.class)
            )})
    })
    public ResponseEntity<?> getGoal(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return ResponseEntity.ok(goalService.getGoals(member));
    }
}
