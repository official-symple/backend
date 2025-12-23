package com.DreamOfDuck.subscription.service.verify;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppleReceiptResponse {
    @JsonProperty("status")
    private Integer status;

    @JsonProperty("receipt")
    private Receipt receipt;

    @JsonProperty("latest_receipt_info")
    private List<LatestReceiptInfo> latestReceiptInfo;

    @JsonProperty("pending_renewal_info")
    private List<PendingRenewalInfo> pendingRenewalInfo;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Receipt {
        @JsonProperty("receipt_type")
        private String receiptType;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LatestReceiptInfo {
        @JsonProperty("transaction_id")
        private String transactionId;

        @JsonProperty("original_transaction_id")
        private String originalTransactionId;

        @JsonProperty("product_id")
        private String productId;

        @JsonProperty("purchase_date_ms")
        private String purchaseDateMs;

        @JsonProperty("expires_date_ms")
        private String expiresDateMs;

        @JsonProperty("is_trial_period")
        private String isTrialPeriod;

        @JsonProperty("is_in_intro_offer_period")
        private String isInIntroOfferPeriod;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PendingRenewalInfo {
        @JsonProperty("auto_renew_status")
        private String autoRenewStatus;

        @JsonProperty("product_id")
        private String productId;
    }
}

