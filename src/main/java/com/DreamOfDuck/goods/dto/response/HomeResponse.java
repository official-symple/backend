package com.DreamOfDuck.goods.dto.response;

import com.DreamOfDuck.account.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "home screen response")
public class HomeResponse {
    @Schema(example="박찬덕")
    String duckname;
    @Schema(example="3")
    Integer heart;
    @Schema(example="20")
    Integer dia;
    @Schema(example="50")
    Integer feather;
    @Schema(example="1")
    Integer lv;
    @Schema(example="2")
    Integer cntTalk;
    @Schema(example="20")
    Integer requiredFeather;

    public static HomeResponse from(Member member){
        return HomeResponse.builder()
                .duckname(member.getDuckname())
                .heart(member.getHeart())
                .dia(member.getDia())
                .feather(member.getFeather())
                .lv(member.getLv())
                .cntTalk(member.getCntTalk())
                .build();
    }
}
