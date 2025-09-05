package com.DreamOfDuck.mind.dto.response;

import com.DreamOfDuck.mind.entity.MindCheckTime;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "mind check response")
public class MindCheckTimeResponse {

    @Schema(example="null or monday")
    private String dayOfWeek;
    @NotNull
    @Schema(example="09:00:00")
    private LocalTime dayTime;
    @NotNull
    @Schema(example="21:00:00")
    private LocalTime nightTime;

    public static MindCheckTimeResponse of(MindCheckTime mindCheckTime) {
        return MindCheckTimeResponse.builder()
                .dayOfWeek(mindCheckTime.getDayOfWeek().toString().toLowerCase())
                .dayTime(mindCheckTime.getDayTime())
                .nightTime(mindCheckTime.getNightTime())
                .build();
    }
}
