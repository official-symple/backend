package com.DreamOfDuck.subscription.service.verify.dto.appstore;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppStoreServerSubscriptionStatusResponse {
    @JsonProperty("environment")
    private String environment;

    @JsonProperty("bundleId")
    private String bundleId;

    @JsonProperty("data")
    private List<DataItem> data;

    public enum SubscriptionStatus {
        UNKNOWN(0),
        ACTIVE(1),
        EXPIRED(2),
        BILLING_RETRY(3),
        BILLING_GRACE_PERIOD(4),
        REVOKED(5);

        private final int code;

        SubscriptionStatus(int code) {
            this.code = code;
        }

        @JsonValue
        public int getCode() {
            return code;
        }

        @JsonCreator
        public static SubscriptionStatus fromCode(Integer code) {
            if (code == null) {
                return null;
            }
            for (SubscriptionStatus value : SubscriptionStatus.values()) {
                if (value.code == code) {
                    return value;
                }
            }
            return UNKNOWN;
        }
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataItem {
        @JsonProperty("subscriptionGroupIdentifier")
        private String subscriptionGroupIdentifier;

        @JsonProperty("lastTransactions")
        private List<LastTransaction> lastTransactions;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LastTransaction {
        @JsonProperty("originalTransactionId")
        private String originalTransactionId;

        @JsonProperty("status")
        private SubscriptionStatus status;

        @JsonProperty("signedTransactionInfo")
        private String signedTransactionInfo;

        @JsonProperty("signedRenewalInfo")
        private String signedRenewalInfo;
    }
}


