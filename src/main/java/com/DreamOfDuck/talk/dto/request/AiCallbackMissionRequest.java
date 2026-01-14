package com.DreamOfDuck.talk.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiCallbackMissionRequest {
    @NotNull
    @Schema(description = "세션 ID", example = "123")
    private Long sessionId;

    @Schema(description = "미션 내용", example = "오늘 하루 동안 자신에게 칭찬 3가지 말해보기")
    private String mission;
}
