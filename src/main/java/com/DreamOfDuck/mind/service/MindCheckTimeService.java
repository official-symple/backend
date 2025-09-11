package com.DreamOfDuck.mind.service;

import com.DreamOfDuck.account.entity.Member;
import com.DreamOfDuck.mind.entity.MindCheckTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MindCheckTimeService {
    public MindCheckTime getMindCheckTime(Member member, DayOfWeek dayOfWeek) {
        return member.getMindCheckTimes().stream()
                .filter(time->time.getDayOfWeek()==dayOfWeek)
                .findFirst().orElse(null);
    }
}
