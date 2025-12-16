package com.DreamOfDuck.subscription.service.verify;

import com.DreamOfDuck.subscription.entity.StorePlatform;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VerificationCommand {
    private StorePlatform platform;
    private String productId;

    // Apple
    private String receiptData;

    // Google
    private String purchaseToken;

    // Optional client-provided ids (used as hints / logging)
    private String storeTransactionId;
    private String storeSubscriptionId;
}


