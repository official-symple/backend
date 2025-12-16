package com.DreamOfDuck.subscription.dto.response;

import com.DreamOfDuck.subscription.entity.SubscriptionPlan;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubscriptionPlanResponse {
    private String code;
    private String displayName;
    private boolean unlimitedTalk;
    private boolean adFree;
    private boolean dailyItem;

    public static SubscriptionPlanResponse from(SubscriptionPlan plan) {
        return SubscriptionPlanResponse.builder()
                .code(plan.getCode())
                .displayName(plan.getDisplayName())
                .unlimitedTalk(plan.isUnlimitedTalk())
                .adFree(plan.isAdFree())
                .dailyItem(plan.isDailyItem())
                .build();
    }
}


