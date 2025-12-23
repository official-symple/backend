package com.DreamOfDuck.subscription.service.verify;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AppleReceiptRequest {
    @JsonProperty("receipt-data")
    private String receiptData;

    @JsonProperty("password")
    private String password;

    @JsonProperty("exclude-old-transactions")
    private Boolean excludeOldTransactions;
}

