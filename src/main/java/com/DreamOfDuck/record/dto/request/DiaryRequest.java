package com.DreamOfDuck.record.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DiaryRequest {
    @Schema(example="배고팡파파라바라팡팡팡")
    @NotNull
    String diary;
}
