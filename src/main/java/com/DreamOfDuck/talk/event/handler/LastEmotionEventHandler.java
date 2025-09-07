package com.DreamOfDuck.talk.event.handler;

import com.DreamOfDuck.account.dto.request.HeartRequest;
import com.DreamOfDuck.talk.event.LastEmotionCreatedEvent;
import com.DreamOfDuck.talk.service.LastEmotionAsyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class LastEmotionEventHandler {
    private final LastEmotionAsyncService lastEmotionAsyncService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMission(LastEmotionCreatedEvent event) {
        lastEmotionAsyncService.saveReportAndMission(event.getMember(), event.getSessionId());
        lastEmotionAsyncService.saveAdvice(event.getMember(), event.getSessionId());
    }
}
