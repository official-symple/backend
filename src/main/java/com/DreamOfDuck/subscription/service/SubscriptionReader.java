package com.DreamOfDuck.subscription.service;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.entity.Subscribe;

public interface SubscriptionReader {
    PlanSnapshot read(Member member);
    boolean isPremium(Member member);

    default Subscribe effectivePlan(Member member) {
        return read(member).getPlan();
    }
}


