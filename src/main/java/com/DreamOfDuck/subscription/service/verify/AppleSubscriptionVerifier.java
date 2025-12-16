package com.DreamOfDuck.subscription.service.verify;

import com.DreamOfDuck.subscription.config.IapProperties;
import com.DreamOfDuck.subscription.entity.StorePlatform;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * IMPORTANT: This is a clean abstraction layer.
 * Replace the stub logic with real App Store receipt / S2S verification when you wire secrets and Apple endpoints.
 */
@Component
@RequiredArgsConstructor
public class AppleSubscriptionVerifier implements StoreSubscriptionVerifier {
    private final IapProperties properties;

    @Override
    public StorePlatform supports() {
        return StorePlatform.APPLE;
    }

    @Override
    public VerificationResult verify(VerificationCommand command) {
        // Stub: validate shape only.
        if (!StringUtils.hasText(command.getReceiptData())) {
            return VerificationResult.builder().valid(false).rawResponse("missing_receipt").build();
        }

        // TODO: use properties.getApple().getSharedSecret() + Apple verification endpoints.
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(30);
        String subId = StringUtils.hasText(command.getStoreSubscriptionId())
                ? command.getStoreSubscriptionId()
                : safeKey("apple_receipt", command.getReceiptData());
        String txId = StringUtils.hasText(command.getStoreTransactionId())
                ? command.getStoreTransactionId()
                : subId;

        return VerificationResult.builder()
                .valid(true)
                .expiresAt(expiresAt)
                .autoRenewing(true)
                .storeSubscriptionId(subId)
                .storeTransactionId(txId)
                .rawResponse("stub_ok:sandbox=" + properties.getApple().isSandbox())
                .build();
    }

    private String safeKey(String prefix, String payload) {
        String trimmed = payload.trim();
        int len = Math.min(trimmed.length(), 48);
        return prefix + ":" + trimmed.substring(0, len);
    }
}


