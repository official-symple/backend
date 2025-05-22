package com.DreamOfDuck.talk.dto.request;

import com.DreamOfDuck.talk.dto.response.MessageResponseF;
import com.DreamOfDuck.talk.entity.Message;
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
@Schema(description = "message create request")
public class MessageRequestF {
    @Schema(example="1")
    @NotNull()
    Integer duckType;
    @Schema(example="오리 ㅎㅇ")
    @NotNull(message="content는 필수 입력 값입니다.")
    String content;
}
