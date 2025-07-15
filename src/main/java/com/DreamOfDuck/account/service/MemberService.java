package com.DreamOfDuck.account.service;

import com.DreamOfDuck.account.dto.request.MemberCreateRequest;
import com.DreamOfDuck.account.dto.request.MemberUpdateRequest;
import com.DreamOfDuck.account.dto.request.ScoreRequest;
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
    public MemberResponse join(Member member, MemberCreateRequest request) {
        if(member.getRole()== Role.ROLE_USER){
            throw new CustomException(ErrorCode.USER_ALREADY_EXIST);
        }
        member.setRole(Role.ROLE_USER);
        member.setIsMarketing(request.getIsMarketing());
        member.setBirthday(request.getBirthday());
        member.setNickname(request.getNickname());
        member.setGender(Gender.valueOf(request.getGender().toUpperCase()));
        member.setConcern(Cause.fromId(request.getConcern()));
        if(request.getStatus().size()!=9){
            throw new CustomException(ErrorCode.NINE_STATUS);
        }
        member.setStatus(request.getStatus());
        int sum = request.getStatus().stream().mapToInt(Integer::intValue).sum();
        if(0<=sum&&sum<=4) member.setTotalStatus("우울 아님");
        else if(sum<=9) member.setTotalStatus("가벼운 우울");
        else if(sum<=19) member.setTotalStatus("중간 정도의 우울");
        else member.setTotalStatus("심한 우울");
        member.setMaxScore(0);
        return MemberResponse.from(member);
    }
    @Transactional
    public MemberResponse updateMemberInfo(Member member, MemberUpdateRequest request) {
        if(member.getRole()!= Role.ROLE_USER){
            throw new CustomException(ErrorCode.DO_SIGNUP_FIRST);
        }
        member.setIsMarketing(request.getIsMarketing());
        if(request.getBirthday()!=null) member.setBirthday(request.getBirthday());
        if(request.getNickname()!=null) member.setNickname(request.getNickname());
        if(request.getGender()!=null) member.setGender(Gender.valueOf(request.getGender().toUpperCase()));
        if(request.getConcern()!=null) member.setConcern(Cause.fromId(request.getConcern()));
        if(request.getStatus()!=null){
            if(request.getStatus().size()!=9){
                throw new CustomException(ErrorCode.NINE_STATUS);
            }
            member.setStatus(request.getStatus());
            int sum = request.getStatus().stream().mapToInt(Integer::intValue).sum();
            if(0<=sum&&sum<=4) member.setTotalStatus("우울 아님");
            else if(sum<=9) member.setTotalStatus("가벼운 우울");
            else if(sum<=19) member.setTotalStatus("중간 정도의 우울");
            else member.setTotalStatus("심한 우울");
        }
        return MemberResponse.from(member);
    }
    @Transactional
    public MemberResponse updateScore(Member member, ScoreRequest request) {
        if(member.getMaxScore()==null){
            member.setMaxScore(request.getScore());
        }
        else if(member.getMaxScore()<request.getScore()){
            member.setMaxScore(request.getScore());
        }
        return MemberResponse.from(member);
    }
}
