package com.DreamOfDuck.talk.entity;

import com.DreamOfDuck.global.exception.CustomException;
import com.DreamOfDuck.global.exception.ErrorCode;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Talker {
    USER(0), DUCK1(1), DUCK2(2), DUCK3(3);
    private final Integer value;

    public static Talker fromValue(Integer value) {
        for(Talker talker : Talker.values()) {
            if(talker.getValue().equals(value)) {
                return talker;
            }
        }
        throw new CustomException(ErrorCode.NOT_FOUND_DUCK);
    }
}
