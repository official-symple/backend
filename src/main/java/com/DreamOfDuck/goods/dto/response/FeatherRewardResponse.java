package com.DreamOfDuck.goods.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "feather reward response")
public class FeatherRewardResponse {
    @Schema(example="10")
    Integer featherReward;
}
