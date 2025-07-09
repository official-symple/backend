package com.DreamOfDuck.talk.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "last emotion request")
public class SessionUpdateRequest {
    @Schema(example="1")
    @NotNull
    Long sessionId;
    @Schema(
            example="1",
            description = "그대로야(1) or 기분이 나아졌어(2) or 생각이 정리된 것 같아(3)"
    )
    @NotNull(message="last_emotion은 필수 입력 값입니다.")
    Integer lastEmotion;

}
