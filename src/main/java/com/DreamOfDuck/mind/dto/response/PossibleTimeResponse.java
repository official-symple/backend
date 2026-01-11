package com.DreamOfDuck.mind.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "possible time response")
public class PossibleTimeResponse {
    @Schema(example="true")
    boolean possibleTime;
}
