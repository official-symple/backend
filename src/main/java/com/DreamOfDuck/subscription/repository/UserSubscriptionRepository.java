package com.DreamOfDuck.subscription.repository;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.subscription.entity.SubscriptionStatus;
import com.DreamOfDuck.subscription.entity.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {
    Optional<UserSubscription> findByMember(Member member);
    Optional<UserSubscription> findByMemberAndStatus(Member member, SubscriptionStatus status);
}


