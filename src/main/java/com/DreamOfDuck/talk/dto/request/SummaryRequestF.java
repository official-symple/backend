package com.DreamOfDuck.talk.dto.request;

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
@Schema(description = "summary create request")
public class SummaryRequestF {
    @NotNull
    Integer persona;

    Boolean formal;

    String language;

    List<String> emotion;

    String emotion_cause;

    @NotNull
    List<MessageFormatF> messages;

}
