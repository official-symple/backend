package com.DreamOfDuck.talk.service;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.global.exception.CustomException;
import com.DreamOfDuck.global.exception.ErrorCode;
import com.DreamOfDuck.talk.dto.request.AdviceRequestF;
import com.DreamOfDuck.talk.dto.request.MessageFormatF;
import com.DreamOfDuck.talk.dto.request.MessageRequestF;
import com.DreamOfDuck.talk.dto.request.MissionRequestF;
import com.DreamOfDuck.talk.dto.response.AdviceResponse;
import com.DreamOfDuck.talk.dto.response.MissionResponse;
import com.DreamOfDuck.talk.dto.response.SummaryResponseF;
import com.DreamOfDuck.talk.entity.Emotion;
import com.DreamOfDuck.talk.entity.Session;
import com.DreamOfDuck.talk.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    @Async
    public void saveReportAndMission(Member host, Long sessionId){
        Session session = sessionRepository.findById(sessionId).orElse(null);

        MessageRequestF requestF = MessageRequestF.builder()
                .persona(session.getDuckType().getValue())
                .language(host.getLanguage()==null?"KOR":host.getLanguage().toString().toLowerCase())
                .formal(session.getIsFormal())
                .emotion(session.getEmotion().stream().map(Emotion::getText).collect(Collectors.toList()))
                .emotion_cause(session.getCause().getText())
                .messages(MessageFormatF.fromSession(session))
                .build();
        SummaryResponseF responseF = getSummary(requestF);
        session.setProblem(responseF.getProblem());
        session.setSolutions(responseF.getSolutions());

        MissionRequestF requestF2 = MissionRequestF.builder()
                .persona(session.getDuckType().getValue())
                .language(host.getLanguage()==null?"KOR":host.getLanguage().toString().toLowerCase())
                .formal(session.getIsFormal())
                .emotion(session.getEmotion().stream().map(Emotion::getText).collect(Collectors.toList()))
                .emotion_cause(session.getCause().getText())
                .summary(responseF.getProblem())
                .nickname(session.getHost().getNickname())
                .build();
        MissionResponse responseF2 = getMission(requestF2);
        System.out.println("mission 받기 : "+responseF2.getMission());
        session.setMission(responseF2.getMission());

        AdviceRequestF requestF3 = AdviceRequestF.builder()
                .messages(requestF.getMessages())
                .language(host.getLanguage()==null?"KOR":host.getLanguage().toString().toLowerCase())
                .persona(session.getDuckType().getValue())
                .formal(session.getIsFormal())
                .nickname(session.getHost().getNickname())
                .build();
        AdviceResponse responseF3 = getAdvice(requestF3);
        System.out.println("advice 받기 : "+responseF3.getAdvice());
        session.setAdvice(responseF3.getAdvice());
        sessionRepository.save(session);
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

    private MissionResponse getMission(MissionRequestF request){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MissionRequestF> requestEntity = new HttpEntity<>(request, headers);

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

    private AdviceResponse getAdvice(AdviceRequestF request){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AdviceRequestF> requestEntity = new HttpEntity<>(request, headers);
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
