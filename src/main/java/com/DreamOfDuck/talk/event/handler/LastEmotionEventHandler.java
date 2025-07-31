package com.DreamOfDuck.talk.event.handler;

import com.DreamOfDuck.account.dto.request.HeartRequest;
import com.DreamOfDuck.account.service.MemberService;
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
    private final MemberService memberService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMission(LastEmotionCreatedEvent event) {
        asyncService.saveReportAndMission(event.getSessionId());
        HeartRequest heartRequest = new HeartRequest();
        heartRequest.setHeart(event.getMember().getHeart()+2);
        memberService.updateHeart(event.getMember(), heartRequest);
    }

}
