package com.DreamOfDuck.pang.dto.response;

import com.DreamOfDuck.pang.entity.Item;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "item response")
public class ItemResponse {
    @Schema(example="2")
    Long tornado;
    @Schema(example="1")
    Long bubblePang;
    @Schema(example="4")
    Long breadCrumble;
    @Schema(example="4")
    Long grass;

    public static ItemResponse fromItem(Item item) {
        return ItemResponse.builder()
                .tornado(item.getTornado())
                .bubblePang(item.getBubblePang())
                .breadCrumble(item.getBreadCrumble())
                .grass(item.getGrass())
                .build();
    }
}
