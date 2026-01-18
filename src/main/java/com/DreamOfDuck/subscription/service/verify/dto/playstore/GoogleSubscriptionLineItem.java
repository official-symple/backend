package com.DreamOfDuck.subscription.service.verify.dto.playstore;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleSubscriptionLineItem {
    /**
     * RFC3339 timestamp string (e.g., 2026-01-18T12:34:56.789Z)
     */
    @JsonProperty("startTime")
    private String startTime;

    /**
     * RFC3339 timestamp string
     */
    @JsonProperty("expiryTime")
    private String expiryTime;

    @JsonProperty("productId")
    private String productId;

    /**
     * Present for auto-renewing plans.
     */
    @JsonProperty("autoRenewingPlan")
    private Object autoRenewingPlan;

    /**
     * Present for prepaid plans.
     */
    @JsonProperty("prepaidPlan")
    private Object prepaidPlan;

    @JsonProperty("offerDetails")
    private OfferDetails offerDetails;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OfferDetails {
        @JsonProperty("offerTags")
        private List<String> offerTags;
    }
}


