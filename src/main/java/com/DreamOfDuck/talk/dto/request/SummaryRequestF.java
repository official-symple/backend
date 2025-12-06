package com.DreamOfDuck.talk.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("persona")
    private Integer persona;

    @JsonProperty("formal")
    private Boolean formal;

    @JsonProperty("language")
    private String language;

    @JsonProperty("emotion")
    private List<String> emotion;

    // 2. [중요] 변수명은 Java 표준인 camelCase로 변경하고,
    // JSON 키값은 FastAPI가 원하는 snake_case("emotion_cause")로 매핑합니다.
    @JsonProperty("emotion_cause")
    private String emotionCause;

    @NotNull
    @JsonProperty("messages")
    private List<MessageFormatF> messages;

}
