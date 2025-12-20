package com.DreamOfDuck.subscription.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MySubscriptionResponse {
    private boolean isPremium;
    private LocalDateTime startedAt;
    private LocalDateTime expiresAt;
    private Boolean isTrialPeriod;
}
