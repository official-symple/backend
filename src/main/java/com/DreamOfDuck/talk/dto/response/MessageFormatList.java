package com.DreamOfDuck.talk.dto.response;

import com.DreamOfDuck.talk.entity.Message;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageFormatList {
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
    List<String> content;
    @Schema(example="2023-05-10T15:30:00")
    LocalDateTime time;
    public static MessageFormatList from(Message message) {
        return MessageFormatList.builder()
                .messageId(message.getId())
                .sessionId(message.getSession().getId())
                .talker(message.getTalker().getValue())
                .content(message.getContents())
                .time(message.getCreatedAt())
                .build();
    }
}
