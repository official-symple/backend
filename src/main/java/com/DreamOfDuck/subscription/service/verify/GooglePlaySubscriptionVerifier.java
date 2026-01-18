package com.DreamOfDuck.subscription.service.verify;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.DreamOfDuck.subscription.config.IapProperties;
import com.DreamOfDuck.subscription.config.IapProperties.Google;
import com.DreamOfDuck.subscription.dto.request.VerifySubscriptionRequest;
import com.DreamOfDuck.subscription.entity.StorePlatformEnum;
import com.DreamOfDuck.subscription.service.verify.dto.playstore.GoogleSubscriptionLineItem;
import com.DreamOfDuck.subscription.service.verify.dto.playstore.GoogleSubscriptionPurchaseV2Response;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.androidpublisher.AndroidPublisher;
import com.google.api.services.androidpublisher.AndroidPublisherScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * IMPORTANT: This is a clean abstraction layer.
 * Replace the stub logic with real Google Play Developer API verification when you wire credentials.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GooglePlaySubscriptionVerifier implements StoreSubscriptionVerifier {
    private final IapProperties properties;
    private final ObjectMapper objectMapper;

    private volatile AndroidPublisher androidPublisher;

    @Override
    public StorePlatformEnum supports() {
        return StorePlatformEnum.GOOGLE;
    }

    @Override
    public VerificationResult verify(VerifySubscriptionRequest request) {
        if (!StringUtils.hasText(request.getPurchaseToken()) || !StringUtils.hasText(request.getProductId())) {
            return VerificationResult.builder().valid(false).rawResponse("missing_token_or_product").build();
        }

        Google google = properties.getGoogle();
        if (google == null || !StringUtils.hasText(google.getServiceAccountJson()) || !StringUtils.hasText(google.getPackageName())) {
            return VerificationResult.builder()
                    .valid(false)
                    .rawResponse("missing_google_config(serviceAccountJson/packageName)")
                    .build();
        }

        try {
            AndroidPublisher client = getOrCreateAndroidPublisher(google);

            // Subscriptions v2: purchaseToken-only lookup (productId is validated against response when present)
            Object response = client.purchases()
                    .subscriptionsv2()
                    .get(google.getPackageName(), request.getPurchaseToken())
                    .execute();

            // Use JsonNode so we don't depend on the exact generated model surface
            JsonNode root = objectMapper.valueToTree(response);
            GoogleSubscriptionPurchaseV2Response parsed =
                    objectMapper.treeToValue(root, GoogleSubscriptionPurchaseV2Response.class);

            GoogleSubscriptionLineItem primaryLineItem = firstLineItem(parsed);

            Instant now = Instant.now();
            Instant startInstant = parseRfc3339Instant(firstText(primaryLineItem != null ? primaryLineItem.getStartTime() : null, parsed.getStartTime()));
            Instant expiryInstant = parseRfc3339Instant(primaryLineItem != null ? primaryLineItem.getExpiryTime() : null);

            boolean stateActive = "SUBSCRIPTION_STATE_ACTIVE".equalsIgnoreCase(nullToEmpty(parsed.getSubscriptionState()));
            boolean notExpired = expiryInstant != null && expiryInstant.isAfter(now);

            boolean valid = stateActive && notExpired;

            // Validate productId if Google returns it in lineItems
            if (primaryLineItem != null && StringUtils.hasText(primaryLineItem.getProductId())
                    && !primaryLineItem.getProductId().equals(request.getProductId())) {
                log.warn("Google productId mismatch: requestProductId={}, responseProductId={}",
                        request.getProductId(), primaryLineItem.getProductId());
                valid = false;
            }

            boolean autoRenewing = primaryLineItem != null && primaryLineItem.getAutoRenewingPlan() != null;
            boolean isTrial = primaryLineItem != null && hasTrialTag(primaryLineItem);

            LocalDateTime startedAt = startInstant != null
                    ? LocalDateTime.ofInstant(startInstant, ZoneId.systemDefault())
                    : null;
            LocalDateTime expiresAt = expiryInstant != null
                    ? LocalDateTime.ofInstant(expiryInstant, ZoneId.systemDefault())
                    : null;

            String txId = StringUtils.hasText(parsed.getLatestOrderId())
                    ? parsed.getLatestOrderId()
                    : (StringUtils.hasText(request.getStoreTransactionId())
                            ? request.getStoreTransactionId()
                            : safeKey("google_tx", request.getPurchaseToken()));

            return VerificationResult.builder()
                    .valid(valid)
                    .startedAt(startedAt)
                    .expiresAt(expiresAt)
                    .autoRenewing(autoRenewing)
                    .isTrialPeriod(isTrial)
                    .storeTransactionId(txId)
                    .originalTransactionId(txId)
                    .rawResponse(objectMapper.writeValueAsString(root))
                    .build();

        } catch (Exception e) {
            log.warn("Google Play subscription verification failed: {}", e.getMessage(), e);
            return VerificationResult.builder()
                    .valid(false)
                    .rawResponse("verification_error: " + e.getMessage())
                    .build();
        }
    }

    private String safeKey(String prefix, String payload) {
        String trimmed = payload.trim();
        int len = Math.min(trimmed.length(), 48);
        return prefix + ":" + trimmed.substring(0, len);
    }

    private AndroidPublisher getOrCreateAndroidPublisher(Google google) throws Exception {
        AndroidPublisher existing = androidPublisher;
        if (existing != null) {
            return existing;
        }

        synchronized (this) {
            if (androidPublisher != null) {
                return androidPublisher;
            }
            String json = google.getServiceAccountJson();
            GoogleCredentials credentials = GoogleCredentials
                    .fromStream(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)))
                    .createScoped(AndroidPublisherScopes.ANDROIDPUBLISHER);

            NetHttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            androidPublisher = new AndroidPublisher.Builder(transport, jsonFactory, new HttpCredentialsAdapter(credentials))
                    .setApplicationName("duck-be")
                    .build();
            return androidPublisher;
        }
    }

    private GoogleSubscriptionLineItem firstLineItem(GoogleSubscriptionPurchaseV2Response parsed) {
        if (parsed == null) {
            return null;
        }
        List<GoogleSubscriptionLineItem> items = parsed.getLineItems();
        if (items == null || items.isEmpty()) {
            return null;
        }
        return items.get(0);
    }

    private boolean hasTrialTag(GoogleSubscriptionLineItem item) {
        if (item == null || item.getOfferDetails() == null || item.getOfferDetails().getOfferTags() == null) {
            return false;
        }
        return item.getOfferDetails().getOfferTags().stream()
                .filter(StringUtils::hasText)
                .map(String::toLowerCase)
                .anyMatch(tag -> tag.contains("trial"));
    }

    private Instant parseRfc3339Instant(String value) {
        try {
            if (!StringUtils.hasText(value)) {
                return null;
            }
            return Instant.parse(value);
        } catch (Exception e) {
            log.debug("Failed to parse RFC3339 instant: {}", value);
            return null;
        }
    }

    private String firstText(String primary, String fallback) {
        return StringUtils.hasText(primary) ? primary : fallback;
    }

    private String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
