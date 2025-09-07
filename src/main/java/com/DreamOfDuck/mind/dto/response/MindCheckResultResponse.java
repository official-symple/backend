package com.DreamOfDuck.mind.dto.response;

import com.DreamOfDuck.mind.entity.MindCheck;
import com.DreamOfDuck.mind.entity.PositiveEmotion;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "mind check result")
public class MindCheckResultResponse {
    @Schema(example="true")
    boolean question1;
    @Schema(example="true")
    boolean question2;
    @Schema(example="true")
    boolean question3;
    @Schema(example="흥분돼")
    int emotionId;
    @Schema(example="2025-09-06T17:30:45")
    LocalDateTime dateTime;

    public static MindCheckResultResponse of(MindCheck mindCheck){
        return MindCheckResultResponse.builder()
                .question1(mindCheck.isQuestion1())
                .question2(mindCheck.isQuestion2())
                .question3(mindCheck.isQuestion3())
                .emotionId(mindCheck.getPositiveEmotion()==null?
                        mindCheck.getNegativeEmotion().getId()
                        :mindCheck.getPositiveEmotion().getId())
                .dateTime(mindCheck.getCreateTime())
                .build();
    }
}
