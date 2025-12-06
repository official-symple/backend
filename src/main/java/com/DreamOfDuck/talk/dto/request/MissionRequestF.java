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
    @NotNull
    Integer persona;

    @NotNull
    Boolean formal;

    String language;

    @NotNull
    String summary;

    @NotNull
    String nickname;

    String emotion_cause;
    List<String> emotion;

}
