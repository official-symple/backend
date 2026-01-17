package com.DreamOfDuck.subscription.service.verify;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.DreamOfDuck.global.exception.CustomException;
import com.DreamOfDuck.global.exception.ErrorCode;
import com.DreamOfDuck.subscription.config.IapProperties;
import com.DreamOfDuck.subscription.dto.request.VerifySubscriptionRequest;
import com.DreamOfDuck.subscription.entity.StorePlatformEnum;
import com.DreamOfDuck.subscription.service.verify.dto.appstore.AppStoreServerSubscriptionStatusResponse;
import com.DreamOfDuck.subscription.service.verify.dto.appstore.AppStoreServerTransactionResponse;
import com.DreamOfDuck.subscription.service.verify.dto.appstore.AppStoreSignedRenewalInfo;
import com.DreamOfDuck.subscription.service.verify.dto.appstore.AppStoreSignedTransactionInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppleSubscriptionVerifier implements StoreSubscriptionVerifier {
    private static final String APP_STORE_SERVER_API_PRODUCTION = "https://api.storekit.itunes.apple.com/inApps/v1/transactions/";
    private static final String APP_STORE_SERVER_API_SANDBOX = "https://api.storekit-sandbox.itunes.apple.com/inApps/v1/transactions/";
    private static final String APP_STORE_SERVER_SUBSCRIPTIONS_API_PRODUCTION = "https://api.storekit.itunes.apple.com/inApps/v1/subscriptions/";
    private static final String APP_STORE_SERVER_SUBSCRIPTIONS_API_SANDBOX = "https://api.storekit-sandbox.itunes.apple.com/inApps/v1/subscriptions/";

    private final IapProperties properties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public StorePlatformEnum supports() {
        return StorePlatformEnum.APPLE;
    }

    @Override
    public VerificationResult verify(VerifySubscriptionRequest request) {
        if (!StringUtils.hasText(request.getReceiptData())) {
            throw new CustomException(ErrorCode.IAP_VALUE_NOT_FOUND);
        }

        // JWT receipt from StoreKit 2 - decode and verify with App Store Server API
        return verifyJwtReceipt(request);
    }

    private VerificationResult verifyJwtReceipt(VerifySubscriptionRequest request) {
        try {
            String[] parts = request.getReceiptData().split("\\.");
            if (parts.length != 3) {
                throw new CustomException(ErrorCode.IAP_JWT_FORMAT_ERROR);
            }
            
            String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            AppStoreSignedTransactionInfo receiptTxInfo = objectMapper.readValue(payload, AppStoreSignedTransactionInfo.class);
            log.info("Decoded JWT receipt txInfo: {}", receiptTxInfo);

            // Extract transactionId to verify with App Store Server API
            String transactionId = receiptTxInfo != null ? receiptTxInfo.getTransactionId() : null;
            if (!StringUtils.hasText(transactionId)) {
                log.warn("No transactionId found in JWT receipt");
                throw new CustomException(ErrorCode.IAP_JWT_FORMAT_ERROR);
            }

            // Verify transaction with App Store Server API
            AppStoreSignedInfo initialInfo = fetchTransactionAndRenewalInfo(transactionId);

            String originalTransactionId = initialInfo.transactionInfo.getOriginalTransactionId();
            if (!StringUtils.hasText(originalTransactionId)) {
                originalTransactionId = transactionId;
            }

            // Fetch latest renewed status using originalTransactionId, and prefer the newest transaction by expiresDate
            AppStoreSignedInfo latestInfo = fetchLatestSubscriptionStatusByOriginalTransactionId(originalTransactionId);
            AppStoreSignedInfo effectiveInfo = (latestInfo != null && latestInfo.transactionInfo != null) ? latestInfo : initialInfo;

            // Parse server response (signedTransactionInfo/signedRenewalInfo are JWTs)
            return parseServerTransactionInfo(effectiveInfo.transactionInfo, effectiveInfo.renewalInfo, request);

        } catch (Exception e) {
            log.error("Error verifying JWT receipt", e);
            return VerificationResult.builder()
                    .valid(false)
                    .rawResponse("verification_error: " + e.getMessage())
                    .build();
        }
    }

    private AppStoreSignedInfo fetchTransactionAndRenewalInfo(String transactionId) {
        try {
            String baseUrl = properties.getApple().isSandbox() 
                    ? APP_STORE_SERVER_API_SANDBOX 
                    : APP_STORE_SERVER_API_PRODUCTION;
            String url = baseUrl + transactionId;

            HttpHeaders headers = new HttpHeaders();
            
            // Generate JWT token for App Store Server API authentication
            String jwtToken = generateAppStoreConnectJWT();
            log.info("JWT Token: {}", jwtToken);
            if (StringUtils.hasText(jwtToken)) {
                headers.setBearerAuth(jwtToken);
            } else {
                log.warn("Failed to generate JWT token, API call may fail");
            }

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<AppStoreServerTransactionResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, AppStoreServerTransactionResponse.class);

            AppStoreServerTransactionResponse serverResponse = response.getBody();
            if (serverResponse == null || !StringUtils.hasText(serverResponse.getSignedTransactionInfo())) {
                return null;
            }

            log.info("Server Response: {}", serverResponse);

            AppStoreSignedTransactionInfo transactionInfo = decodeSignedPayload(serverResponse.getSignedTransactionInfo(), AppStoreSignedTransactionInfo.class);
            AppStoreSignedRenewalInfo renewalInfo = StringUtils.hasText(serverResponse.getSignedRenewalInfo())
                    ? decodeSignedPayload(serverResponse.getSignedRenewalInfo(), AppStoreSignedRenewalInfo.class)
                    : null;

            if (transactionInfo == null) {
                return null;
            }

            return new AppStoreSignedInfo(transactionInfo, renewalInfo);

        } catch (RestClientException e) {
            log.warn("App Store Server API call failed: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.warn("Error calling App Store Server API: {}", e.getMessage());
            return null;
        }
    }

    private AppStoreSignedInfo fetchLatestSubscriptionStatusByOriginalTransactionId(String originalTransactionId) {
        try {
            String baseUrl = properties.getApple().isSandbox()
                    ? APP_STORE_SERVER_SUBSCRIPTIONS_API_SANDBOX
                    : APP_STORE_SERVER_SUBSCRIPTIONS_API_PRODUCTION;
            String url = baseUrl + originalTransactionId;

            HttpHeaders headers = new HttpHeaders();
            String jwtToken = generateAppStoreConnectJWT();
            if (StringUtils.hasText(jwtToken)) {
                headers.setBearerAuth(jwtToken);
            } else {
                log.warn("Failed to generate JWT token, subscriptions API call may fail");
            }

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<AppStoreServerSubscriptionStatusResponse> response =
                    restTemplate.exchange(url, HttpMethod.GET, entity, AppStoreServerSubscriptionStatusResponse.class);
            AppStoreServerSubscriptionStatusResponse body = response.getBody();
            if (body == null || body.getData() == null) {
                return null;
            }
            log.info("App Store subscriptions API response: environment={}, bundleId={}, dataSize={}",
                    body.getEnvironment(), body.getBundleId(), body.getData().size());

            AppStoreSignedInfo best = null;
            Long bestExpires = null;

            for (AppStoreServerSubscriptionStatusResponse.DataItem dataItem : body.getData()) {
                if (dataItem == null || dataItem.getLastTransactions() == null) {
                    continue;
                }

                for (AppStoreServerSubscriptionStatusResponse.LastTransaction lastTxItem : dataItem.getLastTransactions()) {
                    if (lastTxItem == null) {
                        continue;
                    }
                    String signedTransactionInfo = lastTxItem.getSignedTransactionInfo();
                    if (!StringUtils.hasText(signedTransactionInfo)) {
                        continue;
                    }

                    AppStoreSignedTransactionInfo txInfo = decodeSignedPayload(signedTransactionInfo, AppStoreSignedTransactionInfo.class);
                    if (txInfo == null) {
                        continue;
                    }
                    enrichTransactionInfo(txInfo, body, dataItem, lastTxItem);
                    log.info("Tx Info: {}", txInfo);

                    Long expiresDate = txInfo.getExpiresDate();
                    if (expiresDate == null) {
                        continue;
                    }

                    if (bestExpires == null || expiresDate > bestExpires) {
                        String signedRenewalInfo = lastTxItem.getSignedRenewalInfo();
                        AppStoreSignedRenewalInfo renewalInfo = StringUtils.hasText(signedRenewalInfo)
                                ? decodeSignedPayload(signedRenewalInfo, AppStoreSignedRenewalInfo.class)
                                : null;
                        best = new AppStoreSignedInfo(txInfo, renewalInfo);
                        bestExpires = expiresDate;
                    }
                }
            }

            return best;

        } catch (RestClientException e) {
            log.warn("App Store Server subscriptions API call failed: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.warn("Error calling App Store Server subscriptions API: {}", e.getMessage());
            return null;
        }
    }

    private void enrichTransactionInfo(
            AppStoreSignedTransactionInfo txInfo,
            AppStoreServerSubscriptionStatusResponse response,
            AppStoreServerSubscriptionStatusResponse.DataItem dataItem,
            AppStoreServerSubscriptionStatusResponse.LastTransaction lastTxItem
    ) {
        // assume non-null for given response format
        txInfo.setEnvironment(response.getEnvironment());
        txInfo.setBundleId(response.getBundleId());
        txInfo.setSubscriptionGroupIdentifier(dataItem.getSubscriptionGroupIdentifier());
        txInfo.setOriginalTransactionId(lastTxItem.getOriginalTransactionId());
    }

    private <T> T decodeSignedPayload(String signedJwt, Class<T> clazz) {
        try {
            String[] parts = signedJwt.split("\\.");
            if (parts.length != 3) {
                return null;
            }
            String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            return objectMapper.readValue(payload, clazz);
        } catch (Exception e) {
            log.warn("Failed to decode signed JWT payload: {}", e.getMessage());
            return null;
        }
    }

    private VerificationResult parseServerTransactionInfo(
            AppStoreSignedTransactionInfo transactionInfo,
            AppStoreSignedRenewalInfo renewalInfo,
            VerifySubscriptionRequest request
    ) {
        try {
            String transactionId = transactionInfo.getTransactionId();
            String originalTransactionId = transactionInfo.getOriginalTransactionId();
            
            Long purchaseDate = transactionInfo.getPurchaseDate();
            Long expiresDate = transactionInfo.getExpiresDate();
            log.info("Purchase Date: {}", purchaseDate);
            log.info("Expires Date: {}", expiresDate);

            if (expiresDate == null) {
                log.warn("No expiration date found in server transaction info");
                return VerificationResult.builder()
                        .valid(false)
                        .rawResponse("no_expiration_date")
                        .build();
            }

            LocalDateTime startedAt = purchaseDate != null 
                    ? LocalDateTime.ofInstant(Instant.ofEpochMilli(purchaseDate), ZoneId.systemDefault())
                    : null;
            LocalDateTime expiresAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(expiresDate), ZoneId.systemDefault());

            Boolean isTrialPeriod = transactionInfo.getIsTrialPeriod();
            Boolean isInIntroOfferPeriod = transactionInfo.getIsInIntroOfferPeriod();
            boolean isTrial = Boolean.TRUE.equals(isTrialPeriod) || Boolean.TRUE.equals(isInIntroOfferPeriod);

            // Check renewal info (comes from signedRenewalInfo, not signedTransactionInfo)
            log.info("Renewal Info: {}", renewalInfo);
            boolean autoRenewing = false;
            if (renewalInfo != null) {
                autoRenewing = "1".equals(renewalInfo.getAutoRenewStatus());
            }

            String finalTransactionId = StringUtils.hasText(transactionId) 
                    ? transactionId 
                    : request.getStoreTransactionId();
            String finalOriginalTransactionId = StringUtils.hasText(originalTransactionId)
                    ? originalTransactionId
                    : finalTransactionId;

            return VerificationResult.builder()
                    .valid(true)
                    .startedAt(startedAt)
                    .expiresAt(expiresAt)
                    .autoRenewing(autoRenewing)
                    .isTrialPeriod(isTrial)
                    .storeTransactionId(finalTransactionId)
                    .originalTransactionId(finalOriginalTransactionId)
                    .rawResponse(objectMapper.writeValueAsString(transactionInfo))
                    .build();

        } catch (Exception e) {
            log.error("Error parsing server transaction info", e);
            return VerificationResult.builder()
                    .valid(false)
                    .rawResponse("parse_error: " + e.getMessage())
                    .build();
        }
    }

    private static class AppStoreSignedInfo {
        private final AppStoreSignedTransactionInfo transactionInfo;
        private final AppStoreSignedRenewalInfo renewalInfo;

        private AppStoreSignedInfo(AppStoreSignedTransactionInfo transactionInfo, AppStoreSignedRenewalInfo renewalInfo) {
            this.transactionInfo = transactionInfo;
            this.renewalInfo = renewalInfo;
        }
    }

    private VerificationResult parseJwtPayload(AppStoreSignedTransactionInfo txInfo, VerificationCommand command) {
        try {
            if (txInfo == null) {
                return VerificationResult.builder()
                        .valid(false)
                        .rawResponse("invalid_jwt_payload")
                        .build();
            }
            String transactionId = txInfo.getTransactionId();
            String originalTransactionId = txInfo.getOriginalTransactionId();
            
            Long purchaseDate = txInfo.getPurchaseDate();
            Long expiresDate = txInfo.getExpiresDate();
            
            if (expiresDate == null) {
                log.warn("No expiration date found in JWT receipt");
                return VerificationResult.builder()
                        .valid(false)
                        .rawResponse("no_expiration_date")
                        .build();
            }

            LocalDateTime startedAt = purchaseDate != null 
                    ? LocalDateTime.ofInstant(Instant.ofEpochMilli(purchaseDate), ZoneId.systemDefault())
                    : null;
            LocalDateTime expiresAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(expiresDate), ZoneId.systemDefault());

            Boolean isTrialPeriod = txInfo.getIsTrialPeriod();
            Boolean isInIntroOfferPeriod = txInfo.getIsInIntroOfferPeriod();
            boolean isTrial = Boolean.TRUE.equals(isTrialPeriod) || Boolean.TRUE.equals(isInIntroOfferPeriod);

            boolean autoRenewing = false;

            String finalTransactionId = StringUtils.hasText(transactionId) 
                    ? transactionId 
                    : command.getStoreTransactionId();
            String finalOriginalTransactionId = StringUtils.hasText(originalTransactionId)
                    ? originalTransactionId
                    : finalTransactionId;

            return VerificationResult.builder()
                    .valid(true)
                    .startedAt(startedAt)
                    .expiresAt(expiresAt)
                    .autoRenewing(autoRenewing)
                    .isTrialPeriod(isTrial)
                    .storeTransactionId(finalTransactionId)
                    .originalTransactionId(finalOriginalTransactionId)
                    .rawResponse(objectMapper.writeValueAsString(txInfo))
                    .build();

        } catch (Exception e) {
            log.error("Error parsing JWT claims", e);
            return VerificationResult.builder()
                    .valid(false)
                    .rawResponse("parse_error: " + e.getMessage())
                    .build();
        }
    }

    private String generateAppStoreConnectJWT() {
        try {
            IapProperties.Apple appleConfig = properties.getApple();
            
            if (!StringUtils.hasText(appleConfig.getKeyFileContent()) ||
                !StringUtils.hasText(appleConfig.getKeyId()) ||
                !StringUtils.hasText(appleConfig.getIssuerId()) ||
                !StringUtils.hasText(appleConfig.getBundleId())) {
                log.warn("Missing Apple API configuration for JWT generation");
                return null;
            }
            log.info("Generating App Store Connect JWT with Key ID: {}, Issuer ID: {}, Bundle ID: {}",
                    appleConfig.getKeyId(), appleConfig.getIssuerId(), appleConfig.getBundleId());
            log.info("Apple Key File Content: {}", appleConfig.getKeyFileContent());

            PrivateKey privateKey = parsePrivateKey(appleConfig.getKeyFileContent());
            if (privateKey == null) {
                return null;
            }

            long now = System.currentTimeMillis() / 1000;
            long expiration = now + 3600; // 1 hour expiration

            return Jwts.builder()
                    .setHeaderParam("alg", "ES256")
                    .setHeaderParam("kid", appleConfig.getKeyId())
                    .setIssuer(appleConfig.getIssuerId())
                    .setIssuedAt(new Date(now * 1000))
                    .setExpiration(new Date(expiration * 1000))
                    .claim("aud", "appstoreconnect-v1")
                    .claim("bid", appleConfig.getBundleId())
                    .signWith(privateKey, SignatureAlgorithm.ES256)
                    .compact();

        } catch (Exception e) {
            log.error("Error generating App Store Connect JWT", e);
            return null;
        }
    }

    private PrivateKey parsePrivateKey(String keyContent) {
        try {
            // Remove PEM headers and whitespace
            String privateKeyPEM = keyContent
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");

            // Decode base64
            byte[] keyBytes = Base64.getDecoder().decode(privateKeyPEM);

            // Create PKCS8EncodedKeySpec
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);

            // Get KeyFactory for EC algorithm
            KeyFactory keyFactory = KeyFactory.getInstance("EC");

            // Generate private key
            return keyFactory.generatePrivate(keySpec);

        } catch (Exception e) {
            log.error("Error parsing private key from PEM content", e);
            return null;
        }
    }
}
