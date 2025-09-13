package com.DreamOfDuck.goods.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "heart request")
public class HeartRequest {
    @Schema(example="45")
    @NotNull
    Integer heart;
}
