package com.DreamOfDuck.record.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalTime;

@Data
@Schema(description = "health record update request")
public class HealthUpdateRequest {
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
}
