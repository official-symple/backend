package com.DreamOfDuck.talk.service;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.global.exception.CustomException;
import com.DreamOfDuck.global.exception.ErrorCode;
import com.DreamOfDuck.talk.dto.request.MessageFormatF;
import com.DreamOfDuck.talk.dto.request.MessageRequestF;
import com.DreamOfDuck.talk.dto.request.SessionCreateRequest;
import com.DreamOfDuck.talk.dto.request.SessionUpdateRequest;
import com.DreamOfDuck.talk.dto.response.*;
import com.DreamOfDuck.talk.entity.*;
import com.DreamOfDuck.talk.repository.InterviewRepository;
import com.DreamOfDuck.talk.repository.SessionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly=true)
@RequiredArgsConstructor
public class SessionService {
    private final RestTemplate restTemplate;
    private final SessionRepository sessionRepository;
    private final InterviewRepository interviewRepository;
    @Value("${fastApi.summary.endpoint}")
    private String endpoint_summary;
    @Value("${fastApi.mission.endpoint}")
    private String endpoint_mission;
    @Value("${fastApi.advice.endpoint}")
    private String endpoint_advice;

    @Transactional
    public SessionResponse save(Member host, SessionCreateRequest request){
        Session session = Session.builder()
                .duckType(Talker.fromValue(request.getDuckType()))
                .isFormal(request.getIsFormal())
                .emotion(request.getEmotion().stream()
                        .map(Emotion::fromId)
                        .collect(Collectors.toList()))
                .cause(Cause.fromId(request.getCause()))
                .build();
        session.addHost(host);
        sessionRepository.save(session);
        return SessionResponse.from(session);
    }
    @Transactional
    public SessionResponse update(Member host, SessionUpdateRequest request){
        Session session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_SESSION));

        if(session.getHost()!=host){
            throw new CustomException(ErrorCode.DIFFERENT_USER_SESSION);
        }

        session.setLastEmotion(LastEmotion.fromId(request.getLastEmotion()));
        if(session.getLastEmotion() == LastEmotion.OPTION4){
            if(request.getInputField() ==null || request.getInputField().isEmpty())  throw new CustomException(ErrorCode.EMPTY_INPUT_FIELD);
        }

        session.setInputField(request.getInputField());
        return SessionResponse.from(session);
    }
    public SessionResponse findById(Member host, Long sessionId){
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_SESSION));

        if(session.getHost()!=host){
            throw new CustomException(ErrorCode.DIFFERENT_USER_SESSION);
        }
        SessionResponse sessionResponse = SessionResponse.from(session);
        return SessionResponse.from(session);
    }
    @Transactional
    public void delete(Member host, Long sessionId){
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_SESSION));

        if(session.getHost()!=host){
            throw new CustomException(ErrorCode.DIFFERENT_USER_SESSION);
        }
        sessionRepository.deleteById(sessionId);
    }
    public SessionResponseList findByUser(Member host){
        List<Session> sessionList = sessionRepository.findByHost(host);
        SessionResponseList res = new SessionResponseList();
        res.setSessions(sessionList.stream().map(SessionResponse::from).collect(Collectors.toList()));
        res.setIsInterview(interviewRepository.existsByHost(host));
        return res;
    }

    @Transactional
    public ReportResponse saveSolution(Member host, Long sessionId){
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
        return ReportResponse.from(session);
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
    @Transactional
    public MissionResponse saveMission(Member host, Long sessionId){
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
        return responseF;
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
    @Transactional
    public AdviceResponse saveAdvice(Member host, Long sessionId){
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
        return responseF;
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
