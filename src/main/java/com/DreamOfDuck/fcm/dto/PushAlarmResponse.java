package com.DreamOfDuck.fcm.dto;

import com.DreamOfDuck.fcm.entity.PushAlarm;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PushAlarmResponse {
    @Schema(example="true")
    boolean reminder;
    @Schema(example="true")
    boolean resultCheck;
    @Schema(example="true")
    boolean random;
    public static PushAlarmResponse from(PushAlarm pushAlarm) {
        return PushAlarmResponse.builder()
                .reminder(pushAlarm.isReminder())
                .resultCheck(pushAlarm.isResultCheck())
                .random(pushAlarm.isRandom())
                .build();
    }
}
