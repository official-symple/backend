package com.DreamOfDuck.goods.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "dia request")
public class DiaRequest {
    @Schema(example="45", description = "추가/삭제할 다이아 갯수만큼 넣어주세요.")
    @NotNull
    Integer dia;
}