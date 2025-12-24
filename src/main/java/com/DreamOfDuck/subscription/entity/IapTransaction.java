package com.DreamOfDuck.subscription.entity;

import java.time.LocalDateTime;

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
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "iap_transaction", indexes = {
    @Index(name = "idx_iap_member", columnList = "member_id"),
    @Index(name = "idx_iap_member_expires", columnList = "member_id,expires_at")
})
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
    private StorePlatformEnum platform;

    @Column(name = "product_id", length = 100)
    private String productId;

    @Column(name = "store_transaction_id", length = 200)
    private String storeTransactionId;

    @Column(name = "original_transaction_id", length = 200)
    private String originalTransactionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", length = 20, nullable = false)
    private VerificationStatusEnum verificationStatus;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "is_trial_period")
    private Boolean isTrialPeriod;

    @Column(name = "auto_renewing")
    private Boolean autoRenewing;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Lob
    @Column(name = "raw_response", columnDefinition = "TEXT")
    private String rawResponse;

    public boolean isActive() {
        return verificationStatus == VerificationStatusEnum.VERIFIED 
            && expiresAt != null 
            && expiresAt.isAfter(LocalDateTime.now());
    }

    public void updateFromVerification(LocalDateTime expiresAt, Boolean isTrialPeriod, Boolean autoRenewing, String rawResponse) {
        this.expiresAt = expiresAt;
        this.isTrialPeriod = isTrialPeriod;
        this.autoRenewing = autoRenewing;
        this.verifiedAt = LocalDateTime.now();
        this.rawResponse = rawResponse;
        this.verificationStatus = VerificationStatusEnum.VERIFIED;
    }

    public void markExpired() {
        this.verificationStatus = VerificationStatusEnum.EXPIRED;
        this.verifiedAt = LocalDateTime.now();
    }
}
