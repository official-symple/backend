package com.DreamOfDuck.talk.entity;

import com.DreamOfDuck.global.exception.CustomException;
import com.DreamOfDuck.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LastEmotion {
    OPTION1(0, "그대로야"),
    OPTION2(1, "기분이 나아졌어"),
    OPTION3(2, "더 안 좋아졌어");
    private final Integer id;
    private final String text;

    public static LastEmotion fromId(int id){
        for(LastEmotion emotion : LastEmotion.values()){
            if(emotion.getId()==id){
                return emotion;
            }
        }
        throw new CustomException(ErrorCode.NOT_FOUND_EMOTION);
    }
}
