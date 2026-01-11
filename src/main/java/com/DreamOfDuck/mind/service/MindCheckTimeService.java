package com.DreamOfDuck.mind.service;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.global.exception.CustomException;
import com.DreamOfDuck.global.exception.ErrorCode;
import com.DreamOfDuck.mind.dto.request.MindCheckTimeRequest;
import com.DreamOfDuck.mind.dto.response.MindCheckTimeResponse;
import com.DreamOfDuck.mind.dto.response.PossibleTimeResponse;
import com.DreamOfDuck.mind.entity.MindCheckTime;
import com.DreamOfDuck.mind.repository.MindCheckTimeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

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
    public PossibleTimeResponse isPossibleTime(MindCheckTimeRequest request) {
        PossibleTimeResponse response = PossibleTimeResponse.builder().possibleTime(true).build();
        if (request.getDayTime().isBefore(LocalTime.of(6, 0)) || request.getDayTime().isAfter(LocalTime.of(13, 0))) {
            response.setPossibleTime(false);
        }
        LocalTime night = request.getNightTime();
        if (!((night.equals(LocalTime.of(18,0)) || night.isAfter(LocalTime.of(18,0)))
                || (night.isBefore(LocalTime.of(4,0)) || night.equals(LocalTime.of(4,0))))) {
            response.setPossibleTime(false);
        }
        return response;
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
