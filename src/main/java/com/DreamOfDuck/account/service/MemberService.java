package com.DreamOfDuck.account.service;

import com.DreamOfDuck.account.dto.request.*;
import com.DreamOfDuck.account.dto.response.HomeResponse;
import com.DreamOfDuck.account.dto.response.MemberResponse;
import com.DreamOfDuck.account.entity.CustomUserDetails;
import com.DreamOfDuck.account.entity.Gender;
import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.entity.Role;
import com.DreamOfDuck.account.repository.MemberRepository;
import com.DreamOfDuck.global.exception.CustomException;
import com.DreamOfDuck.global.exception.ErrorCode;
import com.DreamOfDuck.goods.event.AttendanceCreatedEvent;
import com.DreamOfDuck.talk.entity.Cause;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;

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

        if(request.getNickname().length()>14 || request.getNickname().length()<2){
            throw new CustomException(ErrorCode.NICKNAME_LEN);
        }
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

        member.setLongestStreak(0);
        member.setCurStreak(0);

        member.setMaxScore(0);
        member.setHeart(2);
        member.setDia(0);
        member.setFeather(0);
        member.setLv(1);

        member.setDuckname("꽥꽥이");
        return MemberResponse.from(member);
    }
    @Transactional
    public MemberResponse updateMemberInfo(Member member, MemberUpdateRequest request) {
        if(member.getRole()!= Role.ROLE_USER){
            throw new CustomException(ErrorCode.DO_SIGNUP_FIRST);
        }
        if(request.getIsMarketing()!=null) member.setIsMarketing(request.getIsMarketing());
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
        eventPublisher.publishEvent(new AttendanceCreatedEvent(member.getEmail(), LocalDate.now()));

        return MemberResponse.from(member);
    }

    public HomeResponse getHomeInfo(Member member){
        return HomeResponse.from(member);
    }
    @Transactional
    public void addAttendance(Member member, LocalDate date){
        member.getAttendedDates().add(date);
    }

    @Transactional
    public HomeResponse updateHeart(Member member, HeartRequest request) {
        member.setHeart(request.getHeart());
        return HomeResponse.from(member);
    }
    @Transactional
    public HomeResponse updateDia(Member member, DiaRequest request) {
        member.setDia(request.getDia());
        return HomeResponse.from(member);
    }
    @Transactional
    public HomeResponse updateFeather(Member member, FeatherRequest request) {
        int totalFeather=member.getFeather()+request.getFeather();
        int curLv=member.getLv();
        int[] levelRequirements = {
                0, 150, 300, 450, 600, 1000, 1600, 2200, 2900, 3600,
                4300, 5000, 6000, 7000, 8000, 9000, 10000, 11500,
                13000, 14500, 16000, 17500, 19000, 21000, 23000,
                25000, 27000, 29000, 31000, 33000, 35000
        };
        int i;
        for(i=0;i<levelRequirements.length;i++){
            if(totalFeather<levelRequirements[i]) break;
        }

        if(curLv<i){
            curLv=i;
            totalFeather-=levelRequirements[i-1];
            member.setLv(curLv);
        }
        member.setFeather(totalFeather);

        Integer requiredFeather=levelRequirements[curLv];
        requiredFeather-=totalFeather;
        member.setRequiredFeather(requiredFeather);
        return HomeResponse.from(member);
    }
    @Transactional
    public HomeResponse updateLv(Member member, LvRequest request) {
        member.setLv(request.getLv());
        return HomeResponse.from(member);
    }
    @Transactional
    public HomeResponse updateDuckname(Member member, DucknameRequest request) {
        member.setDuckname(request.getDuckname());
        if(request.getDuckname().length()>14 || request.getDuckname().length()<2){
            throw new CustomException(ErrorCode.NICKNAME_LEN);
        }
        return HomeResponse.from(member);
    }

}