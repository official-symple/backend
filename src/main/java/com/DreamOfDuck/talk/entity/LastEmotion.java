package com.DreamOfDuck.talk.entity;

import com.DreamOfDuck.global.exception.CustomException;
import com.DreamOfDuck.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LastEmotion {
    OPTION1(1, "그대로야"),
    OPTION2(2, "기분이 나아졌어"),
    OPTION3(3, "생각이 정리된 것 같아"),
    OPTION4(4, "기타(입력)");
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
