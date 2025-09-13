package com.DreamOfDuck.goods.service;

import com.DreamOfDuck.account.dto.request.*;
import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.entity.Role;
import com.DreamOfDuck.global.exception.CustomException;
import com.DreamOfDuck.global.exception.ErrorCode;
import com.DreamOfDuck.goods.dto.request.DiaRequest;
import com.DreamOfDuck.goods.dto.request.FeatherRequest;
import com.DreamOfDuck.goods.dto.response.AttendanceByMonthResponse;
import com.DreamOfDuck.goods.dto.response.AttendanceResponse;
import com.DreamOfDuck.goods.dto.response.HomeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GoodsService {
    int[] levelRequirements = {
            0, 150, 300, 450, 600, 1000, 1600, 2200, 2900, 3600,
            4300, 5000, 6000, 7000, 8000, 9000, 10000, 11500,
            13000, 14500, 16000, 17500, 19000, 21000, 23000,
            25000, 27000, 29000, 31000, 33000, 35000
    };
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addAttendance(Member member, LocalDate date){
        member.getAttendedDates().add(date);
    }

    @Transactional
    public HomeResponse plusHeart(Member member, Integer cnt) {
        member.setHeart(member.getHeart()+cnt);
        HomeResponse res = HomeResponse.from(member);
        res.setRequiredFeather(levelRequirements[member.getLv()]);
        return res;
    }

    @Transactional
    public HomeResponse plusFeather(Member member, Integer cnt) {
        member.setFeather(member.getFeather()+cnt);
        HomeResponse res = HomeResponse.from(member);
        res.setRequiredFeather(levelRequirements[member.getLv()]);
        return res;
    }
    @Transactional
    public HomeResponse minusHeart(Member member, Integer cnt) {
        if(member.getRole()!= Role.ROLE_ADMIN) member.setHeart(member.getHeart()-cnt);
        HomeResponse res = HomeResponse.from(member);
        res.setRequiredFeather(levelRequirements[member.getLv()]);
        return res;
    }
    @Transactional
    public HomeResponse plusDia(Member member, DiaRequest request) {
        member.setDia(member.getDia()+request.getDia());
        HomeResponse res = HomeResponse.from(member);
        res.setRequiredFeather(levelRequirements[member.getLv()]);
        return res;
    }
    @Transactional
    public HomeResponse minusDia(Member member, DiaRequest request) {
        member.setDia(member.getDia()-request.getDia());
        HomeResponse res = HomeResponse.from(member);
        res.setRequiredFeather(levelRequirements[member.getLv()]);
        return res;
    }
    @Transactional
    public HomeResponse plusDiaAndFeather(Member member, DFRequest request) {
        member.setDia(member.getDia()+request.getDia());
        int totalFeather=member.getFeather()+request.getFeather();
        int curLv=member.getLv();

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
        HomeResponse res = HomeResponse.from(member);
        res.setRequiredFeather(levelRequirements[member.getLv()]);
        return res;
    }

    @Transactional
    public AttendanceResponse breakIce(Member member, LocalDate date) {

        if(member.getAttendedDates().contains(date)){
            throw new CustomException(ErrorCode.ATTENDANCE_EXIST);
        }
        addAttendance(member, date);
        Set<LocalDate> attendances = member.getAttendedDates();
        ZoneId userZone = ZoneId.of(member.getLocation()==null?"Asia/Seoul":member.getLocation());
        LocalDate now = LocalDate.now(userZone);
        NavigableSet<LocalDate> sortedDates = new TreeSet<>(attendances).descendingSet();
        updateStreak(member, sortedDates, now);
        return AttendanceResponse.fromMember(member);
    }
    private void updateStreak(Member member, NavigableSet<LocalDate> sortedDates, LocalDate now) {
        LocalDate lld = null;
        Integer longestStreak = 0;
        Integer cnt=0;
        for(LocalDate date : sortedDates){
            if(lld==null) {
                lld=date;
                cnt++;
            }
            else{
                if(lld.minusDays(1).equals(date)){
                    cnt++;
                }
                else{
                    if(cnt>longestStreak) longestStreak=cnt;
                    lld=date;
                    cnt=0;
                }
            }
        }
        if(member.getLongestStreak()<=longestStreak){
            member.setLongestStreak(longestStreak);
            member.setLastDayOfLongestStreak(lld);
            FeatherRequest request = new FeatherRequest();
            request.setFeather(checkAttendanceReward(longestStreak));
            updateFeather(member, request);
        }
        if(Objects.requireNonNull(lld).equals(now)){
            if(member.getCurStreak()<longestStreak){
                member.setCurStreak(longestStreak);
            }
        }

    }
    public Integer checkAttendanceReward(Integer streak) {
        Integer feather = 0;

        // 기본 깃털 지급
        switch (streak) {
            case 3 -> feather = 10;
            case 5 -> feather = 20;
            case 7 -> feather = 30;
            case 10 -> feather = 40;
            case 14 -> feather = 50;
            case 21 -> feather = 60;
            case 30 -> feather = 70;
            case 50 -> feather = 100;
        }
        if(streak>=100 && streak%50==0){
            feather=100;
        }
        return feather;
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
