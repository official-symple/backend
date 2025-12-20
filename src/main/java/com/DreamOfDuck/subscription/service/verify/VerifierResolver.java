package com.DreamOfDuck.subscription.service.verify;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.DreamOfDuck.subscription.entity.StorePlatformEnum;

@Component
public class VerifierResolver {
    private final Map<StorePlatformEnum, StoreSubscriptionVerifier> verifiers = new EnumMap<>(StorePlatformEnum.class);

    public VerifierResolver(List<StoreSubscriptionVerifier> verifierList) {
        for (StoreSubscriptionVerifier verifier : verifierList) {
            verifiers.put(verifier.supports(), verifier);
        }
    }

    public StoreSubscriptionVerifier get(StorePlatformEnum platform) {
        return verifiers.get(platform);
    }
}


