package com.DreamOfDuck.talk.entity;

import com.DreamOfDuck.global.exception.CustomException;
import com.DreamOfDuck.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;

@Getter
@RequiredArgsConstructor
public enum Cause {
    CAUSE1(0, "가족"),
    CAUSE2(1, "반려동물"),
    CAUSE3(2, "건강"),
    CAUSE4(3, "돈"),
    CAUSE5(4, "연애"),
    CAUSE6(5, "우정"),
    CAUSE7(6, "직장"),
    CAUSE8(7, "진로,취업"),
    CAUSE9(8, "학업"),
    CAUSE10(9, "기타");

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
