package com.DreamOfDuck.subscription.dto.response;

import com.DreamOfDuck.account.entity.Subscribe;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class VerifySubscriptionResponse {
    private boolean success;
    private Subscribe plan;
    private LocalDateTime expiresAt;
}


