package com.DreamOfDuck.goods.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.DreamOfDuck.account.dto.request.DFRequest;
import com.DreamOfDuck.account.dto.request.DucknameRequest;
import com.DreamOfDuck.account.entity.Attendance;
import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.entity.Role;
import com.DreamOfDuck.account.repository.MemberRepository;
import com.DreamOfDuck.global.exception.CustomException;
import com.DreamOfDuck.global.exception.ErrorCode;
import com.DreamOfDuck.goods.dto.request.DiaRequest;
import com.DreamOfDuck.goods.dto.request.FeatherRequest;
import com.DreamOfDuck.goods.dto.response.AttendanceByMonthResponse;
import com.DreamOfDuck.goods.dto.response.AttendanceResponse;
import com.DreamOfDuck.goods.dto.response.HomeResponse;
import com.DreamOfDuck.goods.dto.response.RewardResponse;
import com.DreamOfDuck.pang.entity.Item;
import com.DreamOfDuck.pang.service.ItemService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GoodsService {
    private final ItemService itemService;
    private final MemberRepository memberRepository;
    int[] levelRequirements = {
            0, 150, 300, 450, 600, 1000, 1600, 2200, 2900, 3600,
            4300, 5000, 6000, 7000, 8000, 9000, 10000, 11500,
            13000, 14500, 16000, 17500, 19000, 21000, 23000,
            25000, 27000, 29000, 31000, 33000, 35000
    };


    @Transactional
    public RewardResponse addAttendance(Member member, LocalDate date) {
        Set<Attendance> attendedDates = member.getAttendedDates();

        boolean alreadyAttended = attendedDates.stream()
                .anyMatch(a -> a.getDate().equals(date));
        if (alreadyAttended) {
            throw new CustomException(ErrorCode.ATTENDANCE_EXIST);
        }
        member.getAttendedDates().add(Attendance.builder().date(date).build());

        // 정렬된 TreeSet으로 변환해서 streak 계산
        attendedDates.add(Attendance.builder().date(date).build());
        NavigableSet<LocalDate> sortedDates = attendedDates.stream()
                .map(Attendance::getDate)
                .collect(Collectors.toCollection(TreeSet::new));
        Integer newCurStreak = calculateCurrentStreak(sortedDates, date, member.getCurStreak());
        member.setCurStreak(newCurStreak);
        //longest streak
        updateLongestStreak(member, date, newCurStreak);
        //reward
        Integer feather = checkAttendanceReward(newCurStreak);
        if(member.getFeatherByAttendance()==null){
            member.setFeatherByAttendance(feather);
        }else{
            member.setFeatherByAttendance(member.getFeatherByAttendance()+feather);
        }

        return getRewardByAttendance(member);
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
    @Transactional
    public HomeResponse plusHeart(Member member, Integer cnt) {
        member.setHeart(member.getHeart()+cnt);
        HomeResponse res = HomeResponse.from(member);
        res.setRequiredFeather(levelRequirements[member.getLv()]);
        return res;
    }

    @Transactional
    public HomeResponse minusHeart(Member member, Integer cnt) {
        member.setHeart(member.getHeart()-cnt);
        HomeResponse res = HomeResponse.from(member);
        res.setRequiredFeather(levelRequirements[member.getLv()]);
        return res;
    }
    @Transactional
    public HomeResponse minusCntTalk(Member member) {
        if(member.getRole() != Role.ROLE_PREMIUM) member.setCntTalk(member.getCntTalk()-1);
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


    private void updateStreak(Member member, NavigableSet<LocalDate> sortedDates, LocalDate now) {
        //오늘 포함 최장 연속 출석 업뎃
        Integer curStreak = 0;
        if (!sortedDates.isEmpty() && sortedDates.first().equals(now)) {
            LocalDate prev = null;
            for(LocalDate date : sortedDates) {
                if(prev==null) {
                    curStreak++;
                    prev=date;
                }
                else{
                    if(date.plusDays(1).equals(prev)) {
                        curStreak++;
                        prev=date;
                    }
                    else{
                        break;
                    }
                }
                log.info(String.valueOf(prev));
            }
        }
        member.setCurStreak(curStreak);
        //최장 연속 출석 업뎃
        LocalDate lld = null;
        int cnt = 0;
        int longestStreak = 0;
        LocalDate prev = null;

        for(LocalDate date : sortedDates){
            if(prev == null || !prev.minusDays(1).equals(date)) cnt = 1;
            else cnt++;
            prev = date;

            if(cnt > longestStreak){
                longestStreak = cnt;
                lld = date;
            }
        }

        Integer feather=0;
        if(member.getLongestStreak()<=longestStreak){
            feather=checkAttendanceReward(longestStreak);
            member.setLongestStreak(longestStreak);
            member.setLastDayOfLongestStreak(lld);
            //깃털 추가
            if(member.getFeatherByAttendance()==null){
                member.setFeatherByAttendance(feather);
            }else {
                member.setFeatherByAttendance(member.getFeatherByAttendance() + feather);
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
                .filter(info->info.getDate().getYear()==yearMonth.getYear() && info.getDate().getMonth()==yearMonth.getMonth())
                .sorted()
                .map(info->AttendanceByMonthResponse.builder()
                        .attendedDate(info.getDate())
                        .build())
                .collect(Collectors.toList());
    }
    public AttendanceResponse getAttendance(Member member){
        return AttendanceResponse.fromMember(member);
    }
    @Async("threadPoolTaskExecutor")
    @Transactional
    public void setHeartAsync(Long memberId, Integer cnt) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_EXIST));

        member.setHeart(cnt);
        log.info("Member {} 하트 리셋 완료: {}", memberId, cnt);
    }

    @Async("threadPoolTaskExecutor")
    @Transactional
    public void setHeartAsync(Member member, Integer cnt) {
        member.setHeart(cnt);
    }
    private RewardResponse getRewardByAttendance(Member member){
        Integer feather = member.getFeatherByAttendance();
        if(feather==null) feather=0;
        member.setFeatherByAttendance(0);
        FeatherRequest req = new FeatherRequest();
        req.setFeather(feather);
        updateFeather(member, req);
        RewardResponse res = RewardResponse.builder()
                .featherReward(feather)
                .bubblePang(0L)
                .breadCrumble(0L)
                .tornado(0L)
                .grass(0L)
                .build();
        if(member.getRole()==Role.ROLE_PREMIUM){
            Item item = itemService.getOrCreateItem(member);
            if(item.getBreadCrumble()==0L){
                res.setBreadCrumble(1L);
                itemService.updateBreadCrumble(member);
            }
            if(item.getBubblePang()==0L){
                res.setBubblePang(1L);
                itemService.updateBubblePang(member);
            }
            if(item.getTornado()==0L){
                res.setTornado(1L);
                itemService.updateTornado(member);
            }
            if(item.getGrass()==0L){
                res.setGrass(1L);
                itemService.updateGrass(member);
            }
        }
        return res;
    }

}
