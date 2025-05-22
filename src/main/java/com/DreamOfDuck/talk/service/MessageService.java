package com.DreamOfDuck.talk.service;

import com.DreamOfDuck.talk.dto.request.MessageRequest;
import com.DreamOfDuck.talk.dto.request.MessageRequestF;
import com.DreamOfDuck.talk.dto.response.MessageFormat;
import com.DreamOfDuck.talk.dto.response.MessageResponse;
import com.DreamOfDuck.talk.dto.response.MessageResponseF;
import com.DreamOfDuck.talk.entity.Message;
import com.DreamOfDuck.talk.entity.Session;
import com.DreamOfDuck.talk.entity.Talker;
import com.DreamOfDuck.talk.repository.MessageRepository;
import com.DreamOfDuck.talk.repository.SessionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional(readOnly=true)
@RequiredArgsConstructor
public class MessageService {
    private final RestTemplate restTemplate;
    private final MessageRepository messageRepository;
    private final SessionRepository sessionRepository;
    @Value("${fastApi.talk.endpoint}")
    private String endpoint;

    @Transactional
    public MessageResponse save(MessageRequest request) {
        Session session = sessionRepository.findById(request.getSessionId()).orElseThrow(EntityNotFoundException::new);
        //유저 메시지 저장
        Message user = Message.builder()
                .talker(Talker.USER)
                .content(request.getContent())
                .build();
        user.addConversation(session);
        messageRepository.save(user);

        /*fast api와 연동*/
        MessageRequestF requestF = MessageRequestF.builder()
                .duckType(session.getDuckType().getValue())
                .content(request.getContent())
                .build();
        MessageResponseF responseF = getMessageFromDuck(requestF);
        Message duck = Message.builder()
                .talker(session.getDuckType())
                .content(responseF.getContent())
                .build();
        duck.addConversation(session);
        messageRepository.save(duck);
        /*fast api와 연동*/
        /*목업 데이터*/
//        MessageResponse res = MessageResponse.builder()
//                .request(MessageFormat.from(user))
//                .response(MessageFormat.builder()
//                        .messageId(0L)
//                        .sessionId(session.getId())
//                        .talker(session.getDuckType().getValue())
//                        .content("꽥")
//                        .time(user.getCreatedAt())
//                        .build())
//                .build();
        /*목업 데이터*/
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
                throw new RuntimeException("fastApi로부터 응답이 없습니다.");
            }
            return res.getBody();
        } catch(RestClientException e) {
            throw new RuntimeException("fastApi와 통신 실패");
        }
    }
    public MessageFormat findById(Long messageId) {
        return MessageFormat.from(messageRepository.findById(messageId).orElseThrow(EntityNotFoundException::new));
    }
    @Transactional
    public void delete(Long messageId){
        messageRepository.deleteById(messageId);
    }
}
