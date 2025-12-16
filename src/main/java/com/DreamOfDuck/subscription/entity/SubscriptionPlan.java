package com.DreamOfDuck.subscription.entity;

import com.DreamOfDuck.account.entity.Subscribe;
import com.DreamOfDuck.global.entity.TimeStamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "subscription_plan")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class SubscriptionPlan extends TimeStamp {
    @Id
    @Column(name = "plan_code", length = 20)
    private String code; // Subscribe name: FREE, PREMIUM, ...

    @Column(name = "display_name", length = 50)
    private String displayName;

    // Benefits (requested booleans)
    @Column(name = "unlimited_talk", nullable = false)
    private boolean unlimitedTalk;

    @Column(name = "ad_free", nullable = false)
    private boolean adFree;

    @Column(name = "daily_item", nullable = false)
    private boolean dailyItem;

    public static SubscriptionPlan of(Subscribe subscribe, String displayName, boolean unlimitedTalk, boolean adFree, boolean dailyItem) {
        return SubscriptionPlan.builder()
                .code(subscribe.name())
                .displayName(displayName)
                .unlimitedTalk(unlimitedTalk)
                .adFree(adFree)
                .dailyItem(dailyItem)
                .build();
    }

    public Subscribe asSubscribe() {
        return Subscribe.valueOf(this.code);
    }
}


