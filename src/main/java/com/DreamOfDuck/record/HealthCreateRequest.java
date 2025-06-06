package com.DreamOfDuck.record;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Schema(description = "health record create request")
public class HealthCreateRequest {
    @Schema(example="5673")
    Integer walking;
    @Schema(example="75")
    Integer heartbeat;
    @Schema(example="07:45:00")
    LocalTime sleeping;
    @Schema(example="07:45:00")
    LocalTime screenTime;
    @Schema(example="12:00:00")
    LocalTime lightening;
    @Schema(example="오늘의 일기")
    String diary;
    @Schema(example="2025-06-05")
    @NotNull
    LocalDate date;
}
