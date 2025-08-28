package com.DreamOfDuck.account.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "device token request")
public class TokenRequest {
    @Schema(example="dafedfioasdfjkcxi3")
    @NotNull
    String deviceToken;
}
