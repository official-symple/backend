package com.DreamOfDuck.subscription.service.verify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppStoreServerTransactionResponse {
    @JsonProperty("signedTransactionInfo")
    private String signedTransactionInfo;

    @JsonProperty("signedRenewalInfo")
    private String signedRenewalInfo;
}

