package com.DreamOfDuck.mind.entity;

import com.DreamOfDuck.global.exception.CustomException;
import com.DreamOfDuck.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum NegativeEmotion {
    NEGATIVE_EMOTIONEN1(0, "긴장돼"),
    NEGATIVE_EMOTION2(1, "비참해"),
    NEGATIVE_EMOTION3(2, "슬퍼"),
    NEGATIVE_EMOTION4(3, "우울해"),
    NEGATIVE_EMOTION5(4, "지루해"),
    NEGATIVE_EMOTION6(5, "풀이 죽었어"),
    NEGATIVE_EMOTION7(6, "피곤해"),
    NEGATIVE_EMOTION8(7, "외로워"),
    NEGATIVE_EMOTION9(8, "후회돼"),
    NEGATIVE_EMOTION10(9, "죄책감 들어"),
    NEGATIVE_EMOTION11(10, "지쳐"),
    NEGATIVE_EMOTION12(11, "낙심했어"),
    NEGATIVE_EMOTION13(12, "괴로워"),
    NEGATIVE_EMOTION14(13, "귀찮아"),
    NEGATIVE_EMOTION15(14, "두려워"),
    NEGATIVE_EMOTION16(15, "화나"),
    NEGATIVE_EMOTION17(16, "깜짝 놀랐어"),
    NEGATIVE_EMOTION18(17, "언짢아"),
    NEGATIVE_EMOTION19(18, "불안해"),
    NEGATIVE_EMOTION20(19, "혼란스러워"),
    NEGATIVE_EMOTION21(20, "불쾌해");
    private final Integer id;
    private final String text;
    //성능개선 위해 map사용 -> O(1)
    private static final Map<Integer, NegativeEmotion> NEGATIVE_EMOTION_MAP = new HashMap<>();
    static {
        for (NegativeEmotion emotion : NegativeEmotion.values()) {
            NEGATIVE_EMOTION_MAP.put(emotion.getId(), emotion);
        }
    }

    public static NegativeEmotion fromId(Integer id) {
        NegativeEmotion emotion = NEGATIVE_EMOTION_MAP.get(id);
        if (emotion == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_EMOTION);
        }
        return emotion;
    }
}
