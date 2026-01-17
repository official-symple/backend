package com.DreamOfDuck.subscription.service.verify;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.DreamOfDuck.subscription.config.IapProperties;
import com.DreamOfDuck.subscription.dto.request.VerifySubscriptionRequest;
import com.DreamOfDuck.subscription.entity.StorePlatformEnum;

import lombok.RequiredArgsConstructor;

/**
 * IMPORTANT: This is a clean abstraction layer.
 * Replace the stub logic with real Google Play Developer API verification when you wire credentials.
 */
@Component
@RequiredArgsConstructor
public class GooglePlaySubscriptionVerifier implements StoreSubscriptionVerifier {
    private final IapProperties properties;

    @Override
    public StorePlatformEnum supports() {
        return StorePlatformEnum.GOOGLE;
    }

    @Override
    public VerificationResult verify(VerifySubscriptionRequest request) {
        if (!StringUtils.hasText(request.getPurchaseToken()) || !StringUtils.hasText(request.getProductId())) {
            return VerificationResult.builder().valid(false).rawResponse("missing_token_or_product").build();
        }

        // TODO: Implement real Google Play Developer API verification
        // Use purchaseToken to verify with Google, extract subscription info
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusDays(30);
        String txId = StringUtils.hasText(request.getStoreTransactionId())
                ? request.getStoreTransactionId()
                : safeKey("google_tx", request.getPurchaseToken());
        String origTxId = txId; // In real implementation, extract from Google response

        return VerificationResult.builder()
                .valid(true)
                .startedAt(now)
                .expiresAt(expiresAt)
                .autoRenewing(true)
                .isTrialPeriod(false) // Extract from Google response in real implementation
                .storeTransactionId(txId)
                .originalTransactionId(origTxId)
                .rawResponse("stub_ok:pkg=" + properties.getGoogle().getPackageName())
                .build();
    }

    private String safeKey(String prefix, String payload) {
        String trimmed = payload.trim();
        int len = Math.min(trimmed.length(), 48);
        return prefix + ":" + trimmed.substring(0, len);
    }
}
