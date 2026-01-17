package com.DreamOfDuck.subscription.dto.response;

import java.time.LocalDateTime;

import com.DreamOfDuck.account.entity.Role;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VerifySubscriptionResponse {
    private boolean success;
    private LocalDateTime startedAt;
    private LocalDateTime expiresAt;
    private Role role;
    private Boolean isTrialPeriod;
}
