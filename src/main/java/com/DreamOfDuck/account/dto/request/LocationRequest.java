package com.DreamOfDuck.account.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "location request")
public class LocationRequest {
    @Schema(example="Asia/Seoul")
    @NotNull
    String location;
}
