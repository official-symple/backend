package com.DreamOfDuck.fcm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "push alarm set request")
public class PushAlarmRequest {
    @NotNull
    @Schema(example="true")
    boolean reminder;
    @NotNull
    @Schema(example="true")
    boolean resultCheck;
    @NotNull
    @Schema(example="true")
    boolean random;
}
