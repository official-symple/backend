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
public class AiCallbackSummaryRequest {
    @NotNull
    @Schema(description = "세션 ID", example = "123")
    private Long sessionId;

    @Schema(description = "문제 요약", example = "내가 맡은 모든 일들을 잘하고 싶은데 오점이 생겼다는 생각에 너무 화가 나")
    private String problem;

    @Schema(description = "해결 방안 목록")
    private List<String> solutions;
}
