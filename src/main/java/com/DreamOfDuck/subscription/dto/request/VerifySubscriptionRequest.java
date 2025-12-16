package com.DreamOfDuck.subscription.dto.request;

import com.DreamOfDuck.subscription.entity.StorePlatform;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifySubscriptionRequest {
    private StorePlatform platform;
    private String productId;

    // Apple
    private String receiptData;

    // Google
    private String purchaseToken;

    // Optional hints
    private String storeTransactionId;
    private String storeSubscriptionId;
}


