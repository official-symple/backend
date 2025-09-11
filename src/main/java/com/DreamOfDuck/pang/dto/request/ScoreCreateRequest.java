package com.DreamOfDuck.pang.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "score create request")
public class ScoreCreateRequest {
    @NotNull
    @Schema(example="7632")
    Long score;
}
