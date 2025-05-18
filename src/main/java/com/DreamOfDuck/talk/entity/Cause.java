package com.DreamOfDuck.talk.entity;

import com.DreamOfDuck.global.exception.CustomException;
import com.DreamOfDuck.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;

@Getter
@RequiredArgsConstructor
public enum Cause {
    CAUSE1(0, "인간관계"),
    CAUSE2(1, "돈"),
    CAUSE3(2, "진로"),
    CAUSE4(3, "학업"),
    CAUSE5(4, "건강"),
    CAUSE6(5, "가족"),
    CAUSE7(6, "연인"),
    CAUSE8(7, "사별");

    private final Integer id;
    private final String text;

    public static Cause fromId(Integer id){
        for(Cause cause : Cause.values()){
            if(cause.getId()==id){
                return cause;
            }
        }
        throw new CustomException(ErrorCode.NOT_FOUND_CAUSE);
    }

}
