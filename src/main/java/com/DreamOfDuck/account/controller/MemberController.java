package com.DreamOfDuck.account.controller;

import com.DreamOfDuck.account.dto.request.MemberRequest;
import com.DreamOfDuck.account.dto.response.MemberResponse;
import com.DreamOfDuck.account.entity.CustomUserDetails;
import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {
    private final MemberService memberService;
    @PostMapping("signup")
    public MemberResponse signup(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody @Valid MemberRequest request) {
        Member member = memberService.findMemberByEmail(customUserDetails.getUsername());
        return memberService.join(member, request);
    }

}
