package com.DreamOfDuck.talk.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    @Schema(
            example = "{ \"messageId\": 1, \"sessionId\": 1, \"talker\": 0, \"content\": \"오리ㅎㅇ\", \"date\": \"2025-05-06\" }",
            description="talker : 유저=0, 꽥둥이=1, 꽥돌이=2, 꽥둑이=3"
    )
    MessageFormat request;

    @Schema(
            example = "{ \"messageId\": 2, \"sessionId\": 1, \"talker\": 1, \"content\": \"꽥\", \"date\": \"2025-05-06\" }",
            description="talker : 유저=0, 꽥둥이=1, 꽥돌이=2, 꽥둑이=3"
    )
    MessageFormat response;
    public static MessageResponse from(MessageFormat request, MessageFormat response) {
        return MessageResponse.builder()
                .request(request)
                .response(response)
                .build();
    }
}
