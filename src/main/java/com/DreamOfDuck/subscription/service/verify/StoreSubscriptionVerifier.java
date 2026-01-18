package com.DreamOfDuck.subscription.service.verify;

import com.DreamOfDuck.subscription.dto.request.VerifySubscriptionRequest;
import com.DreamOfDuck.subscription.entity.StorePlatformEnum;

public interface StoreSubscriptionVerifier {
    StorePlatformEnum supports();
    VerificationResult verify(VerifySubscriptionRequest request);
}


