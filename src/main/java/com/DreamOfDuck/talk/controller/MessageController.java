package com.DreamOfDuck.talk.controller;

import com.DreamOfDuck.account.entity.CustomUserDetails;
import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.service.MemberService;
import com.DreamOfDuck.talk.dto.request.MessageCreateRequest;
import com.DreamOfDuck.talk.dto.request.SessionCreateRequest;
import com.DreamOfDuck.talk.dto.response.MessageFormat;
import com.DreamOfDuck.talk.dto.response.MessageResponse;
import com.DreamOfDuck.talk.dto.response.MessageResponseList;
import com.DreamOfDuck.talk.dto.response.SessionResponse;
import com.DreamOfDuck.talk.service.MessageService;
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
@RequestMapping("/api/message")
public class MessageController {
    private final MessageService messageService;
    private final MemberService memberService;

    @PostMapping("")
    @Operation(summary = "메시지 생성", description = "메시지를 생성할 때 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = MessageResponse.class)
            )})
    })
    public ResponseEntity<?> createMessage(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid @RequestBody MessageCreateRequest request){
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        MessageResponse response = messageService.save(member, request);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/chat")
    @Operation(summary = "메시지 생성 new version", description = "메시지를 생성할 때 사용하는 API, version new")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = MessageResponseList.class)
            )})
    })
    public ResponseEntity<?> createMessageNew(@AuthenticationPrincipal CustomUserDetails customUserDetails, @Valid @RequestBody MessageCreateRequest request){
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        MessageResponseList response = messageService.saveNew(member, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "특정 메시지 조회", description = "특정 메시지를 조회할 때 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = MessageFormat.class)
            )})
    })
    public ResponseEntity<?> getMessageById(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable("id") Long id){
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return ResponseEntity.ok(messageService.findById(member, id));
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "특정 메시지 삭제", description = "특정 메시지를 삭제할 때 사용하는 API")
    public ResponseEntity<?> deleteMessageById(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable("id") Long id){
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        messageService.delete(member, id);
        return ResponseEntity.ok("성공적으로 메시지을 삭제했습니다.");
    }

}
