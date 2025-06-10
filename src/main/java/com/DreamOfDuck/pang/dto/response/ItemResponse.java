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
public class ItemResponse {
    @Schema(example="1")
    Long itemId;
    @Schema(example="110")
    Integer dia;
    @Schema(example="110")
    Integer feather;

    public static ItemResponse from(Item item) {
        return ItemResponse.builder()
                .itemId(item.getId())
                .dia(item.getDia())
                .feather(item.getFeather())
                .build();
    }
}
