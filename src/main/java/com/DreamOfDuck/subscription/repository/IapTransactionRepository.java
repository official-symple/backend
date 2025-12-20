package com.DreamOfDuck.subscription.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.subscription.entity.IapTransaction;
import com.DreamOfDuck.subscription.entity.VerificationStatusEnum;

public interface IapTransactionRepository extends JpaRepository<IapTransaction, Long> {
    
    Optional<IapTransaction> findTopByMemberOrderByCreatedAtDesc(Member member);
    
    Optional<IapTransaction> findByMemberAndOriginalTransactionId(Member member, String originalTransactionId);
    
    @Query("SELECT t FROM IapTransaction t WHERE t.verificationStatus = :status AND t.expiresAt < :now")
    List<IapTransaction> findExpiredSubscriptions(
        @Param("status") VerificationStatusEnum status,
        @Param("now") LocalDateTime now
    );
    
    @Query("SELECT t FROM IapTransaction t WHERE t.verificationStatus = 'VERIFIED' AND t.expiresAt BETWEEN :now AND :soon")
    List<IapTransaction> findExpiringSubscriptions(
        @Param("now") LocalDateTime now,
        @Param("soon") LocalDateTime soon
    );
}
