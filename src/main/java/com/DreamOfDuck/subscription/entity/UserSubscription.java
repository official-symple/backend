package com.DreamOfDuck.subscription.entity;

import java.time.LocalDateTime;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.entity.Subscribe;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "user_subscription",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_subscription_member", columnNames = {"member_id"})
        },
        indexes = {
                @Index(name = "idx_user_subscription_member", columnList = "member_id"),
                @Index(name = "idx_user_subscription_member_status", columnList = "member_id,status")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class UserSubscription extends TimeStamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "memberId", nullable = false, unique = true)
    private Member member;

    @Column(name = "plan_code", length = 20, nullable = false)
    private String planCode; // Subscribe name

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private SubscriptionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform", length = 20, nullable = false)
    private StorePlatform platform;

    @Column(name = "product_id", length = 100)
    private String productId;

    // For idempotency / matching across verifications
    @Column(name = "store_subscription_id", length = 200)
    private String storeSubscriptionId;

    @Column(name = "store_transaction_id", length = 200)
    private String storeTransactionId;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "auto_renewing")
    private Boolean autoRenewing;

    @Column(name = "last_verified_at")
    private LocalDateTime lastVerifiedAt;

    public Subscribe getPlanAsSubscribe() {
        return Subscribe.valueOf(planCode);
    }

    public boolean isActiveAt(LocalDateTime now) {
        return status == SubscriptionStatus.ACTIVE && expiresAt != null && expiresAt.isAfter(now);
    }

    public void activatePremium(LocalDateTime now, LocalDateTime expiresAt, StorePlatform platform, String productId, String storeSubscriptionId, String storeTransactionId, Boolean autoRenewing) {
        this.planCode = Subscribe.PREMIUM.name();
        this.status = SubscriptionStatus.ACTIVE;
        this.platform = platform;
        this.productId = productId;
        this.startedAt = this.startedAt == null ? now : this.startedAt;
        this.expiresAt = expiresAt;
        this.storeSubscriptionId = storeSubscriptionId;
        this.storeTransactionId = storeTransactionId;
        this.autoRenewing = autoRenewing;
        this.lastVerifiedAt = now;
    }

    public void expire(LocalDateTime now) {
        this.status = SubscriptionStatus.EXPIRED;
        this.lastVerifiedAt = now;
    }
}


