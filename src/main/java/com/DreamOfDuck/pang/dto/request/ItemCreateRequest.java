package com.DreamOfDuck.pang.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemCreateRequest {
    @Schema(example="110")
    @NotNull
    Integer dia;

    @Schema(example="110")
    @NotNull
    Integer feather;

}
