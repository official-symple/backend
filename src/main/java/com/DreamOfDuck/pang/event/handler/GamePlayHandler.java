package com.DreamOfDuck.pang.event.handler;

import com.DreamOfDuck.pang.event.GamePlayEvent;
import com.DreamOfDuck.pang.service.ScoreAsyncService;
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
public class GamePlayHandler {
    private final ScoreAsyncService scoreAsyncService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRedisInfo(GamePlayEvent event) {
        scoreAsyncService.updateTotalPlayer(event.getMember());
        scoreAsyncService.updateWorldRecord(event.getScore());
    }
}
