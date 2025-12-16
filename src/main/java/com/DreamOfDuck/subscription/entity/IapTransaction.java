package com.DreamOfDuck.subscription.entity;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.global.entity.TimeStamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "iap_transaction")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class IapTransaction extends TimeStamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "memberId", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform", length = 20, nullable = false)
    private StorePlatform platform;

    @Column(name = "product_id", length = 100)
    private String productId;

    @Column(name = "store_subscription_id", length = 200)
    private String storeSubscriptionId;

    @Column(name = "store_transaction_id", length = 200)
    private String storeTransactionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", length = 20, nullable = false)
    private VerificationStatus verificationStatus;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Lob
    @Column(name = "request_payload")
    private String requestPayload;

    @Lob
    @Column(name = "raw_response")
    private String rawResponse;
}


