package com.DreamOfDuck.account.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "duck name request")
public class DucknameRequest {
    @Schema(example="박찬덕")
    @NotNull
    String duckname;
}
