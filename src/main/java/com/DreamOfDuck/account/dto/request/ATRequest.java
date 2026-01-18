package com.DreamOfDuck.account.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "access token request")
public class ATRequest {
    @Schema(example="dafedfioasdfjkcxi3")
    @NotNull
    String accessToken;
}
