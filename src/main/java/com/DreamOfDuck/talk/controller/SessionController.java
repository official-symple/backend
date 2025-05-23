package com.DreamOfDuck.talk.controller;

import com.DreamOfDuck.account.entity.CustomUserDetails;
import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.service.MemberService;
import com.DreamOfDuck.talk.dto.request.SessionCreateRequest;
import com.DreamOfDuck.talk.dto.request.SessionUpdateRequest;
import com.DreamOfDuck.talk.dto.response.SessionResponse;
import com.DreamOfDuck.talk.service.SessionService;
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
@RequestMapping("/api/session")
public class SessionController {
    private final SessionService sessionService;
    private final MemberService memberService;

    @PostMapping()
    @Operation(summary = "세션 생성", description = "세션을 생성할 때 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = SessionResponse.class)
            )})
    })
    public ResponseEntity<?> createSession(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid @RequestBody SessionCreateRequest request){
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return ResponseEntity.ok(sessionService.save(member, request));
    }
    @PostMapping("/emotion")
    @Operation(summary = "마지막 감정 점검", description = "마지막 감정을 점검할 때 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = SessionResponse.class)
            )})
    })
    public ResponseEntity<?> updateSession(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid @RequestBody SessionUpdateRequest request){
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return ResponseEntity.ok(sessionService.update(member, request));
    }
    @GetMapping("/{id}")
    @Operation(summary = "특정 세션 조회", description = "특정 세션을 조회할 때 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = SessionResponse.class)
            )})
    })
    public ResponseEntity<?> getSessionById(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable("id") Long id){
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return ResponseEntity.ok(sessionService.findById(member, id));
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "특정 세션 삭제", description = "특정 세션을 삭제할 때 사용하는 API")
    public ResponseEntity<?> deleteSessionById(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable("id") Long id){
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        sessionService.delete(member, id);
        return ResponseEntity.ok("성공적으로 세션을 삭제했습니다.");
    }
    /*
    앞으로 작업할 api
    after generating member entity,
    get sessions by member
     */

}
