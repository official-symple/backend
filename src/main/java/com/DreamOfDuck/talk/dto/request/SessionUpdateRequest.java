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
            example="4",
            description = "4번인 경우만 input_field를 작성해주세요"
    )
    @NotNull(message="last_emotion은 필수 입력 값입니다.")
    Integer lastEmotion;
    @Schema(example="흠 잘 모르겠어")
    @Nullable
    String inputField; //last_emotion 4(기타)선택 시
}
