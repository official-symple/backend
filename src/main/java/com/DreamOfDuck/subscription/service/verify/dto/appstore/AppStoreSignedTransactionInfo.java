package com.DreamOfDuck.subscription.service.verify.dto.appstore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppStoreSignedTransactionInfo {
    @JsonProperty("transactionId")
    private String transactionId;

    @JsonProperty("originalTransactionId")
    private String originalTransactionId;

    @JsonProperty("webOrderLineItemId")
    private String webOrderLineItemId;

    @JsonProperty("bundleId")
    private String bundleId;

    @JsonProperty("productId")
    private String productId;

    @JsonProperty("subscriptionGroupIdentifier")
    private String subscriptionGroupIdentifier;

    @JsonProperty("purchaseDate")
    @JsonDeserialize(using = FlexibleEpochMillisDeserializer.class)
    private Long purchaseDate;

    @JsonProperty("originalPurchaseDate")
    @JsonDeserialize(using = FlexibleEpochMillisDeserializer.class)
    private Long originalPurchaseDate;

    @JsonProperty("expiresDate")
    @JsonDeserialize(using = FlexibleEpochMillisDeserializer.class)
    private Long expiresDate;

    @JsonProperty("quantity")
    @JsonDeserialize(using = FlexibleLongDeserializer.class)
    private Long quantity;

    @JsonProperty("type")
    private String type;

    @JsonProperty("inAppOwnershipType")
    private String inAppOwnershipType;

    @JsonProperty("signedDate")
    @JsonDeserialize(using = FlexibleEpochMillisDeserializer.class)
    private Long signedDate;

    @JsonProperty("environment")
    private String environment;

    @JsonProperty("transactionReason")
    private String transactionReason;

    @JsonProperty("storefront")
    private String storefront;

    @JsonProperty("storefrontId")
    @JsonDeserialize(using = FlexibleLongDeserializer.class)
    private Long storefrontId;

    @JsonProperty("price")
    @JsonDeserialize(using = FlexibleLongDeserializer.class)
    private Long price;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("appTransactionId")
    private String appTransactionId;

    @JsonProperty("isTrialPeriod")
    @JsonDeserialize(using = FlexibleBooleanDeserializer.class)
    private Boolean isTrialPeriod;

    @JsonProperty("isInIntroOfferPeriod")
    @JsonDeserialize(using = FlexibleBooleanDeserializer.class)
    private Boolean isInIntroOfferPeriod;
}


