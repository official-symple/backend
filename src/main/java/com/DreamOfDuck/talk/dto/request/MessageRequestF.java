package com.DreamOfDuck.talk.dto.request;

import com.DreamOfDuck.talk.dto.response.MessageResponseF;
import com.DreamOfDuck.talk.entity.Message;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "message create request")
public class MessageRequestF {

    @NotNull()
    Integer persona;

    @NotNull
    Boolean formal;

    @NotNull()
    List<MessageFormatF> messages;
}
