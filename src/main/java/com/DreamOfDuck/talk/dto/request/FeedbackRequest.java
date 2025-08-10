package com.DreamOfDuck.talk.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "feedback create request")
public class FeedbackRequest {
    @Schema(example="1")
    @NotNull
    Long sessionId;
    @Schema(example="버튼이 안 눌려요ㅠ")
    @NotNull(message="feedback은 필수 입력 값입니다.")
    String feedback;
}
