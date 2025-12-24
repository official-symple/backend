package com.DreamOfDuck.subscription.service.verify;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.DreamOfDuck.subscription.config.IapProperties;
import com.DreamOfDuck.subscription.entity.StorePlatformEnum;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
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

        // JWT receipt from StoreKit 2 - decode and verify with App Store Server API
        return verifyJwtReceipt(command);
    }

    @SuppressWarnings("unchecked")
    private VerificationResult verifyJwtReceipt(VerificationCommand command) {
        try {
            // Decode JWT receipt to extract transactionId
            String[] parts = command.getReceiptData().split("\\.");
            if (parts.length != 3) {
                return VerificationResult.builder()
                        .valid(false)
                        .rawResponse("invalid_jwt_format")
                        .build();
            }
            
            // Decode payload (second part)
            String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            Map<String, Object> claimsMap = objectMapper.readValue(payload, Map.class);
            Claims claims = Jwts.claims(claimsMap);

            log.info("Decoded JWT claims: {}", claims);

            // Extract transactionId to verify with App Store Server API
            String transactionId = claims.get("transactionId", String.class);
            if (!StringUtils.hasText(transactionId)) {
                log.warn("No transactionId found in JWT receipt");
                return VerificationResult.builder()
                        .valid(false)
                        .rawResponse("no_transaction_id")
                        .build();
            }

            // Verify transaction with App Store Server API
            Map<String, Object> serverTransactionInfo = verifyWithAppStoreServer(transactionId);
            if (serverTransactionInfo == null) {
                // If API call fails, fall back to local JWT decode
                log.warn("App Store Server API verification failed, falling back to local JWT decode");
                return parseJwtClaims(claims, command);
            }

            // Parse server response (signedTransactionInfo is also a JWT)
            return parseServerTransactionInfo(serverTransactionInfo, command);

        } catch (Exception e) {
            log.error("Error verifying JWT receipt", e);
            return VerificationResult.builder()
                    .valid(false)
                    .rawResponse("verification_error: " + e.getMessage())
                    .build();
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> verifyWithAppStoreServer(String transactionId) {
        try {
            String baseUrl = properties.getApple().isSandbox() 
                    ? APP_STORE_SERVER_API_SANDBOX 
                    : APP_STORE_SERVER_API_PRODUCTION;
            String url = baseUrl + transactionId;

            HttpHeaders headers = new HttpHeaders();
            
            // Generate JWT token for App Store Server API authentication
            String jwtToken = generateAppStoreConnectJWT();
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

            // Decode signedTransactionInfo (it's also a JWT)
            String[] parts = serverResponse.getSignedTransactionInfo().split("\\.");
            if (parts.length != 3) {
                return null;
            }

            String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            return objectMapper.readValue(payload, Map.class);

        } catch (RestClientException e) {
            log.warn("App Store Server API call failed: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.warn("Error calling App Store Server API: {}", e.getMessage());
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private VerificationResult parseServerTransactionInfo(Map<String, Object> transactionInfo, VerificationCommand command) {
        try {
            String transactionId = (String) transactionInfo.get("transactionId");
            String originalTransactionId = (String) transactionInfo.get("originalTransactionId");
            
            Long purchaseDate = getTimestampFromMap(transactionInfo, "purchaseDate");
            Long expiresDate = getTimestampFromMap(transactionInfo, "expiresDate");
            
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

            Boolean isTrialPeriod = (Boolean) transactionInfo.get("isTrialPeriod");
            Boolean isInIntroOfferPeriod = (Boolean) transactionInfo.get("isInIntroOfferPeriod");
            boolean isTrial = Boolean.TRUE.equals(isTrialPeriod) || Boolean.TRUE.equals(isInIntroOfferPeriod);

            // Check renewal info
            Map<String, Object> renewalInfo = (Map<String, Object>) transactionInfo.get("renewalInfo");
            boolean autoRenewing = false;
            if (renewalInfo != null) {
                String autoRenewStatus = (String) renewalInfo.get("autoRenewStatus");
                autoRenewing = "1".equals(autoRenewStatus);
            }

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

    private VerificationResult parseJwtClaims(Claims claims, VerificationCommand command) {
        try {
            String transactionId = claims.get("transactionId", String.class);
            String originalTransactionId = claims.get("originalTransactionId", String.class);
            
            Long purchaseDate = getTimestamp(claims, "purchaseDate");
            Long expiresDate = getTimestamp(claims, "expiresDate");
            
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

            Boolean isTrialPeriod = claims.get("isTrialPeriod", Boolean.class);
            Boolean isInIntroOfferPeriod = claims.get("isInIntroOfferPeriod", Boolean.class);
            boolean isTrial = Boolean.TRUE.equals(isTrialPeriod) || Boolean.TRUE.equals(isInIntroOfferPeriod);

            String renewalInfo = claims.get("renewalInfo", String.class);
            boolean autoRenewing = renewalInfo != null;

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
                    .rawResponse(objectMapper.writeValueAsString(claims))
                    .build();

        } catch (Exception e) {
            log.error("Error parsing JWT claims", e);
            return VerificationResult.builder()
                    .valid(false)
                    .rawResponse("parse_error: " + e.getMessage())
                    .build();
        }
    }

    private Long getTimestampFromMap(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            long timestamp = ((Number) value).longValue();
            if (timestamp < 10000000000L) {
                timestamp *= 1000;
            }
            return timestamp;
        }
        if (value instanceof String) {
            try {
                long timestamp = Long.parseLong((String) value);
                if (timestamp < 10000000000L) {
                    timestamp *= 1000;
                }
                return timestamp;
            } catch (NumberFormatException e) {
                log.warn("Invalid timestamp format for {}: {}", key, value);
                return null;
            }
        }
        return null;
    }

    private Long getTimestamp(Claims claims, String key) {
        Object value = claims.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            long timestamp = ((Number) value).longValue();
            // Apple timestamps can be in seconds or milliseconds
            // If less than 10^10, it's in seconds, convert to milliseconds
            if (timestamp < 10000000000L) {
                timestamp *= 1000;
            }
            return timestamp;
        }
        if (value instanceof String) {
            try {
                long timestamp = Long.parseLong((String) value);
                if (timestamp < 10000000000L) {
                    timestamp *= 1000;
                }
                return timestamp;
            } catch (NumberFormatException e) {
                log.warn("Invalid timestamp format for {}: {}", key, value);
                return null;
            }
        }
        return null;
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
