package com.DreamOfDuck.subscription.service.verify;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.DreamOfDuck.global.exception.CustomException;
import com.DreamOfDuck.global.exception.ErrorCode;
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
        log.info("[GooglePlaySubscriptionVerifier] verify 시작 - purchaseToken: {}, productId: {}, storeTransactionId: {}",
                request.getPurchaseToken(), request.getProductId(), request.getStoreTransactionId());

        if (!StringUtils.hasText(request.getPurchaseToken()) || !StringUtils.hasText(request.getProductId())) {
            log.error("[GooglePlaySubscriptionVerifier] 요청 파라미터 누락 - purchaseToken: {}, productId: {}",
                    request.getPurchaseToken(), request.getProductId());
            throw new CustomException(ErrorCode.IAP_INVALID_REQUEST);
        }

        Google google = properties.getGoogle();
        if (google == null || !StringUtils.hasText(google.getPackageName())) {
            log.error("[GooglePlaySubscriptionVerifier] 구글 설정 오류 - google: {}, packageName: {}",
                    google != null, google != null ? google.getPackageName() : "null");
            throw new RuntimeException("구글 설정 오류");
        }

        log.info("[GooglePlaySubscriptionVerifier] 구글 설정 확인 완료 - packageName: {}", google.getPackageName());

        try {
            log.debug("[GooglePlaySubscriptionVerifier] AndroidPublisher 클라이언트 생성 시작");
            AndroidPublisher client = getOrCreateAndroidPublisher(google);
            log.debug("[GooglePlaySubscriptionVerifier] AndroidPublisher 클라이언트 생성 완료");

            log.info("[GooglePlaySubscriptionVerifier] API 호출 시작 - packageName: {}, purchaseToken: {}",
                    google.getPackageName(), request.getPurchaseToken());

            // Subscriptions v2: purchaseToken-only lookup (productId is validated against response when present)
            Object response = client.purchases()
                    .subscriptionsv2()
                    .get(google.getPackageName(), request.getPurchaseToken())
                    .execute();

            log.info("[GooglePlaySubscriptionVerifier] API 응답 수신 완료 - response: {}", response);

            // Use JsonNode so we don't depend on the exact generated model surface
            log.debug("[GooglePlaySubscriptionVerifier] 응답 파싱 시작");
            JsonNode root = objectMapper.valueToTree(response);
            GoogleSubscriptionPurchaseV2Response parsed =
                    objectMapper.treeToValue(root, GoogleSubscriptionPurchaseV2Response.class);
            log.debug("[GooglePlaySubscriptionVerifier] 응답 파싱 완료 - subscriptionState: {}, startTime: {}",
                    parsed.getSubscriptionState(), parsed.getStartTime());

            GoogleSubscriptionLineItem primaryLineItem = firstLineItem(parsed);
            log.info("[GooglePlaySubscriptionVerifier] PrimaryLineItem 추출 - lineItem 존재: {}, productId: {}, startTime: {}, expiryTime: {}, autoRenewingPlan: {}",
                    primaryLineItem != null,
                    primaryLineItem != null ? primaryLineItem.getProductId() : "null",
                    primaryLineItem != null ? primaryLineItem.getStartTime() : "null",
                    primaryLineItem != null ? primaryLineItem.getExpiryTime() : "null",
                    primaryLineItem != null && primaryLineItem.getAutoRenewingPlan() != null);

            Instant now = Instant.now();
            Instant startInstant = parseRfc3339Instant(firstText(primaryLineItem != null ? primaryLineItem.getStartTime() : null, parsed.getStartTime()));
            Instant expiryInstant = parseRfc3339Instant(primaryLineItem != null ? primaryLineItem.getExpiryTime() : null);

            log.info("[GooglePlaySubscriptionVerifier] 시간 파싱 완료 - now: {}, startInstant: {}, expiryInstant: {}",
                    now, startInstant, expiryInstant);

            boolean stateActive = "SUBSCRIPTION_STATE_ACTIVE".equalsIgnoreCase(nullToEmpty(parsed.getSubscriptionState()));
            boolean notExpired = expiryInstant != null && expiryInstant.isAfter(now);

            log.info("[GooglePlaySubscriptionVerifier] 상태 검증 - subscriptionState: {}, stateActive: {}, expiryInstant: {}, now: {}, notExpired: {}",
                    parsed.getSubscriptionState(), stateActive, expiryInstant, now, notExpired);

            boolean valid = stateActive && notExpired;

            // Validate productId if Google returns it in lineItems
            if (primaryLineItem != null && StringUtils.hasText(primaryLineItem.getProductId())
                    && !primaryLineItem.getProductId().equals(request.getProductId())) {
                log.warn("[GooglePlaySubscriptionVerifier] ProductId 불일치 - requestProductId: {}, responseProductId: {}",
                        request.getProductId(), primaryLineItem.getProductId());
                valid = false;
            }

            boolean autoRenewing = primaryLineItem != null && primaryLineItem.getAutoRenewingPlan() != null;
            boolean isTrial = primaryLineItem != null && hasTrialTag(primaryLineItem);

            log.info("[GooglePlaySubscriptionVerifier] 추가 정보 - autoRenewing: {}, isTrial: {}", autoRenewing, isTrial);

            LocalDateTime startedAt = startInstant != null
                    ? LocalDateTime.ofInstant(startInstant, ZoneId.systemDefault())
                    : null;
            LocalDateTime expiresAt = expiryInstant != null
                    ? LocalDateTime.ofInstant(expiryInstant, ZoneId.systemDefault())
                    : null;

            log.info("[GooglePlaySubscriptionVerifier] LocalDateTime 변환 - startedAt: {}, expiresAt: {}", startedAt, expiresAt);

            String txId = StringUtils.hasText(parsed.getLatestOrderId())
                    ? parsed.getLatestOrderId()
                    : (StringUtils.hasText(request.getStoreTransactionId())
                            ? request.getStoreTransactionId()
                            : safeKey("google_tx", request.getPurchaseToken()));

            log.info("[GooglePlaySubscriptionVerifier] TransactionId 결정 - latestOrderId: {}, storeTransactionId: {}, 최종 txId: {}",
                    parsed.getLatestOrderId(), request.getStoreTransactionId(), txId);

            VerificationResult result = VerificationResult.builder()
                    .valid(valid)
                    .startedAt(startedAt)
                    .expiresAt(expiresAt)
                    .autoRenewing(autoRenewing)
                    .isTrialPeriod(isTrial)
                    .storeTransactionId(txId)
                    .originalTransactionId(txId)
                    .rawResponse(objectMapper.writeValueAsString(root))
                    .build();

            log.info("[GooglePlaySubscriptionVerifier] 검증 완료 - valid: {}, startedAt: {}, expiresAt: {}, autoRenewing: {}, isTrial: {}, txId: {}",
                    result.isValid(), result.getStartedAt(), result.getExpiresAt(), result.getAutoRenewing(), result.getIsTrialPeriod(), result.getStoreTransactionId());

            return result;

        } catch (Exception e) {
            log.error("[GooglePlaySubscriptionVerifier] 검증 실패 - purchaseToken: {}, productId: {}, error: {}",
                    request.getPurchaseToken(), request.getProductId(), e.getMessage(), e);
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
            InputStream credentialsStream = getServiceAccountInputStream(google);
            GoogleCredentials credentials = GoogleCredentials
                    .fromStream(credentialsStream)
                    .createScoped(AndroidPublisherScopes.ANDROIDPUBLISHER);
            log.info("credential: {}" , credentials);

            NetHttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            androidPublisher = new AndroidPublisher.Builder(transport, jsonFactory, new HttpCredentialsAdapter(credentials))
                    .setApplicationName("DreamOfDuck-Backend")
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

    private InputStream getServiceAccountInputStream(Google google) throws IOException {
        ClassPathResource resource = new ClassPathResource("iap-verifier.json");
        return resource.getInputStream();
    }
}
