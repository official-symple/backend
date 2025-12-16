package com.DreamOfDuck.subscription.config;

import com.DreamOfDuck.account.entity.Subscribe;
import com.DreamOfDuck.subscription.entity.SubscriptionPlan;
import com.DreamOfDuck.subscription.repository.SubscriptionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionPlanSeeder implements ApplicationRunner {
    private final SubscriptionPlanRepository planRepository;

    @Override
    public void run(ApplicationArguments args) {
        // Only seed the two plans you asked for (FREE / PREMIUM).
        upsert(SubscriptionPlan.of(Subscribe.FREE, "Free", false, false, false));
        upsert(SubscriptionPlan.of(Subscribe.PREMIUM, "Premium", true, true, true));
    }

    private void upsert(SubscriptionPlan plan) {
        if (!planRepository.existsById(plan.getCode())) {
            planRepository.save(plan);
        }
    }
}


