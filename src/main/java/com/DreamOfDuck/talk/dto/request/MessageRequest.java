package com.DreamOfDuck.talk.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "message create request")
public class MessageRequest {
    @Schema(example="1")
    @NotNull(message="sessionId는 필수 입력 값입니다.")
    Long sessionId;
    @Schema(example="오리 ㅎㅇ")
    @NotNull(message="content는 필수 입력 값입니다.")
    String content;
}
