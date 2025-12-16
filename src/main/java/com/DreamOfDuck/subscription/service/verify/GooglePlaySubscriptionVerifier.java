package com.DreamOfDuck.subscription.service.verify;

import com.DreamOfDuck.subscription.config.IapProperties;
import com.DreamOfDuck.subscription.entity.StorePlatform;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * IMPORTANT: This is a clean abstraction layer.
 * Replace the stub logic with real Google Play Developer API verification when you wire credentials.
 */
@Component
@RequiredArgsConstructor
public class GooglePlaySubscriptionVerifier implements StoreSubscriptionVerifier {
    private final IapProperties properties;

    @Override
    public StorePlatform supports() {
        return StorePlatform.GOOGLE;
    }

    @Override
    public VerificationResult verify(VerificationCommand command) {
        // Stub: validate shape only.
        if (!StringUtils.hasText(command.getPurchaseToken()) || !StringUtils.hasText(command.getProductId())) {
            return VerificationResult.builder().valid(false).rawResponse("missing_token_or_product").build();
        }

        // TODO: use properties.getGoogle().getServiceAccountJson() + properties.getGoogle().getPackageName()
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(30);
        String subId = StringUtils.hasText(command.getStoreSubscriptionId())
                ? command.getStoreSubscriptionId()
                : safeKey("google_token", command.getPurchaseToken());
        String txId = StringUtils.hasText(command.getStoreTransactionId())
                ? command.getStoreTransactionId()
                : subId;

        return VerificationResult.builder()
                .valid(true)
                .expiresAt(expiresAt)
                .autoRenewing(true)
                .storeSubscriptionId(subId)
                .storeTransactionId(txId)
                .rawResponse("stub_ok:pkg=" + properties.getGoogle().getPackageName())
                .build();
    }

    private String safeKey(String prefix, String payload) {
        String trimmed = payload.trim();
        int len = Math.min(trimmed.length(), 48);
        return prefix + ":" + trimmed.substring(0, len);
    }
}


