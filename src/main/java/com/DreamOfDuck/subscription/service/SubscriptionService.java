package com.DreamOfDuck.subscription.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.entity.Role;
import com.DreamOfDuck.account.repository.MemberRepository;
import com.DreamOfDuck.global.exception.CustomException;
import com.DreamOfDuck.global.exception.ErrorCode;
import com.DreamOfDuck.subscription.dto.request.VerifySubscriptionRequest;
import com.DreamOfDuck.subscription.dto.response.MySubscriptionResponse;
import com.DreamOfDuck.subscription.dto.response.VerifySubscriptionResponse;
import com.DreamOfDuck.subscription.entity.IapTransaction;
import com.DreamOfDuck.subscription.entity.VerificationStatusEnum;
import com.DreamOfDuck.subscription.repository.IapTransactionRepository;
import com.DreamOfDuck.subscription.service.verify.VerificationResult;
import com.DreamOfDuck.subscription.service.verify.VerifierResolver;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final VerifierResolver verifierResolver;
    private final IapTransactionRepository iapTransactionRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public VerifySubscriptionResponse verifyAndActivate(Member member, VerifySubscriptionRequest request) {
        if (request == null || request.getPlatform() == null) {
            throw new CustomException(ErrorCode.IAP_INVALID_REQUEST);
        }

        var verifier = verifierResolver.get(request.getPlatform());
        if (verifier == null) {
            throw new CustomException(ErrorCode.IAP_INVALID_REQUEST);
        }

        VerificationResult result = verifier.verify(request);
        LocalDateTime now = LocalDateTime.now();

        if (!result.isValid() || result.getExpiresAt() == null) {
            // Log failed attempt
            iapTransactionRepository.save(IapTransaction.builder()
                    .member(member)
                    .platform(request.getPlatform())
                    .productId(request.getProductId())
                    .storeTransactionId(request.getStoreTransactionId())
                    .verificationStatus(VerificationStatusEnum.FAILED)
                    .verifiedAt(now)
                    .rawResponse(result.getRawResponse())
                    .build());
            throw new CustomException(ErrorCode.IAP_VERIFICATION_FAILED);
        }

        // Find existing transaction by originalTransactionId or create new one
        IapTransaction transaction = Optional.ofNullable(result.getOriginalTransactionId())
                .flatMap(origId -> iapTransactionRepository.findByMemberAndOriginalTransactionId(member, origId))
                .orElseGet(() -> IapTransaction.builder()
                        .member(member)
                        .platform(request.getPlatform())
                        .productId(request.getProductId())
                        .storeTransactionId(result.getStoreTransactionId())
                        .originalTransactionId(result.getOriginalTransactionId())
                        .startedAt(result.getStartedAt() != null ? result.getStartedAt() : now)
                        .verificationStatus(VerificationStatusEnum.PENDING)
                        .build());

        transaction.updateFromVerification(
                result.getExpiresAt(),
                result.getIsTrialPeriod(),
                result.getAutoRenewing(),
                result.getRawResponse()
        );
        iapTransactionRepository.save(transaction);

        // Update member role to PREMIUM - reload member to ensure it's in persistence context
        Member managedMember = memberRepository.findById(member.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXIST));
        if (result.getExpiresAt().isAfter(now)) {
            managedMember.setRole(Role.ROLE_PREMIUM);
        } else {
            managedMember.setRole(Role.ROLE_USER);
        }
        memberRepository.save(managedMember);

        return VerifySubscriptionResponse.builder()
                .success(true)
                .startedAt(transaction.getStartedAt())
                .expiresAt(result.getExpiresAt())
                .isTrialPeriod(result.getIsTrialPeriod())
                .build();
    }

    @Transactional(readOnly = true)
    public MySubscriptionResponse getMySubscription(Member member) {
        Optional<IapTransaction> latestTransaction = iapTransactionRepository.findTopByMemberOrderByCreatedAtDesc(member);
        
        if (latestTransaction.isEmpty() || !latestTransaction.get().isActive()) {
            return MySubscriptionResponse.builder()
                    .isPremium(false)
                    .build();
        }

        IapTransaction transaction = latestTransaction.get();
        return MySubscriptionResponse.builder()
                .isPremium(true)
                .startedAt(transaction.getStartedAt())
                .expiresAt(transaction.getExpiresAt())
                .isTrialPeriod(transaction.getIsTrialPeriod())
                .build();
    }

    @Transactional
    public void syncMemberRole(Member member) {
        Optional<IapTransaction> latestTransaction = iapTransactionRepository.findTopByMemberOrderByCreatedAtDesc(member);
        
        boolean isPremium = latestTransaction.isPresent() && latestTransaction.get().isActive();
        
        if (isPremium && member.getRole() != Role.ROLE_PREMIUM) {
            member.setRole(Role.ROLE_PREMIUM);
            memberRepository.save(member);
        } else if (!isPremium && member.getRole() == Role.ROLE_PREMIUM) {
            member.setRole(Role.ROLE_USER);
            memberRepository.save(member);
        }
    }

    @Transactional(readOnly = true)
    public boolean isPremium(Member member) {
        Optional<IapTransaction> latestTransaction = iapTransactionRepository.findTopByMemberOrderByCreatedAtDesc(member);
        return latestTransaction.isPresent() && latestTransaction.get().isActive();
    }
}
