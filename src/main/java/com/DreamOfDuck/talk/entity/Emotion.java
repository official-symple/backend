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
    EMOTION1(0, "흥분돼"),
    EMOTION2(1, "행복해"),
    EMOTION3(2, "기뻐"),
    EMOTION4(3, "즐거워"),
    EMOTION5(4, "편안해"),
    EMOTION6(5, "만족스러워"),
    EMOTION7(6, "평온해"),
    EMOTION8(7, "긴장돼"),
    EMOTION9(8, "비참해"),
    EMOTION10(9, "슬퍼"),
    EMOTION11(10, "우울해"),
    EMOTION12(11, "지루해"),
    EMOTION13(12, "풀이 죽었어"),
    EMOTION14(13, "피곤해"),
    EMOTION15(14, "외로워"),
    EMOTION16(15, "후회돼"),
    EMOTION17(16, "죄책감 들어"),
    EMOTION18(17, "지쳐"),
    EMOTION19(18, "낙심했어"),
    EMOTION20(19, "괴로워"),
    EMOTION21(20, "귀찮아"),
    EMOTION22(21, "두려워"),
    EMOTION23(22, "화나"),
    EMOTION24(23, "깜짝 놀랐어"),
    EMOTION25(24, "언짢아"),
    EMOTION26(25, "불안해"),
    EMOTION27(26, "혼란스러워"),
    EMOTION28(27, "불쾌해");

    private final Integer id;
    private final String text;
    //성능개선 위해 map사용 -> O(1)
    private static final Map<Integer, Emotion> EMOTION_MAP = new HashMap<>();
    static {
        for (Emotion emotion : Emotion.values()) {
            EMOTION_MAP.put(emotion.getId(), emotion);
        }
    }

    public static Emotion fromId(Integer id) {
        Emotion emotion = EMOTION_MAP.get(id);
        if (emotion == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_EMOTION);
        }
        return emotion;
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
