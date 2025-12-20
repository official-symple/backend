package com.DreamOfDuck.subscription.service.verify;

import com.DreamOfDuck.subscription.entity.StorePlatformEnum;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VerificationCommand {
    private StorePlatformEnum platform;
    private String productId;

    // Apple
    private String receiptData;

    // Google
    private String purchaseToken;

    // Optional client-provided transaction id
    private String storeTransactionId;
}
