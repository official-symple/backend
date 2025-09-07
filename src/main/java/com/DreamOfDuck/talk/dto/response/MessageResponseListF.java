package com.DreamOfDuck.talk.dto.response;

import com.DreamOfDuck.talk.entity.Message;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponseListF {
    @Schema(example="[ㅎㅇㅎㅇ, 개 귀찮다]")
    List<String> content;

    public static MessageResponseListF from(Message message) {
        return MessageResponseListF.builder()
                .content(message.getContents())
                .build();
    }
}
