package com.DreamOfDuck.talk.service;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.global.exception.CustomException;
import com.DreamOfDuck.global.exception.ErrorCode;
import com.DreamOfDuck.talk.dto.request.SessionCreateRequest;
import com.DreamOfDuck.talk.dto.request.SessionUpdateRequest;
import com.DreamOfDuck.talk.dto.response.SessionResponse;
import com.DreamOfDuck.talk.entity.*;
import com.DreamOfDuck.talk.repository.SessionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@Transactional(readOnly=true)
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;

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
        Session session = sessionRepository.findById(request.getSessionId()).orElseThrow(EntityNotFoundException::new);
       if(session.getHost()!=host){
           throw new CustomException(ErrorCode.DIFFERENT_USER);
       }

        session.setLast_emotion(LastEmotion.fromId(request.getLast_emotion()));
        if(session.getLast_emotion() == LastEmotion.OPTION4){
            if(request.getInput_field() ==null || request.getInput_field().isEmpty())  throw new CustomException(ErrorCode.EMPTY_INPUT_FIELD);
        }

        session.setInput_field(request.getInput_field());
        return SessionResponse.from(session);
    }
    public SessionResponse findById(Member host, Long sessionId){
        Session session = sessionRepository.findById(sessionId).orElseThrow(EntityNotFoundException::new);
        if(session.getHost()!=host){
            throw new CustomException(ErrorCode.DIFFERENT_USER);
        }
        return SessionResponse.from(session);
    }
    @Transactional
    public void delete(Member host, Long sessionId){
        Session session = sessionRepository.findById(sessionId).orElseThrow(EntityNotFoundException::new);
        if(session.getHost()!=host){
            throw new CustomException(ErrorCode.DIFFERENT_USER);
        }
        sessionRepository.deleteById(sessionId);
    }
}
