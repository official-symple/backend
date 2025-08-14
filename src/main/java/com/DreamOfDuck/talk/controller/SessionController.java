package com.DreamOfDuck.talk.controller;

import com.DreamOfDuck.account.entity.CustomUserDetails;
import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.service.MemberService;
import com.DreamOfDuck.talk.dto.request.FeedbackRequest;
import com.DreamOfDuck.talk.dto.request.SessionCreateRequest;
import com.DreamOfDuck.talk.dto.request.SessionUpdateRequest;
import com.DreamOfDuck.talk.dto.response.*;
import com.DreamOfDuck.talk.service.LastEmotionAsyncService;
import com.DreamOfDuck.talk.service.SessionService;
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
@RequestMapping("/api/session")
public class SessionController {
    private final SessionService sessionService;
    private final MemberService memberService;
    private final LastEmotionAsyncService lastEmotionAsyncService;

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
        SessionResponse res = sessionService.update(member, request);

        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/emotion/{id}")
    @Operation(summary = "마지막 감정 삭제(개발용)", description = "마지막 감정을 삭제할 때 사용하는 API")
    public ResponseEntity<?> deleteEmotion(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable("id") Long id){
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        sessionService.deleteEmotion(member, id);
        return ResponseEntity.ok("마지막 감정을 정상적으로 삭제했습니다.");
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
    @GetMapping("")
    @Operation(summary = "세션 정보 불러오기", description = "유저의 모든 세션을 불러올 때 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = SessionResponseList.class)
            )})
    })
    public ResponseEntity<?> getSessionByUser(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return ResponseEntity.ok(sessionService.findByUser(member));
    }
    @PostMapping("/summary/{id}")
    @Operation(summary = "오늘의 리포트 불러오기", description = "채팅 후 오늘의 리포트를 가져올 때 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = ReportResponse.class)
            )})
    })
    public ResponseEntity<?> getReportBySessionId(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Parameter(description = "session Id") @PathVariable("id") Long id){
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return ResponseEntity.ok(sessionService.getReportById(member, id));
    }
    @PostMapping("/mission/{id}")
    @Operation(summary = "오늘의 미션 불러오기", description = "채팅 후 오늘의 미션을 가져올 때 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = MissionResponse.class)
            )})
    })
    public ResponseEntity<?> getMissionBySessionId(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Parameter(description = "session Id") @PathVariable("id") Long id){
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return ResponseEntity.ok(sessionService.getMissionById(member, id));
    }
    @PostMapping("/advice/{id}")
    @Operation(summary = "오늘의 미션 단어 조합 불러오기", description = "mission api호출 후 단어 조합 불러올 때 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = AdviceResponse.class)
            )})
    })
    public ResponseEntity<?> getAdviceBySessionId(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Parameter(description = "session Id") @PathVariable("id") Long id){
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return ResponseEntity.ok(sessionService.getAdviceById(member, id));
    }
    @PostMapping("/feedback/{id}")
    @Operation(summary = "세션별 피드백 접수하기", description = "세션별 피드백 접수할 때 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = FeedbackResponse.class)
            )})
    })
    public ResponseEntity<?> createFeedback(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid @RequestBody FeedbackRequest request, @Parameter(description = "session Id") @PathVariable("id") Long id){
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return ResponseEntity.ok(sessionService.saveFeedback(member, request, id));
    }

}
