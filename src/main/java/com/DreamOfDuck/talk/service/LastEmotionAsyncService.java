package com.DreamOfDuck.talk.service;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.global.exception.CustomException;
import com.DreamOfDuck.global.exception.ErrorCode;
import com.DreamOfDuck.talk.dto.request.*;
import com.DreamOfDuck.talk.entity.Emotion;
import com.DreamOfDuck.talk.entity.Session;
import com.DreamOfDuck.talk.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class LastEmotionAsyncService {
    private final SessionRepository sessionRepository;
    private final RestTemplate restTemplate;
    @Value("${fastApi.summary.endpoint}")
    private String endpoint_summary;
    @Value("${fastApi.mission.endpoint}")
    private String endpoint_mission;
    @Value("${fastApi.advice.endpoint}")
    private String endpoint_advice;

    /**
     * Summary 요청을 AI 서버에 전송 (즉시 200 응답 확인)
     * AI 서버가 처리를 완료하면 콜백 API로 결과를 전송함
     * Summary 콜백 수신 시 Mission 요청이 자동으로 트리거됨
     */
    public void requestSummary(Member host, Long sessionId){
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_SESSION));

        // Summary 요청 생성
        SummaryRequestF summaryRequest = SummaryRequestF.builder()
                .sessionId(sessionId)  // AI 서버 콜백용
                .language(host.getLanguage() == null ? "kor" : host.getLanguage().toString().toLowerCase())
                .messages(MessageFormatF.fromSession(session))
                .emotionCause(session.getCause().getText())
                .emotion(session.getEmotion().stream().map(Emotion::getText).collect(Collectors.toList()))
                .persona(session.getDuckType().getValue())
                .formal(session.getIsFormal())
                .build();

        // Summary 요청 전송 (즉시 200 응답)
        requestSummaryToAiServer(summaryRequest, sessionId);
        log.info("Summary request sent for session {}", sessionId);
    }

    /**
     * Mission 요청을 AI 서버에 전송 (Summary 콜백 수신 후 호출됨)
     */
    public void requestMission(Long sessionId, String summary){
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_SESSION));

        Member host = session.getHost();

        // Mission 요청 생성 (summary 포함)
        MissionRequestF missionRequest = MissionRequestF.builder()
                .sessionId(sessionId)  // AI 서버 콜백용
                .persona(session.getDuckType().getValue())
                .language(host.getLanguage() == null ? "kor" : host.getLanguage().toString().toLowerCase())
                .formal(session.getIsFormal())
                .summary(summary)  // Summary 콜백에서 받은 결과 전달
                .nickname(host.getNickname())
                .emotion_cause(session.getCause().getText())
                .emotion(session.getEmotion().stream().map(Emotion::getText).collect(Collectors.toList()))
                .build();

        // Mission 요청 전송 (즉시 200 응답)
        requestMissionToAiServer(missionRequest, sessionId);
        log.info("Mission request sent for session {}", sessionId);
    }
    /**
     * Summary 요청을 AI 서버에 전송 (즉시 200 응답만 확인)
     */
    private void requestSummaryToAiServer(SummaryRequestF request, Long sessionId){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SummaryRequestF> requestEntity = new HttpEntity<>(request, headers);

        try {
            log.info("Sending summary request to AI server for session {}", sessionId);
            ResponseEntity<Void> res = restTemplate.exchange(
                    endpoint_summary,
                    HttpMethod.POST,
                    requestEntity,
                    Void.class
            );

            if (res.getStatusCode().is2xxSuccessful()) {
                log.info("Summary request accepted by AI server for session {}", sessionId);
            } else {
                log.error("Unexpected response from AI server: {}", res.getStatusCode());
                throw new CustomException(ErrorCode.NOT_FOUND_AI_SERVER);
            }
        } catch(RestClientException e) {
            log.error("Failed to send summary request to AI server: {}", e.getMessage());
            throw new CustomException(ErrorCode.NOT_FOUND_AI_SERVER);
        }
    }

    /**
     * Mission 요청을 AI 서버에 전송 (즉시 200 응답만 확인)
     */
    private void requestMissionToAiServer(MissionRequestF request, Long sessionId){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MissionRequestF> requestEntity = new HttpEntity<>(request, headers);

        try {
            log.info("Sending mission request to AI server for session {}", sessionId);
            ResponseEntity<Void> res = restTemplate.exchange(
                    endpoint_mission,
                    HttpMethod.POST,
                    requestEntity,
                    Void.class
            );

            if (res.getStatusCode().is2xxSuccessful()) {
                log.info("Mission request accepted by AI server for session {}", sessionId);
            } else {
                log.error("Unexpected response from AI server: {}", res.getStatusCode());
                throw new CustomException(ErrorCode.NOT_FOUND_AI_SERVER);
            }
        } catch(RestClientException e) {
            log.error("Failed to send mission request to AI server: {}", e.getMessage());
            throw new CustomException(ErrorCode.NOT_FOUND_AI_SERVER);
        }
    }
    /**
     * Advice 요청을 AI 서버에 전송 (즉시 200 응답 확인)
     * AI 서버가 처리를 완료하면 콜백 API로 결과를 전송함
     */
    public void requestAdvice(Member host, Long sessionId){
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_SESSION));

        AdviceRequestF adviceRequest = AdviceRequestF.builder()
                .sessionId(sessionId)  // AI 서버 콜백용
                .messages(MessageFormatF.fromSession(session))
                .language(host.getLanguage() == null ? "kor" : host.getLanguage().toString().toLowerCase())
                .persona(session.getDuckType().getValue())
                .formal(session.getIsFormal())
                .nickname(session.getHost().getNickname())
                .build();

        requestAdviceToAiServer(adviceRequest, sessionId);
        log.info("Advice request sent for session {}", sessionId);
    }
    /**
     * Advice 요청을 AI 서버에 전송 (즉시 200 응답만 확인)
     */
    private void requestAdviceToAiServer(AdviceRequestF request, Long sessionId){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AdviceRequestF> requestEntity = new HttpEntity<>(request, headers);

        try {
            log.info("Sending advice request to AI server for session {}", sessionId);
            ResponseEntity<Void> res = restTemplate.exchange(
                    endpoint_advice,
                    HttpMethod.POST,
                    requestEntity,
                    Void.class
            );

            if (res.getStatusCode().is2xxSuccessful()) {
                log.info("Advice request accepted by AI server for session {}", sessionId);
            } else {
                log.error("Unexpected response from AI server: {}", res.getStatusCode());
                throw new CustomException(ErrorCode.NOT_FOUND_AI_SERVER);
            }
        } catch(RestClientException e) {
            log.error("Failed to send advice request to AI server: {}", e.getMessage());
            throw new CustomException(ErrorCode.NOT_FOUND_AI_SERVER);
        }
    }
}
