package com.DreamOfDuck.mind.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
@Schema(description = "mind check time request")
public class MindCheckTimeRequest {

    @Schema(example="null or monday")
    private String dayOfWeek;
    @NotNull
    @Schema(type="string", example="09:00:00")
    private LocalTime dayTime;
    @NotNull
    @Schema(type="string", example="21:00:00")
    private LocalTime nightTime;
}
