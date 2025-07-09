package com.DreamOfDuck.talk.service;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.global.exception.CustomException;
import com.DreamOfDuck.global.exception.ErrorCode;
import com.DreamOfDuck.talk.dto.request.MessageFormatF;
import com.DreamOfDuck.talk.dto.request.MessageRequestF;
import com.DreamOfDuck.talk.dto.response.AdviceResponse;
import com.DreamOfDuck.talk.dto.response.MissionResponse;
import com.DreamOfDuck.talk.dto.response.ReportResponse;
import com.DreamOfDuck.talk.dto.response.SummaryResponseF;
import com.DreamOfDuck.talk.entity.Emotion;
import com.DreamOfDuck.talk.entity.Session;
import com.DreamOfDuck.talk.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly=true)
@RequiredArgsConstructor
public class AsyncService {
    private final SessionRepository sessionRepository;
    private final RestTemplate restTemplate;
    @Value("${fastApi.summary.endpoint}")
    private String endpoint_summary;
    @Value("${fastApi.mission.endpoint}")
    private String endpoint_mission;
    @Value("${fastApi.advice.endpoint}")
    private String endpoint_advice;

    @Async
    @Transactional
    public void saveSolution(Member host, Long sessionId){
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_SESSION));

        if(session.getHost()!=host){
            throw new CustomException(ErrorCode.DIFFERENT_USER_SESSION);
        }
        if(session.getLastEmotion() == null){
            throw new CustomException(ErrorCode.LAST_EMOTION_NOT_EXIST);
        }
        /*fast api와 연동*/
        MessageRequestF requestF = MessageRequestF.builder()
                .persona(session.getDuckType().getValue())
                .formal(session.getIsFormal())
                .emotion(session.getEmotion().stream().map(Emotion::getText).collect(Collectors.toList()))
                .emotion_cause(session.getCause().getText())
                .messages(MessageFormatF.fromSession(session))
                .build();
        SummaryResponseF responseF = getSummary(requestF);
        session.setProblem(responseF.getProblem());
        session.setSolutions(responseF.getSolutions());
        sessionRepository.save(session);
        /*fast api와 연동*/
        return;
    }
    private SummaryResponseF getSummary(MessageRequestF request){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MessageRequestF> requestEntity = new HttpEntity<>(request, headers);
        try{
            ResponseEntity<SummaryResponseF> res = restTemplate.exchange(endpoint_summary, HttpMethod.POST, requestEntity, SummaryResponseF.class);
            if(res.getBody()==null){
                throw new RuntimeException("fastApi로 부터 응답이 없습니다.");
            }
            return res.getBody();
        } catch(RestClientException e) {
            throw new CustomException(ErrorCode.NOT_FOUND_AI_SERVER);
        }
    }
    @Async
    @Transactional
    public void saveMission(Member host, Long sessionId){
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_SESSION));

        if(session.getHost()!=host){
            throw new CustomException(ErrorCode.DIFFERENT_USER_SESSION);
        }
        if(session.getLastEmotion() == null){
            throw new CustomException(ErrorCode.LAST_EMOTION_NOT_EXIST);
        }
        /*fast api와 연동*/
        MessageRequestF requestF = MessageRequestF.builder()
                .persona(session.getDuckType().getValue())
                .formal(session.getIsFormal())
                .emotion(session.getEmotion().stream().map(Emotion::getText).collect(Collectors.toList()))
                .emotion_cause(session.getCause().getText())
                .messages(MessageFormatF.fromSession(session))
                .build();
        MissionResponse responseF = getMission(requestF);
        session.setMission(responseF.getMission());
        sessionRepository.save(session);
        /*fast api와 연동*/
        return;
    }
    private MissionResponse getMission(MessageRequestF request){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MessageRequestF> requestEntity = new HttpEntity<>(request, headers);

        try{
            ResponseEntity<MissionResponse> res = restTemplate.exchange(endpoint_mission, HttpMethod.POST, requestEntity, MissionResponse.class);
            if(res.getBody()==null){
                throw new RuntimeException("fastApi로 부터 응답이 없습니다.");
            }
            return res.getBody();
        } catch(RestClientException e) {
            throw new CustomException(ErrorCode.NOT_FOUND_AI_SERVER);
        }
    }
    @Async
    @Transactional
    public void saveAdvice(Member host, Long sessionId){
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_SESSION));

        if(session.getHost()!=host){
            throw new CustomException(ErrorCode.DIFFERENT_USER_SESSION);
        }
        if(session.getLastEmotion() == null){
            throw new CustomException(ErrorCode.LAST_EMOTION_NOT_EXIST);
        }
        /*fast api와 연동*/
        MessageRequestF requestF = MessageRequestF.builder()
                .persona(session.getDuckType().getValue())
                .formal(session.getIsFormal())
                .emotion(session.getEmotion().stream().map(Emotion::getText).collect(Collectors.toList()))
                .emotion_cause(session.getCause().getText())
                .messages(MessageFormatF.fromSession(session))
                .build();
        AdviceResponse responseF = getAdvice(requestF);
        session.setAdvice(responseF.getAdvice());
        sessionRepository.save(session);
        /*fast api와 연동*/
        return ;
    }
    private AdviceResponse getAdvice(MessageRequestF request){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MessageRequestF> requestEntity = new HttpEntity<>(request, headers);
        try{
            ResponseEntity<AdviceResponse> res = restTemplate.exchange(endpoint_advice, HttpMethod.POST, requestEntity, AdviceResponse.class);
            if(res.getBody()==null){
                throw new RuntimeException("fastApi로 부터 응답이 없습니다.");
            }
            return res.getBody();
        } catch(RestClientException e) {
            throw new CustomException(ErrorCode.NOT_FOUND_AI_SERVER);
        }
    }
}
