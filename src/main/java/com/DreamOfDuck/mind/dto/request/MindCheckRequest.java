package com.DreamOfDuck.mind.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "mind check create request")
public class MindCheckRequest {
    @NotNull
    @Schema(example="true")
    boolean question1;
    @NotNull
    @Schema(example="true")
    boolean question2;
    @NotNull
    @Schema(example="true")
    boolean question3;
    @NotNull
    @Schema(example="0")
    Integer emotion;

}
