package com.DreamOfDuck.account.service;

import com.DreamOfDuck.account.entity.Member;
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

    @Transactional
    @Async
    public void addAttendance(String email, LocalDate curDate) {
        Member member = memberService.findMemberByEmail(email);

        Set<LocalDate> attendedDates = member.getAttendedDates();

        if (attendedDates.contains(curDate)) {
            return;
        }
        memberService.addAttendance(member, curDate);
        // 정렬된 TreeSet으로 변환해서 streak 계산
        NavigableSet<LocalDate> sortedDates = new TreeSet<>(attendedDates);
        Integer newCurStreak = calculateCurrentStreak(sortedDates, curDate, member.getCurStreak());
        member.setCurStreak(newCurStreak);

        updateLongestStreak(member, curDate, newCurStreak);

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
            return prevStreak + 1;
        } else {
            return 1;
        }
    }
}
