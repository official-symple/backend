package com.DreamOfDuck.account.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LoginRequest {
    @Schema(example="dfadfafesdsaffaecwea")
    private String accessToken;
}
