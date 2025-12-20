package com.DreamOfDuck.account.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "check nickname duplication request")
public class NicknameRequest {
    @Schema(example="기미진")
    @NotNull
    String nickname;
}
