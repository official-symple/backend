package com.DreamOfDuck.account.service;

import com.DreamOfDuck.account.dto.request.MemberRequest;
import com.DreamOfDuck.account.dto.response.MemberResponse;
import com.DreamOfDuck.account.entity.CustomUserDetails;
import com.DreamOfDuck.account.entity.Gender;
import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.entity.Role;
import com.DreamOfDuck.account.repository.MemberRepository;
import com.DreamOfDuck.global.exception.CustomException;
import com.DreamOfDuck.global.exception.ErrorCode;
import com.DreamOfDuck.talk.entity.Cause;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    public Member findMemberByEmail(String email){
        return memberRepository.findByEmail(email).orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_EXIST));
    }

    @Transactional
    public MemberResponse join(Member member, MemberRequest request) {
        if(member.getRole()== Role.ROLE_USER){
            throw new CustomException(ErrorCode.USER_ALREADY_EXIST);
        }
        member.setRole(Role.ROLE_USER);
        member.setIsMarketing(request.getIsMarketing());
        member.setBirthday(request.getBirthday());
        member.setNickname(request.getNickname());
        member.setGender(Gender.valueOf(request.getGender().toUpperCase()));
        member.setConcern(Cause.fromId(request.getConcern()));
        member.setBlue(request.getBlue());
        return MemberResponse.from(member);
    }
}
