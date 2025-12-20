package com.DreamOfDuck.talk.service;


import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.entity.Role;
import com.DreamOfDuck.global.exception.CustomException;
import com.DreamOfDuck.global.exception.ErrorCode;
import com.DreamOfDuck.goods.dto.request.FeatherRequest;
import com.DreamOfDuck.goods.event.AttendanceCreatedEvent;
import com.DreamOfDuck.goods.service.GoodsService;
import com.DreamOfDuck.talk.dto.request.FeedbackRequest;
import com.DreamOfDuck.talk.dto.request.SessionCreateRequest;
import com.DreamOfDuck.talk.dto.request.SessionUpdateRequest;
import com.DreamOfDuck.talk.dto.response.AdviceResponse;
import com.DreamOfDuck.talk.dto.response.FeedbackResponse;
import com.DreamOfDuck.talk.dto.response.MissionResponse;
import com.DreamOfDuck.talk.dto.response.ReportResponse;
import com.DreamOfDuck.talk.dto.response.SessionResponse;
import com.DreamOfDuck.talk.dto.response.SessionResponseList;
import com.DreamOfDuck.talk.entity.Cause;
import com.DreamOfDuck.talk.entity.Emotion;
import com.DreamOfDuck.talk.entity.LastEmotion;
import com.DreamOfDuck.talk.entity.Session;
import com.DreamOfDuck.talk.entity.Talker;
import com.DreamOfDuck.talk.event.LastEmotionCreatedEvent;
import com.DreamOfDuck.talk.repository.SessionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly=true)
@RequiredArgsConstructor
@Slf4j
public class SessionService {
    private final SessionRepository sessionRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final GoodsService goodsService;
    @Transactional
    public SessionResponse save(Member host, SessionCreateRequest request){
        Integer cntTalk=host.getCntTalk()==null?2:host.getCntTalk();
        if(cntTalk<=0 && host.getRole() != Role.ROLE_PREMIUM) throw new CustomException(ErrorCode.NOT_ENOUGH_CNT_TALK);
        goodsService.minusCntTalk(host);
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
        sessionRepository.save(session);
        host.setHeart(host.getHeart()+2);
        log.info("create event of last emotion");
        eventPublisher.publishEvent(new LastEmotionCreatedEvent(session.getId(), host));
        ZoneId userZone = ZoneId.of(host.getLocation()==null?"Asia/Seoul":host.getLocation());
        LocalDate now = LocalDate.now(userZone);
        eventPublisher.publishEvent(new AttendanceCreatedEvent(host.getEmail(), now));
        return SessionResponse.from(session);
    }
    @Transactional
    public void deleteEmotion(Member host, Long sessionId){
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_SESSION));

        if(session.getHost()!=host){
            throw new CustomException(ErrorCode.DIFFERENT_USER_SESSION);
        }

        session.setLastEmotion(null);
        session.setMission(null);
        session.setProblem(null);
        session.setSolutions(null);
        session.setAdvice(null);
        return;
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
        res.setSessions(sessionList.stream()
                .filter(session -> session.getLastEmotion() !=null)
                .sorted(Comparator.comparing(Session::getCreatedAt).reversed())
                .map(SessionResponse::from)
                .collect(Collectors.toList()));
        res.setIsInterview(host.getInterview() != null);
        return res;
    }

    public MissionResponse getMissionById(Member host, Long sessionId){
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_SESSION));

        if(session.getHost()!=host){
            throw new CustomException(ErrorCode.DIFFERENT_USER_SESSION);
        }
        if(session.getLastEmotion() == null){
            throw new CustomException(ErrorCode.LAST_EMOTION_NOT_EXIST);
        }
        if(session.getMission()==null || session.getMission().isEmpty()){
            throw new CustomException(ErrorCode.MISSION_ING);
        }
        return MissionResponse.from(session);
    }
    public AdviceResponse getAdviceById(Member host, Long sessionId){
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_SESSION));

        if(session.getHost()!=host){
            throw new CustomException(ErrorCode.DIFFERENT_USER_SESSION);
        }
        if(session.getLastEmotion() == null){
            throw new CustomException(ErrorCode.LAST_EMOTION_NOT_EXIST);
        }
        if(session.getAdvice()==null || session.getAdvice().isEmpty()){
            throw new CustomException(ErrorCode.ADVICE_ING);
        }
        return AdviceResponse.from(session);
    }
    public ReportResponse getReportById(Member host, Long sessionId){
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_SESSION));

        if(session.getHost()!=host){
            throw new CustomException(ErrorCode.DIFFERENT_USER_SESSION);
        }
        if(session.getLastEmotion() == null){
            throw new CustomException(ErrorCode.LAST_EMOTION_NOT_EXIST);
        }
        if(session.getProblem()==null || session.getProblem().isEmpty() || session.getSolutions().isEmpty()){
            throw new CustomException(ErrorCode.SUMMARY_ING);
        }
        FeatherRequest request = new FeatherRequest();
        request.setFeather(50);
        goodsService.updateFeather(host, request);
        return ReportResponse.from(session);
    }

    @Transactional
    public FeedbackResponse saveFeedback(Member host, FeedbackRequest request, Long id){
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_SESSION));

        if(session.getHost()!=host){
            throw new CustomException(ErrorCode.DIFFERENT_USER_SESSION);
        }
        session.setFeedback(request.getFeedback());
        return FeedbackResponse.from(session);

    }
}
