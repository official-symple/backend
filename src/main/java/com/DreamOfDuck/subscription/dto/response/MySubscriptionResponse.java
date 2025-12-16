package com.DreamOfDuck.subscription.dto.response;

import com.DreamOfDuck.account.entity.Subscribe;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MySubscriptionResponse {
    private Subscribe plan;
    private boolean premiumActive;
    private LocalDateTime expiresAt;

    private boolean unlimitedTalk;
    private boolean adFree;
    private boolean dailyItem;
}


