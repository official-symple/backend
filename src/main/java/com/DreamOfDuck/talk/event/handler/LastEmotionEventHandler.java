package com.DreamOfDuck.talk.event.handler;

import com.DreamOfDuck.talk.event.LastEmotionCreatedEvent;
import com.DreamOfDuck.talk.service.LastEmotionAsyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * @deprecated 더 이상 사용하지 않음
 * AI 서버 콜백 방식으로 변경되어 이벤트 핸들러 불필요
 * SessionService에서 직접 AI 서버에 요청을 보내고,
 * AI 서버가 처리 완료 시 AiCallbackController로 결과를 전송함
 */
@Deprecated
@Component
@Slf4j
@RequiredArgsConstructor
public class LastEmotionEventHandler {
    private final LastEmotionAsyncService lastEmotionAsyncService;

    // @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    // public void handleMission(LastEmotionCreatedEvent event) {
    //     lastEmotionAsyncService.requestSummaryAndMission(event.getMember(), event.getSessionId());
    //     lastEmotionAsyncService.requestAdvice(event.getMember(), event.getSessionId());
    // }
}
