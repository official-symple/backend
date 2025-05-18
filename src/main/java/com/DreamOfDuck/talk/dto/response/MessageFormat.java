package com.DreamOfDuck.talk.dto.response;

import com.DreamOfDuck.talk.entity.Message;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageFormat {
    @Schema(example="1")
    Long messageId;
    @Schema(example="1")
    Long sessionId;
    @Schema(
            example="1",
            description="유저=0, 꽥둥이=1, 꽥돌이=2, 꽥둑이=3"
    )
    Integer talker;
    @Schema(example="오리ㅎㅇ")
    String content;
    @Schema(example="2023-05-10T15:30:00")
    LocalDateTime time;
    public static MessageFormat from(Message message) {
        return MessageFormat.builder()
                .messageId(message.getMessageId())
                .sessionId(message.getSession().getId())
                .talker(message.getTalker().getValue())
                .content(message.getContent())
                .time(message.getCreatedAt())
                .build();
    }

}
