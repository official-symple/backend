package com.DreamOfDuck.record.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalTime;

@Data
@Schema(description = "health goal create request")
public class GoalCreateRequest {

    @Schema(example="screenTime")
    String healthType;
    @Schema(example="75")
    Integer value;

}
