package com.DreamOfDuck.account.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "login request")
public class LoginRequest {
    @Schema(example="dfadfafesdsaffaecwea")
    @NotNull
    private String accessToken;
}
