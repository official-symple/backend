package com.DreamOfDuck.talk.controller;

import com.DreamOfDuck.account.entity.CustomUserDetails;
import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.service.MemberService;
import com.DreamOfDuck.talk.dto.request.InterviewCreateRequest;
import com.DreamOfDuck.talk.dto.response.InterviewResponse;
import com.DreamOfDuck.talk.service.InterviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/interview")
public class InterviewController {
    private final InterviewService interviewService;
    private final MemberService memberService;

    @PostMapping("")
    @Operation(summary = "접수면접 생성/수정", description = "접수면접을 생성/수정할 때 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = InterviewResponse.class)
            )})
    })
    public ResponseEntity<?> createItem(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid @RequestBody InterviewCreateRequest request){
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return ResponseEntity.ok(interviewService.save(member, request));
    }
    @GetMapping("")
    @Operation(summary = "접수면접 얻기", description = "해당 유저의 item을 얻을 때 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = InterviewResponse.class)
            )})
    })
    public ResponseEntity<?> getByUser(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return ResponseEntity.ok(interviewService.getInterviewByHost(member));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "특정 접수면접 삭제", description = "특정 접수면접을 삭제할 때 사용하는 API")
    public ResponseEntity<?> deleteItemById(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable("id") Long id){
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        interviewService.deleteById(member, id);
        return ResponseEntity.ok("접수면접이 정상적으로 삭제되었습니다.");
    }
    @DeleteMapping("")
    @Operation(summary = "유저의 접수면접 삭제", description = "해당 유저의 접수면접을 삭제할 때 사용하는 API")
    public ResponseEntity<?> deleteItemByUser(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        interviewService.deleteByUser(member);
        return ResponseEntity.ok("접수면접이 정상적으로 삭제되었습니다.");
    }

}
