package com.DreamOfDuck.subscription.service.verify;

import com.DreamOfDuck.subscription.entity.StorePlatformEnum;

public interface StoreSubscriptionVerifier {
    StorePlatformEnum supports();
    VerificationResult verify(VerificationCommand command);
}


