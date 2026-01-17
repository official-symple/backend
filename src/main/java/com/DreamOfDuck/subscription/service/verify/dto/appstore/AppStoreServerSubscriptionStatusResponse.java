package com.DreamOfDuck.subscription.service.verify.dto.appstore;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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
        private Integer status;

        @JsonProperty("signedTransactionInfo")
        private String signedTransactionInfo;

        @JsonProperty("signedRenewalInfo")
        private String signedRenewalInfo;
    }
}


