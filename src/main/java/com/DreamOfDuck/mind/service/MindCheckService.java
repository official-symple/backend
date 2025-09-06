package com.DreamOfDuck.mind.service;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.account.service.MemberService;
import com.DreamOfDuck.global.exception.CustomException;
import com.DreamOfDuck.global.exception.ErrorCode;
import com.DreamOfDuck.mind.dto.response.MindCheckReport;
import com.DreamOfDuck.mind.dto.response.MindCheckResultResponse;
import com.DreamOfDuck.mind.dto.response.MindCheckTimeResponse;
import com.DreamOfDuck.mind.repository.MindCheckRepository;
import com.DreamOfDuck.mind.dto.request.MindCheckRequest;
import com.DreamOfDuck.mind.dto.response.MindCheckResponse;
import com.DreamOfDuck.mind.dto.request.MindCheckTimeRequest;
import com.DreamOfDuck.mind.entity.*;
import com.DreamOfDuck.mind.repository.MindCheckTimeRepository;
import com.DreamOfDuck.mind.repository.MindChecksRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MindCheckService {
    private final MindCheckRepository mindCheckRepository;
    private final MindChecksRepository mindChecksRepository;
    private final MindCheckTimeRepository mindCheckTimeRepository;
    private final MemberService memberService;

    @Transactional
    public MindCheckResponse checkMind(Member member, MindCheckRequest request) {

        ZoneId userZone = ZoneId.of(member.getLocation()); // ex: "Asia/Seoul"
        LocalDateTime now = LocalDateTime.now(userZone);
        TimePeriod timePeriod = TimePeriod.of(now);
        //접근 가능한 시간 이후면 에러처리
        if(!checkTime(member, userZone, now, timePeriod)) throw new CustomException(ErrorCode.NOT_PERMISSION_ACCESS);
        //6 to 6
        if(now.toLocalTime().isBefore(LocalTime.of(6,0))){
            now=now.minusDays(1);
        }
        LocalDate nowDate=now.toLocalDate();
        MindChecks mindChecks=memberService.getMindCheck(member, nowDate);
        MindCheck mindCheck = MindCheck.builder()
                .question1(request.isQuestion1())
                .question2(request.isQuestion2())
                .question3(request.isQuestion3())
                .negativeEmotion(request.getNegativeEmotion()==null?null: NegativeEmotion.fromId(request.getNegativeEmotion()))
                .positiveEmotion(request.getPositiveEmotion()==null?null: PositiveEmotion.fromId(request.getPositiveEmotion()))
                .createTime(now)
                .build();
        mindCheckRepository.save(mindCheck);
        //calculate score
        int cnt=0;
        if(request.isQuestion1()) cnt++;
        if(request.isQuestion2()) cnt++;
        if(request.isQuestion3()) cnt++;
        float score=100*cnt/3;
        mindCheck.setScore(score);

        if(mindChecks==null){
            mindChecks = new MindChecks();
            mindChecks.setDate(nowDate);
            mindChecks.addHost(member);
        }
        switch(timePeriod){
            case DAY:
                if(mindChecks.getDayMindCheck()!=null) throw new CustomException(ErrorCode.ALREADY_EXIST);
                mindChecks.setDayMindCheck(mindCheck);
                break;
            case NIGHT:
                if(mindChecks.getNightMindCheck()!=null) throw new CustomException(ErrorCode.ALREADY_EXIST);
                mindChecks.setNightMindCheck(mindCheck);
        }
        mindChecksRepository.save(mindChecks);
        return MindCheckResponse.fromMindCheck(mindCheck);
    }
    private boolean checkTime(Member member, ZoneId userZone, LocalDateTime now, TimePeriod timePeriod) {
        MindCheckTime mindCheckTime = memberService.getMindCheckTime(member, now.getDayOfWeek());
        LocalTime dayTime, nightTime;
        //푸시알림 시간 확인
        if(mindCheckTime==null) {
            dayTime = ZonedDateTime.of(now.toLocalDate(), LocalTime.of(8,0),userZone).toLocalTime();
            nightTime = ZonedDateTime.of(now.toLocalDate(), LocalTime.of(23,0),userZone).toLocalTime();
        }else{
            dayTime = mindCheckTime.getDayTime();
            nightTime = mindCheckTime.getNightTime();
        }
        //에러 처리
        if(timePeriod==TimePeriod.DAY){
            return !now.toLocalTime().isBefore(dayTime) && !now.toLocalTime().isAfter(dayTime.plusHours(1));
        }else{
            return !now.toLocalTime().isBefore(nightTime.minusHours(1)) && !now.toLocalTime().isAfter(nightTime.plusHours(1));
        }
    }
    @Transactional
    public List<MindCheckTimeResponse> setMindCheckTime(Member member, MindCheckTimeRequest request) {
        List<MindCheckTime> mindCheckTimes = member.getMindCheckTimes();

        // 적용할 요일 리스트
        List<DayOfWeek> targetDays;
        if (request.getDayOfWeek() == null) {
            // 월~금
            targetDays = Arrays.asList(
                    DayOfWeek.MONDAY,
                    DayOfWeek.TUESDAY,
                    DayOfWeek.WEDNESDAY,
                    DayOfWeek.THURSDAY,
                    DayOfWeek.FRIDAY,
                    DayOfWeek.SATURDAY,
                    DayOfWeek.SUNDAY
            );
        } else {
            targetDays = Collections.singletonList(DayOfWeek.valueOf(request.getDayOfWeek().toUpperCase()));
        }

        for (DayOfWeek day : targetDays) {
            Optional<MindCheckTime> existing = mindCheckTimes.stream()
                    .filter(time -> time.getDayOfWeek() == day)
                    .findFirst();

            if (existing.isPresent()) {
                existing.get().setDayTime(request.getDayTime());
                existing.get().setNightTime(request.getNightTime());
            } else {
                MindCheckTime mindCheckTime = MindCheckTime.builder()
                        .dayOfWeek(day)
                        .dayTime(request.getDayTime())
                        .nightTime(request.getNightTime())
                        .build();
                mindCheckTime.addHost(member);
                mindCheckTimeRepository.save(mindCheckTime);
            }
        }
        return getMindCheckTimes(member);
    }
    public List<MindCheckTimeResponse> getMindCheckTimes(Member member) {
        return member.getMindCheckTimes().stream()
                .map(MindCheckTimeResponse::of)
                .collect(Collectors.toList());
    }

    public MindCheckReport getMindCheckResult(Member member, LocalDate now) {
        MindChecks mindChecks = memberService.getMindCheck(member, now);
        float score=0;
        if(mindChecks==null) throw new CustomException(ErrorCode.NULL_MIND_CHECK);
        MindCheckReport response = MindCheckReport.of(mindChecks);
        if(mindChecks.getNightMindCheck()!=null && mindChecks.getDayMindCheck()!=null){
            score+= (float) (mindChecks.getDayMindCheck().getScore()*0.5);
            score+= (float) (mindChecks.getNightMindCheck().getScore()*0.5);
        }else if(mindChecks.getNightMindCheck()!=null){
            score+= (float) (mindChecks.getNightMindCheck().getScore());
        }else if(mindChecks.getDayMindCheck()!=null){
            score+= (float) (mindChecks.getDayMindCheck().getScore());
        }else throw new CustomException(ErrorCode.NULL_MIND_CHECK);
        if(score>=75){
            response.setResult("안정");
        }else if(score>=50){
            response.setResult("주의");
        }else if(score>=25){
            response.setResult("경고");
        }else{
            response.setResult("고위험");
        }
        return response;
    }

}
