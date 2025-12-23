package com.DreamOfDuck.subscription.service.verify;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.DreamOfDuck.subscription.config.IapProperties;
import com.DreamOfDuck.subscription.entity.StorePlatformEnum;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppleSubscriptionVerifier implements StoreSubscriptionVerifier {
    private static final String PRODUCTION_URL = "https://buy.itunes.apple.com/verifyReceipt";
    private static final String SANDBOX_URL = "https://sandbox.itunes.apple.com/verifyReceipt";

    private final IapProperties properties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public StorePlatformEnum supports() {
        return StorePlatformEnum.APPLE;
    }

    @Override
    public VerificationResult verify(VerificationCommand command) {
        if (!StringUtils.hasText(command.getReceiptData())) {
            return VerificationResult.builder().valid(false).rawResponse("missing_receipt").build();
        }

        try {
            String url = properties.getApple().isSandbox() ? SANDBOX_URL : PRODUCTION_URL;
            AppleReceiptRequest request = AppleReceiptRequest.builder()
                    .receiptData(command.getReceiptData())
                    .password(StringUtils.hasText(properties.getApple().getSharedSecret()) 
                            ? properties.getApple().getSharedSecret() 
                            : null)
                    .excludeOldTransactions(false)
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<AppleReceiptRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<AppleReceiptResponse> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, AppleReceiptResponse.class);

            AppleReceiptResponse receiptResponse = response.getBody();
            if (receiptResponse == null) {
                log.error("Apple receipt verification returned null response");
                return VerificationResult.builder()
                        .valid(false)
                        .rawResponse("null_response")
                        .build();
            }

            // Handle status codes
            // 21007 = receipt is from sandbox but sent to production, retry with sandbox
            if (receiptResponse.getStatus() == 21007 && !properties.getApple().isSandbox()) {
                log.info("Receipt is from sandbox, retrying with sandbox URL");
                return verifyWithSandbox(command);
            }

            // 0 = success
            if (receiptResponse.getStatus() != 0) {
                log.warn("Apple receipt verification failed with status: {}", receiptResponse.getStatus());
                return VerificationResult.builder()
                        .valid(false)
                        .rawResponse("status_" + receiptResponse.getStatus())
                        .build();
            }

            // Extract subscription info from latest_receipt_info
            List<AppleReceiptResponse.LatestReceiptInfo> latestReceiptInfo = receiptResponse.getLatestReceiptInfo();
            if (latestReceiptInfo == null || latestReceiptInfo.isEmpty()) {
                log.warn("No latest_receipt_info found in Apple response");
                return VerificationResult.builder()
                        .valid(false)
                        .rawResponse("no_receipt_info")
                        .build();
            }

            // Get the most recent transaction (first in the list)
            AppleReceiptResponse.LatestReceiptInfo latestInfo = latestReceiptInfo.get(0);

            // Parse dates
            LocalDateTime startedAt = parseTimestamp(latestInfo.getPurchaseDateMs());
            LocalDateTime expiresAt = parseTimestamp(latestInfo.getExpiresDateMs());

            if (expiresAt == null) {
                log.warn("No expiration date found in receipt");
                return VerificationResult.builder()
                        .valid(false)
                        .rawResponse("no_expiration_date")
                        .build();
            }

            // Check if auto-renewing
            boolean autoRenewing = false;
            List<AppleReceiptResponse.PendingRenewalInfo> pendingRenewalInfo = receiptResponse.getPendingRenewalInfo();
            if (pendingRenewalInfo != null && !pendingRenewalInfo.isEmpty()) {
                AppleReceiptResponse.PendingRenewalInfo renewalInfo = pendingRenewalInfo.get(0);
                if (renewalInfo.getAutoRenewStatus() != null) {
                    autoRenewing = "1".equals(renewalInfo.getAutoRenewStatus());
                }
            }

            // Check if trial period
            boolean isTrialPeriod = "true".equalsIgnoreCase(latestInfo.getIsTrialPeriod())
                    || "true".equalsIgnoreCase(latestInfo.getIsInIntroOfferPeriod());

            String transactionId = StringUtils.hasText(latestInfo.getTransactionId())
                    ? latestInfo.getTransactionId()
                    : command.getStoreTransactionId();
            String originalTransactionId = StringUtils.hasText(latestInfo.getOriginalTransactionId())
                    ? latestInfo.getOriginalTransactionId()
                    : transactionId;

            return VerificationResult.builder()
                    .valid(true)
                    .startedAt(startedAt)
                    .expiresAt(expiresAt)
                    .autoRenewing(autoRenewing)
                    .isTrialPeriod(isTrialPeriod)
                    .storeTransactionId(transactionId)
                    .originalTransactionId(originalTransactionId)
                    .rawResponse(objectMapper.writeValueAsString(receiptResponse))
                    .build();

        } catch (RestClientException e) {
            log.error("Error calling Apple receipt verification API", e);
            return VerificationResult.builder()
                    .valid(false)
                    .rawResponse("api_error: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error during Apple receipt verification", e);
            return VerificationResult.builder()
                    .valid(false)
                    .rawResponse("error: " + e.getMessage())
                    .build();
        }
    }

    private VerificationResult verifyWithSandbox(VerificationCommand command) {
        try {
            AppleReceiptRequest request = AppleReceiptRequest.builder()
                    .receiptData(command.getReceiptData())
                    .password(StringUtils.hasText(properties.getApple().getSharedSecret()) 
                            ? properties.getApple().getSharedSecret() 
                            : null)
                    .excludeOldTransactions(false)
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<AppleReceiptRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<AppleReceiptResponse> response = restTemplate.exchange(
                    SANDBOX_URL, HttpMethod.POST, entity, AppleReceiptResponse.class);

            AppleReceiptResponse receiptResponse = response.getBody();
            if (receiptResponse == null || receiptResponse.getStatus() != 0) {
                return VerificationResult.builder()
                        .valid(false)
                        .rawResponse("sandbox_status_" + (receiptResponse != null ? receiptResponse.getStatus() : "null"))
                        .build();
            }

            return parseReceiptResponse(receiptResponse, command);
        } catch (Exception e) {
            log.error("Error verifying with sandbox", e);
            return VerificationResult.builder()
                    .valid(false)
                    .rawResponse("sandbox_error: " + e.getMessage())
                    .build();
        }
    }

    private VerificationResult parseReceiptResponse(AppleReceiptResponse receiptResponse, VerificationCommand command) {
        try {
            List<AppleReceiptResponse.LatestReceiptInfo> latestReceiptInfo = receiptResponse.getLatestReceiptInfo();
            if (latestReceiptInfo == null || latestReceiptInfo.isEmpty()) {
                return VerificationResult.builder()
                        .valid(false)
                        .rawResponse("no_receipt_info")
                        .build();
            }

            AppleReceiptResponse.LatestReceiptInfo latestInfo = latestReceiptInfo.get(0);
            LocalDateTime startedAt = parseTimestamp(latestInfo.getPurchaseDateMs());
            LocalDateTime expiresAt = parseTimestamp(latestInfo.getExpiresDateMs());

            if (expiresAt == null) {
                return VerificationResult.builder()
                        .valid(false)
                        .rawResponse("no_expiration_date")
                        .build();
            }

            boolean autoRenewing = false;
            List<AppleReceiptResponse.PendingRenewalInfo> pendingRenewalInfo = receiptResponse.getPendingRenewalInfo();
            if (pendingRenewalInfo != null && !pendingRenewalInfo.isEmpty()) {
                AppleReceiptResponse.PendingRenewalInfo renewalInfo = pendingRenewalInfo.get(0);
                if (renewalInfo.getAutoRenewStatus() != null) {
                    autoRenewing = "1".equals(renewalInfo.getAutoRenewStatus());
                }
            }

            boolean isTrialPeriod = "true".equalsIgnoreCase(latestInfo.getIsTrialPeriod())
                    || "true".equalsIgnoreCase(latestInfo.getIsInIntroOfferPeriod());

            String transactionId = StringUtils.hasText(latestInfo.getTransactionId())
                    ? latestInfo.getTransactionId()
                    : command.getStoreTransactionId();
            String originalTransactionId = StringUtils.hasText(latestInfo.getOriginalTransactionId())
                    ? latestInfo.getOriginalTransactionId()
                    : transactionId;

            return VerificationResult.builder()
                    .valid(true)
                    .startedAt(startedAt)
                    .expiresAt(expiresAt)
                    .autoRenewing(autoRenewing)
                    .isTrialPeriod(isTrialPeriod)
                    .storeTransactionId(transactionId)
                    .originalTransactionId(originalTransactionId)
                    .rawResponse(objectMapper.writeValueAsString(receiptResponse))
                    .build();
        } catch (Exception e) {
            log.error("Error parsing receipt response", e);
            return VerificationResult.builder()
                    .valid(false)
                    .rawResponse("parse_error: " + e.getMessage())
                    .build();
        }
    }

    private LocalDateTime parseTimestamp(String timestampMs) {
        if (!StringUtils.hasText(timestampMs)) {
            return null;
        }
        try {
            long timestamp = Long.parseLong(timestampMs);
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
        } catch (NumberFormatException e) {
            log.warn("Invalid timestamp format: {}", timestampMs);
            return null;
        }
    }
}
