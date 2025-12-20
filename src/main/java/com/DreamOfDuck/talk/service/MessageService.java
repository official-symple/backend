package com.DreamOfDuck.talk.service;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.global.exception.CustomException;
import com.DreamOfDuck.global.exception.ErrorCode;
import com.DreamOfDuck.talk.dto.request.MessageCreateRequest;
import com.DreamOfDuck.talk.dto.request.MessageFormatF;
import com.DreamOfDuck.talk.dto.request.MessageRequestF;
import com.DreamOfDuck.talk.dto.response.MessageFormat;
import com.DreamOfDuck.talk.dto.response.MessageFormatList;
import com.DreamOfDuck.talk.dto.response.MessageResponse;
import com.DreamOfDuck.talk.dto.response.MessageResponseF;
import com.DreamOfDuck.talk.dto.response.MessageResponseList;
import com.DreamOfDuck.talk.dto.response.MessageResponseListF;
import com.DreamOfDuck.talk.entity.Emotion;
import com.DreamOfDuck.talk.entity.Message;
import com.DreamOfDuck.talk.entity.Session;
import com.DreamOfDuck.talk.entity.Talker;
import com.DreamOfDuck.talk.repository.MessageRepository;
import com.DreamOfDuck.talk.repository.SessionRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly=true)
@RequiredArgsConstructor
public class MessageService {
    private final RestTemplate restTemplate;
    private final MessageRepository messageRepository;
    private final SessionRepository sessionRepository;
    @Value("${fastApi.talk.endpoint}")
    private String endpoint;
    @Value("${fastApi.newtalk.endpoint}")
    private String newendpoint;

    @Transactional
    public MessageResponse save(Member host, MessageCreateRequest request) {
        Session session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_SESSION));

        if(session.getHost()!=host){
            throw new CustomException(ErrorCode.DIFFERENT_USER_SESSION);
        }
        //유저 메시지 저장
        Message user = Message.builder()
                .talker(Talker.USER)
                .content(request.getContent())
                .build();
        user.addSession(session);
        messageRepository.save(user);

        /*fast api와 연동*/
        MessageRequestF requestF = MessageRequestF.builder()
                .persona(session.getDuckType().getValue())
                .formal(session.getIsFormal())
                .language(host.getLanguage()==null?"kor":host.getLanguage().toString().toLowerCase())
                .emotion(session.getEmotion().stream().map(Emotion::getText).collect(Collectors.toList()))
                .emotion_cause(session.getCause().getText())
                .messages(MessageFormatF.fromSession(session))
                .nickname(host.getNickname())
                .build();
        if(request.getContent()==null){
            requestF.setMessages(null);
        }
        MessageResponseF responseF = getMessageFromDuck(requestF);
        Message duck = Message.builder()
                .talker(session.getDuckType())
                .content(responseF.getContent())
                .build();
        duck.addSession(session);
        messageRepository.save(duck);
        /*fast api와 연동*/

        MessageResponse res = MessageResponse.builder()
                .request(MessageFormat.from(user))
                .response(MessageFormat.from(duck))
                .build();
        return res;
    }
    private MessageResponseF getMessageFromDuck(MessageRequestF request){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MessageRequestF> requestEntity = new HttpEntity<>(request, headers);
        try{
            ResponseEntity<MessageResponseF> res = restTemplate.exchange(endpoint, HttpMethod.POST, requestEntity, MessageResponseF.class);
            if(res.getBody()==null){
                throw new RuntimeException("fastApi로 부터 응답이 없습니다.");
            }
            return res.getBody();
        } catch(RestClientException e) {
            throw new CustomException(ErrorCode.NOT_FOUND_AI_SERVER);
        }
    }
    /*new chat api*/
    @Transactional
    public MessageResponseList saveNew(Member host, MessageCreateRequest request) {
        Session session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_SESSION));

        if(session.getHost()!=host){
            throw new CustomException(ErrorCode.DIFFERENT_USER_SESSION);
        }
        //유저 메시지 저장
        Message user = Message.builder()
                .talker(Talker.USER)
                .content(request.getContent())
                .build();
        user.addSession(session);
        messageRepository.save(user);

        /*fast api와 연동*/
        MessageRequestF requestF = MessageRequestF.builder()
                .persona(session.getDuckType().getValue())
                .formal(session.getIsFormal())
                .language(host.getLanguage()==null?"kor":host.getLanguage().toString().toLowerCase())
                .emotion(session.getEmotion().stream().map(Emotion::getText).collect(Collectors.toList()))
                .emotion_cause(session.getCause().getText())
                .messages(MessageFormatF.fromSession(session))
                .nickname(host.getNickname())
                .build();
        if(request.getContent()==null){
            requestF.setMessages(null);
        }
        MessageResponseListF responseF = getMessageFromDuckNew(requestF);
        Message duck = Message.builder()
                .talker(session.getDuckType())
                .contents(responseF.getContent())
                .build();
        duck.addSession(session);
        messageRepository.save(duck);
        /*fast api와 연동*/

        MessageResponseList res = MessageResponseList.builder()
                .request(MessageFormatList.from(user))
                .response(MessageFormatList.from(duck))
                .build();
        return res;
    }
    private MessageResponseListF getMessageFromDuckNew(MessageRequestF request){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MessageRequestF> requestEntity = new HttpEntity<>(request, headers);
        try{
            ResponseEntity<MessageResponseListF> res = restTemplate.exchange(newendpoint, HttpMethod.POST, requestEntity, MessageResponseListF.class);
            if(res.getBody()==null){
                throw new RuntimeException("fastApi로 부터 응답이 없습니다.");
            }
            return res.getBody();
        } catch(RestClientException e) {
            throw new CustomException(ErrorCode.NOT_FOUND_AI_SERVER);
        }
    }


    public MessageFormat findById(Member host, Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MESSAGE));
        if(message.getSession().getHost()!=host){
            throw new CustomException(ErrorCode.DIFFERENT_USER_SESSION);
        }
        return MessageFormat.from(message);
    }
    @Transactional
    public void delete(Member host, Long messageId){
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_MESSAGE));
        if(message.getSession().getHost()!=host){
            throw new CustomException(ErrorCode.DIFFERENT_USER_SESSION);
        }
        messageRepository.deleteById(messageId);
    }
}
