package com.DreamOfDuck.record.dto.response;

import com.DreamOfDuck.record.entity.Goal;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalResponse {
    @Schema(example="1")
    @NotNull
    Long goalId;

    @Schema(example="true")
    Boolean isSuccess;
    @Schema(example="steps")
    String healthType;
    @Schema(example="75")
    Integer value;
    @Schema(example="\"2025-08-10T06:10:00Z\"")
    LocalDateTime updatedAt;
    public static GoalResponse from(Goal goal) {
        return GoalResponse.builder()
                .goalId(goal.getId())
                .isSuccess(goal.getIsSuccess())
                .healthType(goal.getHealthType().toString())
                .value(goal.getValue())
                .updatedAt(goal.getUpdatedAt())
                .build();
    }
}
