package com.DreamOfDuck.account.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "feather request")
public class FeatherRequest {
    @Schema(example="45")
    @NotNull
    Integer feather;
}
