package com.DreamOfDuck.mind.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.global.exception.CustomException;
import com.DreamOfDuck.global.exception.ErrorCode;
import com.DreamOfDuck.mind.dto.request.MindCheckTimeRequest;
import com.DreamOfDuck.mind.dto.response.MindCheckTimeResponse;
import com.DreamOfDuck.mind.dto.response.PossibleTimeResponse;
import com.DreamOfDuck.mind.dto.response.TimeType;
import com.DreamOfDuck.mind.entity.MindCheckTime;
import com.DreamOfDuck.mind.entity.MindChecks;
import com.DreamOfDuck.mind.repository.MindCheckTimeRepository;
import com.DreamOfDuck.mind.repository.MindChecksRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MindCheckTimeService {
    private final MindCheckTimeRepository mindCheckTimeRepository;
    private final MindChecksRepository mindChecksRepository;
    public MindCheckTime getMindCheckTime(Member member, DayOfWeek dayOfWeek) {
        return member.getMindCheckTimes().stream()
                .filter(time->time.getDayOfWeek()==dayOfWeek)
                .findFirst().orElse(null);
    }
    public PossibleTimeResponse isPossibleTime(Member member) {
        ZoneId userZone = ZoneId.of(member.getLocation());
        LocalDateTime now = LocalDateTime.now(userZone);
        
        boolean isWithinDayTime = checkTime(member, userZone, now, TimePeriod.DAY);
        boolean isWithinNightTime = checkTime(member, userZone, now, TimePeriod.NIGHT);
        
        boolean possibleTime = isWithinDayTime || isWithinNightTime;
        TimeType timeType = null;
        
        if (possibleTime) {
            // 가까운 시간 타입 결정 (간단히 DAY 우선)
            timeType = isWithinDayTime ? TimeType.DAY : TimeType.NIGHT;
            
            // 이미 체크했는지 확인
            LocalDate checkDate = now.toLocalDate();
            if (now.toLocalTime().isBefore(LocalTime.of(6, 0))) {
                checkDate = checkDate.minusDays(1);
            }
            
            MindChecks mindChecks = mindChecksRepository.findByHostAndDate(member, checkDate)
                    .stream().findFirst().orElse(null);
            
            if (mindChecks != null) {
                if (isWithinDayTime && mindChecks.getDayMindCheck() != null) {
                    possibleTime = false;
                    timeType = null;
                } else if (isWithinNightTime && mindChecks.getNightMindCheck() != null) {
                    possibleTime = false;
                    timeType = null;
                }
            }
        }
        
        return PossibleTimeResponse.builder()
                .possibleTime(possibleTime)
                .timeType(timeType)
                .build();
    }
    
    public boolean checkTime(Member member, ZoneId userZone, LocalDateTime now, TimePeriod timePeriod) {
        MindCheckTime mindCheckTime = getMindCheckTime(member, now.getDayOfWeek());
        LocalTime dayTime, nightTime;
        if(mindCheckTime==null) {
            dayTime = ZonedDateTime.of(now.toLocalDate(), LocalTime.of(8,0),userZone).toLocalTime();
            nightTime = ZonedDateTime.of(now.toLocalDate(), LocalTime.of(23,0),userZone).toLocalTime();
        }else{
            dayTime = mindCheckTime.getDayTime();
            nightTime = mindCheckTime.getNightTime();
        }
        if(timePeriod==TimePeriod.DAY){
            return !now.toLocalTime().isBefore(dayTime) && !now.toLocalTime().isAfter(dayTime.plusHours(1));
        }else{
            LocalDateTime nightDateTime = LocalDateTime.of(now.toLocalDate(), nightTime);
            if(now.toLocalTime().isAfter(LocalTime.MIDNIGHT) && now.toLocalTime().isBefore(LocalTime.of(6, 0))) {
                if(!(nightTime.isAfter(LocalTime.MIDNIGHT) && nightTime.isBefore(LocalTime.of(6, 0)))) {
                    nightDateTime = nightDateTime.minusDays(1);
                }
            }
            LocalDateTime start = nightDateTime.minusHours(1);
            LocalDateTime end = nightDateTime.plusHours(1);
            return !now.isBefore(start) && !now.isAfter(end);
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
            //에러처리
            if (request.getDayTime().isBefore(LocalTime.of(6, 0)) || request.getDayTime().isAfter(LocalTime.of(13, 0))) {
                throw new CustomException(ErrorCode.IMPOSSIBLE_PERIOD);
            }
            LocalTime night = request.getNightTime();
            if (!((night.equals(LocalTime.of(18,0)) || night.isAfter(LocalTime.of(18,0)))
                    || (night.isBefore(LocalTime.of(4,0)) || night.equals(LocalTime.of(4,0))))) {
                throw new CustomException(ErrorCode.IMPOSSIBLE_PERIOD);
            }



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
        List<MindCheckTime> times = member.getMindCheckTimes();

        Map<DayOfWeek, MindCheckTime> timeMap = new HashMap<>();
        if (times != null) {
            for (MindCheckTime t : times) {
                timeMap.put(t.getDayOfWeek(), t);
            }
        }

        List<MindCheckTimeResponse> result = Arrays.stream(DayOfWeek.values())
                .map(day -> {
                    MindCheckTime t = timeMap.get(day);
                    if (t == null) {
                        t = MindCheckTime.builder()
                                .dayOfWeek(day)
                                .dayTime(LocalTime.of(8, 0))
                                .nightTime(LocalTime.of(23, 0))
                                .host(member)
                                .build();
                    }
                    return MindCheckTimeResponse.of(t);
                })
                .collect(Collectors.toList());

        return result;
    }
}
