package com.DreamOfDuck.mind.service;

import com.DreamOfDuck.global.exception.CustomException;
import com.DreamOfDuck.global.exception.ErrorCode;

import java.time.LocalDateTime;
import java.time.LocalTime;

enum TimePeriod {
    DAY, NIGHT;

    static TimePeriod of(LocalDateTime time) {
        LocalTime t = time.toLocalTime();

        // 낮: 06:00 ~ 13:00
        if (!t.isBefore(LocalTime.of(6, 0)) && !t.isAfter(LocalTime.of(13, 0))) {
            return DAY;
        }

        // 밤: 18:00 ~ 23:59 || 00:00 ~ 04:00
        if (!t.isBefore(LocalTime.of(18, 0)) || !t.isAfter(LocalTime.of(4, 0))) {
            return NIGHT;
        }

        throw new CustomException(ErrorCode.IMPOSSIBLE_PERIOD);
    }
}
