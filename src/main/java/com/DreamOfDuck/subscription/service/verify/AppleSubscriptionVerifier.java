package com.DreamOfDuck.subscription.service.verify;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.DreamOfDuck.subscription.config.IapProperties;
import com.DreamOfDuck.subscription.entity.StorePlatformEnum;

import lombok.RequiredArgsConstructor;

/**
 * IMPORTANT: This is a clean abstraction layer.
 * Replace the stub logic with real App Store receipt / S2S verification when you wire secrets and Apple endpoints.
 */
@Component
@RequiredArgsConstructor
public class AppleSubscriptionVerifier implements StoreSubscriptionVerifier {
    private final IapProperties properties;

    @Override
    public StorePlatformEnum supports() {
        return StorePlatformEnum.APPLE;
    }

    @Override
    public VerificationResult verify(VerificationCommand command) {
        if (!StringUtils.hasText(command.getReceiptData())) {
            return VerificationResult.builder().valid(false).rawResponse("missing_receipt").build();
        }

        // TODO: Implement real Apple StoreKit 2 verification
        // Parse receipt data, verify with Apple's server, extract transaction info
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusDays(30);
        String txId = StringUtils.hasText(command.getStoreTransactionId())
                ? command.getStoreTransactionId()
                : safeKey("apple_tx", command.getReceiptData());
        String origTxId = txId; // In real implementation, extract from receipt

        return VerificationResult.builder()
                .valid(true)
                .startedAt(now)
                .expiresAt(expiresAt)
                .autoRenewing(true)
                .isTrialPeriod(false) // Extract from receipt in real implementation
                .storeTransactionId(txId)
                .originalTransactionId(origTxId)
                .rawResponse("stub_ok:sandbox=" + properties.getApple().isSandbox())
                .build();
    }

    private String safeKey(String prefix, String payload) {
        String trimmed = payload.trim();
        int len = Math.min(trimmed.length(), 48);
        return prefix + ":" + trimmed.substring(0, len);
    }
}
