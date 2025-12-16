package com.DreamOfDuck.subscription.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.entity.Subscribe;
import com.DreamOfDuck.account.repository.MemberRepository;
import com.DreamOfDuck.global.exception.CustomException;
import com.DreamOfDuck.global.exception.ErrorCode;
import com.DreamOfDuck.subscription.dto.request.VerifySubscriptionRequest;
import com.DreamOfDuck.subscription.dto.response.VerifySubscriptionResponse;
import com.DreamOfDuck.subscription.entity.IapTransaction;
import com.DreamOfDuck.subscription.entity.SubscriptionStatus;
import com.DreamOfDuck.subscription.entity.UserSubscription;
import com.DreamOfDuck.subscription.entity.VerificationStatus;
import com.DreamOfDuck.subscription.repository.IapTransactionRepository;
import com.DreamOfDuck.subscription.repository.UserSubscriptionRepository;
import com.DreamOfDuck.subscription.service.verify.VerificationCommand;
import com.DreamOfDuck.subscription.service.verify.VerificationResult;
import com.DreamOfDuck.subscription.service.verify.VerifierResolver;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final VerifierResolver verifierResolver;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final IapTransactionRepository iapTransactionRepository;
    private final MemberRepository memberRepository;
    private final SubscriptionReader subscriptionReader;

    @Transactional
    public VerifySubscriptionResponse verifyAndActivate(Member member, VerifySubscriptionRequest request) {
        if (request == null || request.getPlatform() == null) {
            throw new CustomException(ErrorCode.IAP_INVALID_REQUEST);
        }

        var verifier = verifierResolver.get(request.getPlatform());
        if (verifier == null) {
            throw new CustomException(ErrorCode.IAP_INVALID_REQUEST);
        }

        VerificationCommand command = VerificationCommand.builder()
                .platform(request.getPlatform())
                .productId(request.getProductId())
                .receiptData(request.getReceiptData())
                .purchaseToken(request.getPurchaseToken())
                .storeTransactionId(request.getStoreTransactionId())
                .storeSubscriptionId(request.getStoreSubscriptionId())
                .build();

        VerificationResult result = verifier.verify(command);
        LocalDateTime now = LocalDateTime.now();

        // Always store an audit record (success/fail)
        iapTransactionRepository.save(IapTransaction.builder()
                .member(member)
                .platform(request.getPlatform())
                .productId(request.getProductId())
                .storeSubscriptionId(result.getStoreSubscriptionId())
                .storeTransactionId(result.getStoreTransactionId())
                .verificationStatus(result.isValid() ? VerificationStatus.VERIFIED : VerificationStatus.FAILED)
                .verifiedAt(now)
                .requestPayload(summarizeRequest(request))
                .rawResponse(result.getRawResponse())
                .build());

        if (!result.isValid() || result.getExpiresAt() == null) {
            throw new CustomException(ErrorCode.IAP_VERIFICATION_FAILED);
        }

        UserSubscription subscription = userSubscriptionRepository.findByMember(member)
                .orElseGet(() -> UserSubscription.builder()
                        .member(member)
                        .planCode(Subscribe.FREE.name())
                        .status(SubscriptionStatus.EXPIRED)
                        .platform(request.getPlatform())
                        .build());

        subscription.activatePremium(
                now,
                result.getExpiresAt(),
                request.getPlatform(),
                request.getProductId(),
                result.getStoreSubscriptionId(),
                result.getStoreTransactionId(),
                result.getAutoRenewing()
        );
        userSubscriptionRepository.save(subscription);

        // Backward compatible flag used throughout the codebase
        member.setSubscribe(Subscribe.PREMIUM);
        memberRepository.save(member);

        return VerifySubscriptionResponse.builder()
                .success(true)
                .plan(Subscribe.PREMIUM)
                .expiresAt(result.getExpiresAt())
                .build();
    }

    @Transactional(readOnly = true)
    public PlanSnapshot myPlan(Member member) {
        return subscriptionReader.read(member);
    }

    /**
     * Safe effective plan resolution:
     * - PREMIUM is only valid when there is an ACTIVE + non-expired UserSubscription.
     * - member.subscribe is treated as a cache/backward-compat flag and can be synced to match the effective plan.
     */
    @Transactional
    public PlanSnapshot myPlanAndSyncMemberFlag(Member member) {
        PlanSnapshot snapshot = subscriptionReader.read(member);

        if (snapshot.isPremiumActive()) {
            if (member.getSubscribe() != Subscribe.PREMIUM) {
                member.setSubscribe(Subscribe.PREMIUM);
                memberRepository.save(member);
            }
            return snapshot;
        }

        if (member.getSubscribe() == Subscribe.PREMIUM) {
            member.setSubscribe(Subscribe.FREE);
            memberRepository.save(member);
        }
        return snapshot;
    }

    @Transactional(readOnly = true)
    public Subscribe effectivePlan(Member member) {
        return subscriptionReader.effectivePlan(member);
    }

    private String summarizeRequest(VerifySubscriptionRequest request) {
        String tokenHint = StringUtils.hasText(request.getPurchaseToken()) ? "purchaseToken=" + safeHint(request.getPurchaseToken()) : null;
        String receiptHint = StringUtils.hasText(request.getReceiptData()) ? "receiptData=" + safeHint(request.getReceiptData()) : null;
        return "platform=" + request.getPlatform()
                + ", productId=" + request.getProductId()
                + (tokenHint != null ? (", " + tokenHint) : "")
                + (receiptHint != null ? (", " + receiptHint) : "");
    }

    private String safeHint(String raw) {
        String t = raw.trim();
        return t.substring(0, Math.min(t.length(), 12));
    }
}


