package com.DreamOfDuck.subscription.service.verify;

import com.DreamOfDuck.subscription.entity.StorePlatform;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class VerifierResolver {
    private final Map<StorePlatform, StoreSubscriptionVerifier> verifiers = new EnumMap<>(StorePlatform.class);

    public VerifierResolver(List<StoreSubscriptionVerifier> verifierList) {
        for (StoreSubscriptionVerifier verifier : verifierList) {
            verifiers.put(verifier.supports(), verifier);
        }
    }

    public StoreSubscriptionVerifier get(StorePlatform platform) {
        return verifiers.get(platform);
    }
}


