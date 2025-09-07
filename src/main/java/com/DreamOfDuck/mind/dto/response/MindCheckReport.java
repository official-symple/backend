package com.DreamOfDuck.mind.dto.response;

import com.DreamOfDuck.mind.entity.MindChecks;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "mind check report response per day")
public class MindCheckReport {
    @Schema(example="2025-09-06")
    LocalDate date;
    @Schema(example="sat")
    String dayOfWeek;
    @Schema(example="위험")
    String result;
    MindCheckResultResponse dayResult;
    MindCheckResultResponse nightResult;
    public static MindCheckReport of(MindChecks mindChecks) {
        return MindCheckReport.builder()
                .date(mindChecks.getDate())
                .dayOfWeek(mindChecks.getDate().getDayOfWeek().toString().toLowerCase())
                .dayResult(mindChecks.getDayMindCheck()==null?null:MindCheckResultResponse.of(mindChecks.getDayMindCheck()))
                .nightResult(mindChecks.getNightMindCheck()==null?null:MindCheckResultResponse.of(mindChecks.getNightMindCheck()))
                .build();
    }
}
