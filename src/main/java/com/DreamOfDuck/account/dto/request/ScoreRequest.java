package com.DreamOfDuck.account.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ScoreRequest {
    @Schema(example="1100")
    @NotNull
    Integer score;
}
