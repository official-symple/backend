package com.DreamOfDuck.subscription.dto.request;

import com.DreamOfDuck.subscription.entity.StorePlatformEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifySubscriptionRequest {
    private StorePlatformEnum platform;
    private String productId;

    // Apple
    private String receiptData;

    // Google
    private String purchaseToken;

    // Optional
    private String storeTransactionId;
}
