package com.DreamOfDuck.subscription.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.DreamOfDuck.account.entity.Role;
import com.DreamOfDuck.account.repository.MemberRepository;
import com.DreamOfDuck.subscription.entity.IapTransaction;
import com.DreamOfDuck.subscription.entity.VerificationStatusEnum;
import com.DreamOfDuck.subscription.repository.IapTransactionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionExpirationScheduler {
    private final IapTransactionRepository iapTransactionRepository;
    private final MemberRepository memberRepository;

    /**
     * Runs every hour to check for expired subscriptions
     * and update member roles accordingly.
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void checkExpiredSubscriptions() {
        LocalDateTime now = LocalDateTime.now();
        
        List<IapTransaction> expiredTransactions = iapTransactionRepository
                .findExpiredSubscriptions(VerificationStatusEnum.VERIFIED, now);
        
        for (IapTransaction transaction : expiredTransactions) {
            try {
                transaction.markExpired();
                iapTransactionRepository.save(transaction);
                
                // Update member role to USER
                var member = transaction.getMember();
                if (member.getRole() == Role.ROLE_PREMIUM) {
                    member.setRole(Role.ROLE_USER);
                    memberRepository.save(member);
                    log.info("Subscription expired for member: {}, role changed to ROLE_USER", member.getId());
                }
            } catch (Exception e) {
                log.error("Failed to process expired subscription for transaction: {}", transaction.getId(), e);
            }
        }
        
        if (!expiredTransactions.isEmpty()) {
            log.info("Processed {} expired subscriptions", expiredTransactions.size());
        }
    }
}

