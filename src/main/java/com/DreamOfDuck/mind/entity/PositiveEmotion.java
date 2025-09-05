package com.DreamOfDuck.mind.entity;

import com.DreamOfDuck.global.exception.CustomException;
import com.DreamOfDuck.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum PositiveEmotion {
    POSITIVE_EMOTION1(0, "흥분돼"),
    POSITIVE_EMOTION2(1, "행복해"),
    POSITIVE_EMOTION3(2, "기뻐"),
    POSITIVE_EMOTION4(3, "즐거워"),
    POSITIVE_EMOTION5(4, "편안해"),
    POSITIVE_EMOTION6(5, "만족스러워"),
    POSITIVE_EMOTION7(6, "평온해");
    private final Integer id;
    private final String text;
    //성능개선 위해 map사용 -> O(1)
    private static final Map<Integer, PositiveEmotion> POSITIVE_EMOTION_MAP = new HashMap<>();
    static {
        for (PositiveEmotion emotion : PositiveEmotion.values()) {
            POSITIVE_EMOTION_MAP.put(emotion.getId(), emotion);
        }
    }

    public static PositiveEmotion fromId(Integer id) {
        PositiveEmotion emotion = POSITIVE_EMOTION_MAP.get(id);
        if (emotion == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_EMOTION);
        }
        return emotion;
    }
}
