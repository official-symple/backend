package com.DreamOfDuck.pang.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "score create request")
public class ItemUseRequest {
    @NotNull
    @Schema(example="2")
    Long tornado;
    @NotNull
    @Schema(example="1")
    Long bubblePang;
    @NotNull
    @Schema(example="4")
    Long breadCrumble;
    @NotNull
    @Schema(example="4")
    Long grass;
}
