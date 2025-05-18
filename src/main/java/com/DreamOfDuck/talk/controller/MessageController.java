package com.DreamOfDuck.talk.controller;

import com.DreamOfDuck.talk.dto.request.MessageRequest;
import com.DreamOfDuck.talk.dto.request.SessionCreateRequest;
import com.DreamOfDuck.talk.dto.response.MessageFormat;
import com.DreamOfDuck.talk.dto.response.MessageResponse;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/message")
public class MessageController {
    private final MessageService messageService;

    @PostMapping("")
    @Operation(summary = "메시지 생성", description = "메시지를 생성할 때 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = MessageResponse.class)
            )})
    })
    public ResponseEntity<?> createMessage(@Valid @RequestBody MessageRequest request){
        MessageResponse response = messageService.save(request);
        /*
        request의 content fastapi로 요청하고 응답받아서 entity저장하고 messageFormat 반환하는 service 생성
         */

        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}")
    @Operation(summary = "특정 메시지 조회", description = "특정 메시지를 조회할 때 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = MessageFormat.class)
            )})
    })
    public ResponseEntity<?> getMessageById(@PathVariable("id") Long id){
        return ResponseEntity.ok(messageService.findById(id));
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "특정 메시지 삭제", description = "특정 메시지를 삭제할 때 사용하는 API")
    public ResponseEntity<?> deleteMessageById(@PathVariable("id") Long id){
        messageService.delete(id);
        return ResponseEntity.ok("성공적으로 메시지을 삭제했습니다.");
    }
}
