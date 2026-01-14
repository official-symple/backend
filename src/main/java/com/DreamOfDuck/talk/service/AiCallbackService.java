package com.DreamOfDuck.talk.service;

import com.DreamOfDuck.global.exception.CustomException;
import com.DreamOfDuck.global.exception.ErrorCode;
import com.DreamOfDuck.talk.dto.request.AiCallbackAdviceRequest;
import com.DreamOfDuck.talk.dto.request.AiCallbackMissionRequest;
import com.DreamOfDuck.talk.dto.request.AiCallbackSummaryRequest;
import com.DreamOfDuck.talk.entity.Session;
import com.DreamOfDuck.talk.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiCallbackService {
    private final SessionRepository sessionRepository;
    private final LastEmotionAsyncService lastEmotionAsyncService;

    /**
     * AI 서버로부터 Summary 결과를 받아 저장
     * Summary 저장 후 Mission 요청을 자동으로 트리거
     */
    @Transactional
    public void saveSummary(AiCallbackSummaryRequest request) {
        Session session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_SESSION));

        session.setProblem(request.getProblem());
        session.setSolutions(request.getSolutions());

        log.info("Summary saved for session {}: problem={}",
                 request.getSessionId(), request.getProblem());

        // Summary가 저장되었으므로 이제 Mission 요청
        try {
            lastEmotionAsyncService.requestMission(request.getSessionId(), request.getProblem());
        } catch (Exception e) {
            log.error("Failed to request mission for session {}: {}",
                     request.getSessionId(), e.getMessage(), e);
            // Mission 요청 실패해도 Summary는 저장됨
        }
    }

    /**
     * AI 서버로부터 Mission 결과를 받아 저장
     */
    @Transactional
    public void saveMission(AiCallbackMissionRequest request) {
        Session session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_SESSION));

        session.setMission(request.getMission());

        log.info("Mission saved for session {}: mission={}",
                 request.getSessionId(), request.getMission());
    }

    /**
     * AI 서버로부터 Advice 결과를 받아 저장
     */
    @Transactional
    public void saveAdvice(AiCallbackAdviceRequest request) {
        Session session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_SESSION));

        session.setAdvice(request.getAdvice());

        log.info("Advice saved for session {}: advice count={}",
                 request.getSessionId(),
                 request.getAdvice() != null ? request.getAdvice().size() : 0);
    }
}
