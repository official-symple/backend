package com.DreamOfDuck.subscription.service.verify;

import com.DreamOfDuck.subscription.entity.StorePlatform;

public interface StoreSubscriptionVerifier {
    StorePlatform supports();
    VerificationResult verify(VerificationCommand command);
}


