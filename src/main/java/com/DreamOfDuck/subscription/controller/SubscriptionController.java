package com.DreamOfDuck.subscription.controller;

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
import com.DreamOfDuck.subscription.dto.response.VerifySubscriptionResponse;
import com.DreamOfDuck.subscription.service.SubscriptionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscription")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final MemberService memberService;

    @GetMapping("/me")
    public ResponseEntity<MySubscriptionResponse> getMySubscription(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return ResponseEntity.ok(subscriptionService.getMySubscription(member));
    }

    @PostMapping("/verify")
    public ResponseEntity<VerifySubscriptionResponse> verify(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                            @RequestBody VerifySubscriptionRequest request) {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return ResponseEntity.ok(subscriptionService.verifyAndActivate(member, request));
    }
}
