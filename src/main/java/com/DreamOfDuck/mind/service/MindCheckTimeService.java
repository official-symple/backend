package com.DreamOfDuck.mind.service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
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
import com.DreamOfDuck.mind.repository.MindCheckTimeRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MindCheckTimeService {
    private final MindCheckTimeRepository mindCheckTimeRepository;
    public MindCheckTime getMindCheckTime(Member member, DayOfWeek dayOfWeek) {
        return member.getMindCheckTimes().stream()
                .filter(time->time.getDayOfWeek()==dayOfWeek)
                .findFirst().orElse(null);
    }
    public PossibleTimeResponse isPossibleTime(Member member) {
        ZoneId userZone = ZoneId.of(member.getLocation());
        LocalTime now = LocalTime.now(userZone);
        DayOfWeek currentDay = DayOfWeek.from(java.time.LocalDate.now(userZone));
        
        MindCheckTime mindCheckTime = getMindCheckTime(member, currentDay);
        
        LocalTime dayTime = mindCheckTime != null ? mindCheckTime.getDayTime() : LocalTime.of(8, 0);
        LocalTime nightTime = mindCheckTime != null ? mindCheckTime.getNightTime() : LocalTime.of(21, 0);
        
        boolean isWithinDayTime = isWithinOneHour(now, dayTime);
        boolean isWithinNightTime = isWithinOneHour(now, nightTime);
        
        boolean possibleTime = isWithinDayTime || isWithinNightTime;
        TimeType timeType = null;
        
        if (possibleTime) {
            long dayDiff = getMinutesDifference(now, dayTime);
            long nightDiff = getMinutesDifference(now, nightTime);
            timeType = dayDiff <= nightDiff ? TimeType.DAY : TimeType.NIGHT;
        }
        
        return PossibleTimeResponse.builder()
                .possibleTime(possibleTime)
                .timeType(timeType)
                .build();
    }
    
    private boolean isWithinOneHour(LocalTime current, LocalTime target) {
        long minutesDiff = getMinutesDifference(current, target);
        return minutesDiff <= 60;
    }
    
    private long getMinutesDifference(LocalTime current, LocalTime target) {
        Duration duration = Duration.between(current, target);
        long minutesDiff = Math.abs(duration.toMinutes());
        // 자정을 넘어가는 경우도 고려 (예: 23:00과 00:30은 30분 차이)
        return Math.min(minutesDiff, 1440 - minutesDiff);
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
