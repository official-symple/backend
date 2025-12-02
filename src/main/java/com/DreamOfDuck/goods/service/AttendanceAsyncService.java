package com.DreamOfDuck.goods.service;

import com.DreamOfDuck.account.entity.Attendance;
import com.DreamOfDuck.goods.dto.request.FeatherRequest;
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
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttendanceAsyncService {

    private final MemberService memberService;
    private final GoodsService goodsService;

    @Transactional
    @Async("threadPoolTaskExecutor")
    public void addAttendance(String email, LocalDate curDate) {
        Member member = memberService.findMemberByEmail(email);
        Set<Attendance> attendedDates = member.getAttendedDates();

        boolean alreadyAttended = attendedDates.stream()
                .anyMatch(a -> a.getDate().equals(curDate));
        if (alreadyAttended) {
            return;
        }
        goodsService.addAttendance(member, curDate, false);
        // 정렬된 TreeSet으로 변환해서 streak 계산
        attendedDates.add(Attendance.builder().date(curDate).isIce(false).build());
        NavigableSet<LocalDate> sortedDates = attendedDates.stream()
                .map(Attendance::getDate)
                .collect(Collectors.toCollection(TreeSet::new));
        Integer newCurStreak = calculateCurrentStreak(sortedDates, curDate, member.getCurStreak());
        member.setCurStreak(newCurStreak);
        //longest streak
        updateLongestStreak(member, curDate, newCurStreak);
        //reward
        Integer feather = goodsService.checkAttendanceReward(newCurStreak);
        if(member.getFeatherByAttendance()==null){
            member.setFeatherByAttendance(feather);
        }else{
            member.setFeatherByAttendance(member.getFeatherByAttendance()+feather);
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
    private void updateLongestStreak(Member member, LocalDate curDate, Integer newCurStreak) {
        if (newCurStreak > member.getLongestStreak()) {
            member.setLongestStreak(newCurStreak);
            member.setLastDayOfLongestStreak(curDate);
        }
    }


}
