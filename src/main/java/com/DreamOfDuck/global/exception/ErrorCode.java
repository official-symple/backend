package com.DreamOfDuck.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // Common
    SERVER_UNTRACKED_ERROR(-100, "미등록 서버 에러입니다. 서버 팀에 연락주세요.", 500),
    OBJECT_NOT_FOUND(-101, "조회된 객체가 없습니다.", 406),
    INVALID_PARAMETER(-102, "잘못된 파라미터입니다.",422),
    PARAMETER_VALIDATION_ERROR(-103, "파라미터 검증 에러입니다.",422),
    PARAMETER_GRAMMAR_ERROR(-104, "파라미터 문법 에러입니다.",422),
    NOT_FOUND_AI_SERVER(-104, "AI 서버와 연결할 수 없습니다.", 500),

    //Auth
    UNAUTHORIZED(-200, "인증 자격이 없습니다.", 401),
    FORBIDDEN(-201, "권한이 없습니다.", 403),
    JWT_ERROR_TOKEN(-202, "잘못된 토큰입니다.", 401),
    JWT_EXPIRE_TOKEN(-203, "만료된 토큰입니다.", 401),
    AUTHORIZED_ERROR(-204, "인증 과정 중 에러가 발생했습니다.", 500),
    JWT_UNMATCHED_CLAIMS(-206, "토큰 인증 정보가 일치하지 않습니다", 401),
    NOT_REFRESH_TOKEN(-207, "리프레시 토큰이 아닙니다.", 401),

    // User
    USER_ALREADY_EXIST(-300, "이미 회원가입된 유저입니다.", 400),
    USER_NOT_EXIST(-301, "존재하지 않는 유저입니다.", 406),
    USER_WRONG_PASSWORD(-302, "비밀번호가 틀렸습니다.", 401),
    DO_SIGNUP_FIRST(-303, "회원가입을 먼저 해주세요.", 400),
    NINE_STATUS(-304, "9개의 status를 입력해주세요. ", 400),
    NICKNAME_LEN(-305, "닉네임 길이를 2자이상 14자 이하로 설정해주세요", 400),
    NICKNAME_EXIST(-306, "이미 존재하는 닉네임입니다.", 400),
    GOOGLE_REQUEST_TOKEN_ERROR(-307, "구글 토큰 에러, 401"),

    //Talk
    NOT_FOUND_CAUSE(-400, "해당 원인은 존재하지 않습니다.", 406),
    NOT_FOUND_EMOTION(-401, "해당 감정은 존재하지 않습니다.", 406),
    NOT_FOUND_DUCK(-402, "해당 오리는 존재하지 않습니다.", 406),
    EMPTY_INPUT_FIELD(-403, "기타란을 입력해 주세요.", 400),
    DIFFERENT_USER_SESSION(-404, "해당 유저는 해당 session Id에 접근할 수 없습니다.",403),
    NOT_FOUND_SESSION(-405, "해당 세션은 존재하지 않습니다.", 406),
    NOT_FOUND_MESSAGE(-406, "해당 메시지는 존재하지 않습니다.", 406),
    NOT_FOUND_INTERVIEW(-407, "해당 접수면접은 존재하지 않습니다.", 406),
    DIFFERENT_USER_INTERVIEW(-408, "해당 유저는 해당 인터뷰에 접근할 수 없습니다.",403),
    LAST_EMOTION_NOT_EXIST(-409, "마지막 감정 입력을 먼저 수행해 주세요.", 406),
    MISSION_ING(-410, "미션 생성 중이에요...ing", 406),
    ADVICE_ING(-411, "ADVICE 생성 중이에요..ing", 406),
    SUMMARY_ING(-412, "리포트 생성 중이에요..ing", 406),

    //Record
    RECORD_ALREADY_EXIST(-500, "해당 회원의 해당 날자 기록은 이미 존재합니다.", 400),
    NOT_FOUND_HEALTH(-501, "존재하지 않는 기록입니다.", 406),
    DIFFERENT_USER_HEALTH(-502, "해당 유저는 해당 health Id에 접근할 수 없습니다.",403),
    NOT_FOUND_GOAL(-503, "존재하지 않는 목표입니다.", 406),
    DIFFERENT_USER_GOAL(-504, "해당 유저는 해당 goal Id에 접근할 수 없습니다.", 403),
    GOAL_ALREADY_EXIST(-500, "해당 목표는 이미 존재합니다. 수정 API를 이용해주세요.", 400),

    //pang
    NOT_FOUND_ITEM(-600, "존재하지 않는 item입니다.", 406),
    DIFFERENT_USER_ITEM(-601, "해당 유저는 해당 item에 접근할 수 없습니다.",403),
    NOT_ENOUGH_ITEM(-601, "아이템이 부족합니다.", 400);
    private final int errorCode;
    private final String message;
    private final int httpCode;
}
