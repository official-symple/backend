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
public class MissionRequestF {
    @Schema(description = "세션 ID (AI 서버 콜백용)")
    Long sessionId;

    @NotNull
    Integer persona;

    @NotNull
    Boolean formal;

    String language;

    String summary;  // Summary 의존성 있으므로 AI 서버에서 처리

    @NotNull
    String nickname;

    String emotion_cause;
    List<String> emotion;

}
