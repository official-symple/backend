package com.DreamOfDuck.subscription.service.verify;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class VerificationResult {
    private boolean valid;
    private LocalDateTime startedAt;
    private LocalDateTime expiresAt;
    private Boolean autoRenewing;
    private Boolean isTrialPeriod;
    private String storeTransactionId;
    private String originalTransactionId;
    private String rawResponse;
}
