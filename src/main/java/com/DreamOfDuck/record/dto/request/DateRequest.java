package com.DreamOfDuck.record.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DateRequest {
    @Schema(example="2025-06-06")
    @NotNull
    LocalDate date;
}
