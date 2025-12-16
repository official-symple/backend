package com.DreamOfDuck.subscription.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.entity.Subscribe;
import com.DreamOfDuck.global.exception.CustomException;
import com.DreamOfDuck.global.exception.ErrorCode;
import com.DreamOfDuck.subscription.entity.SubscriptionPlan;
import com.DreamOfDuck.subscription.entity.SubscriptionStatus;
import com.DreamOfDuck.subscription.entity.UserSubscription;
import com.DreamOfDuck.subscription.repository.SubscriptionPlanRepository;
import com.DreamOfDuck.subscription.repository.UserSubscriptionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DefaultSubscriptionReader implements SubscriptionReader {
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final SubscriptionPlanRepository planRepository;

    @Override
    public PlanSnapshot read(Member member) {
        LocalDateTime now = LocalDateTime.now();

        Optional<UserSubscription> active = userSubscriptionRepository.findByMemberAndStatus(member, SubscriptionStatus.ACTIVE)
                .filter(s -> s.isActiveAt(now));

        Subscribe memberPlan = safeMemberSubscribe(member);
        // Safety rule: PREMIUM is valid only when we have an ACTIVE + non-expired subscription record.
        Subscribe planCode = active.isPresent()
                ? Subscribe.PREMIUM
                : (memberPlan == Subscribe.PREMIUM ? Subscribe.FREE : memberPlan);

        SubscriptionPlan plan = planRepository.findById(planCode.name())
                .orElseThrow(() -> new CustomException(ErrorCode.IAP_PLAN_NOT_FOUND));

        return PlanSnapshot.builder()
                .plan(planCode)
                .unlimitedTalk(plan.isUnlimitedTalk())
                .adFree(plan.isAdFree())
                .dailyItem(plan.isDailyItem())
                .premiumActive(active.isPresent())
                .expiresAt(active.map(UserSubscription::getExpiresAt).orElse(null))
                .build();
    }

    @Override
    public boolean isPremium(Member member) {
        return read(member).isPremiumActive();
    }

    private Subscribe safeMemberSubscribe(Member member) {
        return member.getSubscribe() == null ? Subscribe.FREE : member.getSubscribe();
    }
}


