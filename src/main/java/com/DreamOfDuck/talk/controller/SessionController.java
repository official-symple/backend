package com.DreamOfDuck.talk.controller;

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
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/session")
public class SessionController {
    private final SessionService sessionService;

    @PostMapping()
    @Operation(summary = "세션 생성", description = "세션을 생성할 때 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = SessionResponse.class)
            )})
    })
    public ResponseEntity<?> createSession(@Valid @RequestBody SessionCreateRequest request){
        return ResponseEntity.ok(sessionService.save(request));
    }
    @PostMapping("/emotion")
    @Operation(summary = "마지막 감정 점검", description = "마지막 감정을 점검할 때 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = SessionResponse.class)
            )})
    })
    public ResponseEntity<?> updateSession(@Valid @RequestBody SessionUpdateRequest request){
        return ResponseEntity.ok(sessionService.update(request));
    }
    @GetMapping("/{id}")
    @Operation(summary = "특정 세션 조회", description = "특정 세션을 조회할 때 사용하는 API")
    @ApiResponses(value={
            @ApiResponse(responseCode="200", content = {@Content(schema= @Schema(implementation = SessionResponse.class)
            )})
    })
    public ResponseEntity<?> getSessionById(@PathVariable("id") Long id){
        return ResponseEntity.ok(sessionService.findById(id));
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "특정 세션 삭제", description = "특정 세션을 삭제할 때 사용하는 API")
    public ResponseEntity<?> deleteSessionById(@PathVariable("id") Long id){
        sessionService.delete(id);
        return ResponseEntity.ok("성공적으로 세션을 삭제했습니다.");
    }
    /*
    앞으로 작업할 api
    after generating member entity,
    get sessions by member
     */

}
