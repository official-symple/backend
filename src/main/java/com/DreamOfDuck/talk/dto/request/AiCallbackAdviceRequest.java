package com.DreamOfDuck.talk.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiCallbackAdviceRequest {
    @NotNull
    @Schema(description = "세션 ID", example = "123")
    private Long sessionId;

    @Schema(description = "조언 목록")
    private List<String> advice;
}
