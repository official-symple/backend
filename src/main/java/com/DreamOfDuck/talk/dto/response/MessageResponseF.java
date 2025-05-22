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
public class MessageResponseF {
    @Schema(example="ㅎㅇㅎㅇ")
    String content;

    public static MessageResponseF from(Message message) {
        return MessageResponseF.builder()
                .content(message.getContent())
                .build();
    }

}
