package com.DreamOfDuck.goods.service;

import com.DreamOfDuck.account.dto.request.FeatherRequest;
import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.NavigableSet;
import java.util.Set;

import java.util.TreeSet;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttendanceAsyncService {

    private final MemberService memberService;
    private final GoodsService goodsService;

    @Transactional
    @Async
    public void addAttendance(String email, LocalDate curDate) {
        Member member = memberService.findMemberByEmail(email);
        Set<LocalDate> attendedDates = member.getAttendedDates();

        if (attendedDates.contains(curDate)) {
            return;
        }
        goodsService.addAttendance(member, curDate);
        // 정렬된 TreeSet으로 변환해서 streak 계산
        NavigableSet<LocalDate> sortedDates = new TreeSet<>(attendedDates);
        Integer newCurStreak = calculateCurrentStreak(sortedDates, curDate, member.getCurStreak());
        member.setCurStreak(newCurStreak);
        //longest streak
        updateLongestStreak(member, curDate, newCurStreak);
        //reward
        Integer updatedFeather = checkAttendanceReward(member, newCurStreak);
        FeatherRequest request = new FeatherRequest();
        request.setFeather(updatedFeather);
        goodsService.updateFeather(member, request);
    }

    private void updateLongestStreak(Member member, LocalDate curDate, Integer newCurStreak) {
        if (newCurStreak > member.getLongestStreak()) {
            member.setLongestStreak(newCurStreak);
            member.setLastDayOfLongestStreak(curDate);
        }
    }

    private Integer calculateCurrentStreak(NavigableSet<LocalDate> sortedDates, LocalDate curDate, Integer prevStreak) {
        if (sortedDates.isEmpty()) {
            return 1;
        }

        LocalDate lastDateBefore = sortedDates.lower(curDate); // curDate보다 바로 전 날짜
        if (lastDateBefore != null && lastDateBefore.plusDays(1).equals(curDate)) {
            return prevStreak+1;
        } else {
            return 1;
        }
    }

    public Integer checkAttendanceReward(Member member, Integer streak) {
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
}
