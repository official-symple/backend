package com.DreamOfDuck.record;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthResponse {
    @Schema(example="1")
    Long healthId;
    @Schema(example="5673")
    Integer walking;
    @Schema(example="07:45:00")
    LocalTime sleeping;
    @Schema(example="07:45:00")
    LocalTime screenTime;
    @Schema(example="diary")
    String diary;
    @Schema(example="2025-06-05")
    LocalDate date;

    public static HealthResponse from(Health health) {
        return HealthResponse.builder()
                .healthId(health.getId())
                .walking(health.getWalking())
                .sleeping(health.getSleeping())
                .screenTime(health.getScreenTime())
                .diary(health.getDiary())
                .date(health.getDate())
                .build();
    }
}
