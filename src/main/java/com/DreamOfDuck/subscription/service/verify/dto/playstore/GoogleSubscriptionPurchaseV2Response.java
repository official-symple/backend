package com.DreamOfDuck.subscription.service.verify.dto.playstore;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleSubscriptionPurchaseV2Response {
    @JsonProperty("subscriptionState")
    private String subscriptionState;

    /**
     * RFC3339 timestamp string
     */
    @JsonProperty("startTime")
    private String startTime;

    @JsonProperty("latestOrderId")
    private String latestOrderId;

    @JsonProperty("acknowledgementState")
    private String acknowledgementState;

    @JsonProperty("lineItems")
    private List<GoogleSubscriptionLineItem> lineItems;
}


