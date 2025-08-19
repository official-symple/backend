package com.DreamOfDuck.pang.dto.response;

import com.DreamOfDuck.pang.entity.Score;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "score response")
public class ScoreResponse {
    @Schema(example="1")
    Long scoreId;
    @Schema(example="8963")
    Long score;
    @Schema(example="12")
    double percentile;
    @Schema(example="2452")
    Long rank;
    @Schema(example="241252")
    Long worldRecord;

    public static ScoreResponse from(Score score){
        return ScoreResponse.builder()
                .scoreId(score.getId())
                .score(score.getScore())
                .build();
    }

}
