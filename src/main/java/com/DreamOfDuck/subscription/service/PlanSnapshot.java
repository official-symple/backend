package com.DreamOfDuck.subscription.service;

import com.DreamOfDuck.account.entity.Subscribe;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PlanSnapshot {
    private Subscribe plan;
    private boolean unlimitedTalk;
    private boolean adFree;
    private boolean dailyItem;
    private boolean premiumActive;
    private LocalDateTime expiresAt;
}


