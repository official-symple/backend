package com.DreamOfDuck.talk.event.handler;

import com.DreamOfDuck.talk.event.LastEmotionCreatedEvent;
import com.DreamOfDuck.talk.service.AsyncService;
import com.DreamOfDuck.talk.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class LastEmotionEventHandler {
    private final AsyncService asyncService;
    private final SessionService sessionService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMission(LastEmotionCreatedEvent event) {
        asyncService.saveReportAndMission(event.getSessionId());
    }

}
