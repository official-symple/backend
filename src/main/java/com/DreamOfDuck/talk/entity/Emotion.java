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
    EMOTION1(0, "그저 그런"),
    EMOTION2(1, "그리운"),
    EMOTION3(2, "시원섭섭한"),
    EMOTION4(3, "심심한"),
    EMOTION5(4, "무기력한"),
    EMOTION6(5, "무감한"),
    EMOTION7(6, "고민되는"),
    EMOTION8(7, "궁금한"),
    EMOTION9(8, "놀란"),
    EMOTION10(9, "혼란스러운"),
    EMOTION11(10, "신나는"),
    EMOTION12(11, "열정적인"),
    EMOTION13(12, "즐거운"),
    EMOTION14(13, "의욕적인"),
    EMOTION15(14, "상쾌한"),
    EMOTION16(15, "뿌듯한"),
    EMOTION17(16, "사랑하는"),
    EMOTION18(17, "행복한"),
    EMOTION19(18, "설레는"),
    EMOTION20(19, "감사한"),
    EMOTION21(20, "기대되는"),
    EMOTION22(21, "기쁜"),
    EMOTION23(22, "여유로운"),
    EMOTION24(23, "편안한"),
    EMOTION25(24, "나른한"),
    EMOTION26(25, "화나는"),
    EMOTION27(26, "두려운"),
    EMOTION28(27, "짜증나는"),
    EMOTION29(28, "긴장되는"),
    EMOTION30(29, "답답한"),
    EMOTION31(30, "불안한"),
    EMOTION32(31, "억울한"),
    EMOTION33(32, "후회되는"),
    EMOTION34(33, "괴로운"),
    EMOTION35(34, "외로운"),
    EMOTION36(35, "걱정되는"),
    EMOTION37(36, "실망한"),
    EMOTION38(37, "귀찮은"),
    EMOTION39(38, "피곤한"),
    EMOTION40(39, "공허한"),
    EMOTION41(40, "슬픈"),
    EMOTION42(41, "우울한"),
    EMOTION43(42, "지친");
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
}
