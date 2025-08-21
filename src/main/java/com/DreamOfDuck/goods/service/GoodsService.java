package com.DreamOfDuck.goods.service;

import com.DreamOfDuck.account.dto.request.*;
import com.DreamOfDuck.account.dto.response.AttendanceByMonthResponse;
import com.DreamOfDuck.account.dto.response.AttendanceResponse;
import com.DreamOfDuck.account.dto.response.HomeResponse;
import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.global.exception.CustomException;
import com.DreamOfDuck.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GoodsService {

    @Transactional
    public void addAttendance(Member member, LocalDate date){
        member.getAttendedDates().add(date);
    }

    @Transactional
    public HomeResponse plusHeart(Member member, Integer cnt) {
        member.setHeart(member.getHeart()+cnt);
        return HomeResponse.from(member);
    }
    @Transactional
    public HomeResponse minusHeart(Member member, Integer cnt) {
        member.setHeart(member.getHeart()-cnt);
        return HomeResponse.from(member);
    }
    @Transactional
    public HomeResponse plusDia(Member member, DiaRequest request) {
        member.setDia(member.getDia()+request.getDia());
        return HomeResponse.from(member);
    }
    @Transactional
    public HomeResponse minusDia(Member member, DiaRequest request) {
        member.setDia(member.getDia()-request.getDia());
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
        HomeResponse res = HomeResponse.from(member);
        res.setRequiredFeather(levelRequirements[curLv]);

        return res;
    }

    @Transactional
    public HomeResponse updateDuckname(Member member, DucknameRequest request) {
        member.setDuckname(request.getDuckname());
        if(request.getDuckname().length()>14 || request.getDuckname().length()<2){
            throw new CustomException(ErrorCode.NICKNAME_LEN);
        }
        return HomeResponse.from(member);
    }

    public List<AttendanceByMonthResponse> getAttendanceByMonth(Member member, YearMonth yearMonth){
        return member.getAttendedDates()
                .stream()
                .filter(d->d.getYear()==yearMonth.getYear() && d.getMonth()==yearMonth.getMonth())
                .sorted()
                .map(d->AttendanceByMonthResponse.builder()
                        .attendedDate(d)
                        .build())
                .collect(Collectors.toList());
    }
    public AttendanceResponse getAttendance(Member member){
        return AttendanceResponse.fromMember(member);
    }
}
