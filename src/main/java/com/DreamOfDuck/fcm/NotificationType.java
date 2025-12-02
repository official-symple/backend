package com.DreamOfDuck.fcm;

public enum NotificationType {
    MORNING_START,      // 아침 8시 시작
    NIGHT_START,        // 밤 23시 시작
    MORNING_DEADLINE,   // 아침 마감 10분 전
    MORNING_MISSED,
    MORNING_COMPLETED,// 아침 마감 후 (미완료자)
    NIGHT_MISSED,       // 밤 마감 후 (미완료자)
    STREAK_REWARD       // 16시 스트릭 보상
}
