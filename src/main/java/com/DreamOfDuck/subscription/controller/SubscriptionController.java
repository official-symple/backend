package com.DreamOfDuck.subscription.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.DreamOfDuck.account.entity.CustomUserDetails;
import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.service.MemberService;
import com.DreamOfDuck.subscription.dto.request.VerifySubscriptionRequest;
import com.DreamOfDuck.subscription.dto.response.MySubscriptionResponse;
import com.DreamOfDuck.subscription.dto.response.SubscriptionPlanResponse;
import com.DreamOfDuck.subscription.dto.response.VerifySubscriptionResponse;
import com.DreamOfDuck.subscription.repository.SubscriptionPlanRepository;
import com.DreamOfDuck.subscription.service.PlanSnapshot;
import com.DreamOfDuck.subscription.service.SubscriptionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscription")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final MemberService memberService;
    private final SubscriptionPlanRepository subscriptionPlanRepository;

    @GetMapping("/me")
    public ResponseEntity<MySubscriptionResponse> me(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        PlanSnapshot snapshot = subscriptionService.myPlanAndSyncMemberFlag(member);
        return ResponseEntity.ok(MySubscriptionResponse.builder()
                .plan(snapshot.getPlan())
                .premiumActive(snapshot.isPremiumActive())
                .expiresAt(snapshot.getExpiresAt())
                .unlimitedTalk(snapshot.isUnlimitedTalk())
                .adFree(snapshot.isAdFree())
                .dailyItem(snapshot.isDailyItem())
                .build());
    }

    @GetMapping("/plans")
    public ResponseEntity<List<SubscriptionPlanResponse>> plans() {
        return ResponseEntity.ok(subscriptionPlanRepository.findAll().stream()
                .map(SubscriptionPlanResponse::from)
                .toList());
    }

    @PostMapping("/verify")
    public ResponseEntity<VerifySubscriptionResponse> verify(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                            @RequestBody VerifySubscriptionRequest request) {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return ResponseEntity.ok(subscriptionService.verifyAndActivate(member, request));
    }
}


