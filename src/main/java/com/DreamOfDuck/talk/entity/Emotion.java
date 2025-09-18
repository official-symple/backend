package com.DreamOfDuck.talk.entity;

import com.DreamOfDuck.global.exception.CustomException;
import com.DreamOfDuck.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum Emotion {
    EMOTION1(0, "흥분돼", "excited"),
    EMOTION2(1, "행복해", "happy"),
    EMOTION3(2, "기뻐", "glad"),
    EMOTION4(3, "즐거워", "contented"),
    EMOTION5(4, "편안해", "calm"),
    EMOTION6(5, "만족스러워", "satisfied"),
    EMOTION7(6, "평온해", "relaxed"),
    EMOTION8(7, "긴장돼", "nervous"),
    EMOTION9(8, "비참해", "miserable"),
    EMOTION10(9, "슬퍼", "sad"),
    EMOTION11(10, "우울해", "depressed"),
    EMOTION12(11, "지루해", "bored"),
    EMOTION13(12, "풀이 죽었어", "discouraged"),
    EMOTION14(13, "피곤해", "sleepy"),
    EMOTION15(14, "외로워", "lonely"),
    EMOTION16(15, "후회돼", "regretful"),
    EMOTION17(16, "죄책감 들어", "guilty"),
    EMOTION18(17, "지쳐", "tired"),
    EMOTION19(18, "낙심했어", "discouraged"),
    EMOTION20(19, "괴로워", "lonely"),
    EMOTION21(20, "귀찮아", "annoyed"),
    EMOTION22(21, "두려워", "afraid"),
    EMOTION23(22, "화나", "angry"),
    EMOTION24(23, "깜짝 놀랐어", "surprised"),
    EMOTION25(24, "언짢아", "upset"),
    EMOTION26(25, "불안해", "anxious"),
    EMOTION27(26, "혼란스러워", "confused"),
    EMOTION28(27, "불쾌해", "unpleasant");

    private final Integer id;
    private final String text;
    private final String engText;
    public static Emotion fromId(Integer id) {
        for (Emotion emotion : Emotion.values()) {
            if (emotion.getId().equals(id)) {
                return emotion;
            }
        }
        throw new CustomException(ErrorCode.NOT_FOUND_EMOTION);
    }
    public static Emotion fromText(String text) {
        for (Emotion emotion : Emotion.values()) {
            if (emotion.getText().equals(text)) {
                return emotion;
            }
        }
        throw new CustomException(ErrorCode.NOT_FOUND_EMOTION);
    }
}
