package com.DreamOfDuck.talk.controller;

import com.DreamOfDuck.global.dto.response.SuccessResponse;
import com.DreamOfDuck.talk.dto.request.AiCallbackAdviceRequest;
import com.DreamOfDuck.talk.dto.request.AiCallbackMissionRequest;
import com.DreamOfDuck.talk.dto.request.AiCallbackSummaryRequest;
import com.DreamOfDuck.talk.service.AiCallbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AI Callback", description = "AI 서버로부터 처리 결과를 받는 콜백 API")
@RestController
@RequestMapping("/api/ai/callback")
@RequiredArgsConstructor
@Slf4j
public class AiCallbackController {
    private final AiCallbackService aiCallbackService;

    @Operation(summary = "Summary 결과 콜백", description = "AI 서버가 summary 생성을 완료한 후 호출하는 엔드포인트")
    @PostMapping("/summary")
    public SuccessResponse<Void> receiveSummary(@Valid @RequestBody AiCallbackSummaryRequest request) {
        log.info("Received summary callback for session: {}", request.getSessionId());
        aiCallbackService.saveSummary(request);
        return SuccessResponse.ok(null);
    }

    @Operation(summary = "Mission 결과 콜백", description = "AI 서버가 mission 생성을 완료한 후 호출하는 엔드포인트")
    @PostMapping("/mission")
    public SuccessResponse<Void> receiveMission(@Valid @RequestBody AiCallbackMissionRequest request) {
        log.info("Received mission callback for session: {}", request.getSessionId());
        aiCallbackService.saveMission(request);
        return SuccessResponse.ok(null);
    }

    @Operation(summary = "Advice 결과 콜백", description = "AI 서버가 advice 생성을 완료한 후 호출하는 엔드포인트")
    @PostMapping("/advice")
    public SuccessResponse<Void> receiveAdvice(@Valid @RequestBody AiCallbackAdviceRequest request) {
        log.info("Received advice callback for session: {}", request.getSessionId());
        aiCallbackService.saveAdvice(request);
        return SuccessResponse.ok(null);
    }
}
