package com.DreamOfDuck.talk.service;

import com.DreamOfDuck.talk.dto.request.MessageRequest;
import com.DreamOfDuck.talk.dto.response.MessageFormat;
import com.DreamOfDuck.talk.dto.response.MessageResponse;
import com.DreamOfDuck.talk.entity.Message;
import com.DreamOfDuck.talk.entity.Session;
import com.DreamOfDuck.talk.entity.Talker;
import com.DreamOfDuck.talk.repository.MessageRepository;
import com.DreamOfDuck.talk.repository.SessionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly=true)
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final SessionRepository sessionRepository;

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
        MessageResponse res = MessageResponse.builder()
                .request(MessageFormat.from(user))
                .response(MessageFormat.builder()
                        .messageId(0L)
                        .sessionId(session.getId())
                        .talker(session.getDuckType().getValue())
                        .content("꽥")
                        .time(user.getCreatedAt())
                        .build())
                .build();
        return res;
    }
    public MessageFormat findById(Long messageId) {
        return MessageFormat.from(messageRepository.findById(messageId).orElseThrow(EntityNotFoundException::new));
    }
    @Transactional
    public void delete(Long messageId){
        messageRepository.deleteById(messageId);
    }
}
